package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.databinding.FragmentCrearMonedaBinding;

import java.util.ArrayList;
import java.util.Objects;


public class CrearMonedaFragment extends Fragment {

    private FragmentCrearMonedaBinding binding;

    NavController navController;
    DataBase database;

    ArrayList<String> monedas = new ArrayList<>();
    ArrayList<Float> cantidades = new ArrayList<>();
    ArrayList<String> simbolos = new ArrayList<>();

    String fragment_anterior;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCrearMonedaBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        database = new DataBase(requireContext());

        fragment_anterior = Preferences.getPreferenceString(requireContext(), "id_fragment_anterior");

        binding.editCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificarCantidad(binding.editCantidad);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.volver.setOnClickListener(v-> navController.navigate(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior"))));


        binding.buttonCrear.setOnClickListener(v -> crearMoneda());

        cargarMonedas();

        return binding.getRoot();
    }

    private void verificarCantidad(EditText e){
            if (!e.getText().toString().equals("") && !e.getText().toString().equals("0")) {
                if (e.getText().toString().startsWith("0")) {
                    e.setText(e.getText().toString().substring(1));
                    e.setSelection(e.length());
                }
            }
    }

    public void crearMoneda() {
        String nombre_moneda_persistir = binding.editMoneda.getText().toString();
        String cantidad_elegida = binding.editCantidad.getText().toString();
        String simbolo_persistir = binding.editSimbolo.getText().toString();

        if(nombre_moneda_persistir.equals("")){
            Toast.makeText(requireContext(), R.string.moneda_nombre_vacio, Toast.LENGTH_SHORT).show();
        }
        else {
            Cursor data = database.getMonedaByNombre(nombre_moneda_persistir);
            boolean existe = false;
            while (data.moveToNext()) {
                existe = true;
            }
            if (existe) {
                Toast.makeText(requireContext(), getResources().getString(R.string.error_moneda_existente), Toast.LENGTH_SHORT).show();
            }
            else {
                int cantidad_persistir = 0;
                if (!cantidad_elegida.equals("")) {
                    cantidad_persistir = Integer.parseInt(cantidad_elegida);
                }
                boolean insertData = database.addMonedaCantidad(nombre_moneda_persistir, cantidad_persistir, simbolo_persistir);

                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
                }
                if(monedas.size()==0){
                    navController.navigate(R.id.principalFragment);
                }
                else {
                    monedas.clear();
                    binding.editMoneda.setText("");
                    binding.editSimbolo.setText("");
                    binding.editCantidad.setText("0");
                    cargarMonedas();
                }
            }
        }
    }


    public void cargarMonedas(){
        Cursor data = database.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        while (data.moveToNext()) {
            monedas.add(data.getString(1));
            cantidades.add(data.getFloat(2));
            simbolos.add(data.getString(3));
        }
        llenarLayoutMonedas();
    }

    @SuppressLint("SetTextI18n")
    private void llenarLayoutMonedas(){
        while(binding.layoutMonedas.getChildCount()!=0) {
            binding.layoutMonedas.removeViewAt(0);
        }
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (monedas.isEmpty()){
            binding.volver.setVisibility(View.INVISIBLE);
            binding.subtitulo.setVisibility(View.GONE);
            binding.layoutMonedas.setVisibility(View.GONE);
            Preferences.savePreferenceString(requireContext(), ""+R.id.loginFragment, "id_fragment_anterior");
        }
        else{
            binding.volver.setVisibility(View.VISIBLE);
            binding.subtitulo.setVisibility(View.VISIBLE);
            binding.layoutMonedas.setVisibility(View.VISIBLE);
            Preferences.savePreferenceString(requireContext(), fragment_anterior, "id_fragment_anterior");
            for(int x=0;x!=monedas.size();x++){
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_moneda_cantidad_editable, null);
                @SuppressLint("CutPasteId") Button editar = view.findViewById(R.id.editar);
                @SuppressLint("CutPasteId") TextView moneda = view.findViewById(R.id.moneda);
                @SuppressLint("CutPasteId") TextView simbolo = view.findViewById(R.id.simbolo);
                int index = x;
                editar.setOnClickListener(v -> popupEditarMoneda(monedas.get(index), simbolo.getText().toString()));
                moneda.setText(monedas.get(x));
                simbolo.setText(simbolos.get(x));
                binding.layoutMonedas.addView(view);
            }
        }
    }



    @SuppressLint("RtlHardcoded")
    private void popupEditarMoneda(String nombre, String simbolo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View vista = inflater.inflate(R.layout.popup_editar_moneda, null);

        EditText edit_nombre = vista.findViewById(R.id.edit_nombre);
        EditText edit_simbolo = vista.findViewById(R.id.edit_simbolo);
        edit_nombre.setText(nombre);
        edit_simbolo.setText(simbolo);


        builder.setTitle(getResources().getString(R.string.editar_moneda));
        builder.setView(vista)
                .setPositiveButton(getResources().getString(R.string.aceptar), (dialog, which) ->
                        editarMoneda(nombre, edit_nombre.getText().toString(), edit_simbolo.getText().toString())
                );
        builder.setNegativeButton(getResources().getString(R.string.cancelar), null);
        builder.setCancelable(true);


        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER | Gravity.RIGHT);
        }

        //Envio el dialog para cerrarlo si borra la moneda
        ImageView borrar = vista.findViewById(R.id.borrar);
        borrar.setOnClickListener(v -> popupBorrarMoneda(nombre, dialog));

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void editarMoneda(String nombre_viejo, String nombre, String simbolo){
        if(nombre.equals("")){
            Toast.makeText(requireContext(), R.string.moneda_nombre_vacio, Toast.LENGTH_SHORT).show();
        }
        else {
            if(!nombre_viejo.equals(nombre)) {
                Cursor data = database.getMonedaByNombre(nombre);
                boolean existe = false;
                while (data.moveToNext()) {
                    existe = true;
                }
                if (existe) {
                    Toast.makeText(requireContext(), getResources().getString(R.string.error_moneda_existente), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                boolean insertData = database.updateMoneda(nombre_viejo, nombre, simbolo);

                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
                }
                cleanYCargar();
            }
        }
    }


    private void popupBorrarMoneda(String nombre, Dialog dialog_viejo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.eliminar));
        builder.setMessage(getResources().getString(R.string.estas_seguro_monedas));

        DialogInterface.OnClickListener c = (dialogInterface, i) -> borrarMoneda(nombre, dialog_viejo);

        builder.setPositiveButton(getResources().getString(R.string.si), c);
        builder.setNegativeButton(getResources().getString(R.string.no), null);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void borrarMoneda(String nombre, Dialog dialog_viejo){
        boolean result = database.deleteMonedaByNombre(nombre);

        if(result){
            dialog_viejo.dismiss();
            Toast.makeText(requireContext(), getResources().getString(R.string.eliminado_exito), Toast.LENGTH_SHORT).show();
            cleanYCargar();
        }
        else{
            Toast.makeText(requireContext(), getResources().getString(R.string.error_eliminado), Toast.LENGTH_SHORT).show();
        }
    }

    private void cleanYCargar(){
        monedas.clear();
        simbolos.clear();
        cantidades.clear();
        cargarMonedas();
    }


}