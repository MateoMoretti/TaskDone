package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.Acceso.ActivityLogin;
import com.example.taskdone.DataBase;
import com.example.taskdone.Preferences;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.Utils;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;


@RequiresApi(api = Build.VERSION_CODES.O)
public class PrincipalFragment extends Fragment {

    private FragmentFinanzasPrincipalBinding binding;

    NavController navController;

    DataBase dataBase;
    ArrayList<String> tags;

    ArrayList<String> monedas = new ArrayList<>();
    ArrayList<Float> cantidades = new ArrayList<>();
    ArrayList<String> simbolos = new ArrayList<>();

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi"})
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasPrincipalBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        dataBase = new DataBase(requireContext());

        //Obtengo las monedas del usuario con sus cantidades y simbolos

        Cursor data = dataBase.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        while (data.moveToNext()) {
            monedas.add(data.getString(1));
            cantidades.add(data.getFloat(2));
            simbolos.add(data.getString(3));
        }
        if(monedas.isEmpty()){
            navController.navigate(R.id.crearMonedaFragment);
        }
        else{
            llenarLayoutMonedas();
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, monedas){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                ((TextView) v).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                return v;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerMoneda.setAdapter(spinnerArrayAdapter);
        binding.spinnerMoneda.setBackground(getResources().getDrawable(R.drawable.fondo_blanco_redondeado));
        binding.spinnerMoneda.setGravity(Gravity.CENTER);
        binding.spinnerMoneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Preferences.savePreferenceString(requireContext(), Integer.toString(binding.spinnerMoneda.getSelectedItemPosition()), "gasto_moneda_index");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.agregarTag.setOnClickListener(v -> this.irATags());

        dataBase = new DataBase(requireContext());

        binding.ok.setOnClickListener(v -> {
            try {
                addData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        tags = new ArrayList<>();

        if(getArguments() != null){
            if(getArguments().getString("scroll_tags") != null) {
                scrollToTag();
            }
        }

        binding.editFecha.setOnClickListener(v -> showDatePickerDialog());

        binding.agregarMoneda.setOnClickListener(v -> irACrearMoneda());

        binding.editCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificarCantidad();
                Preferences.savePreferenceString(requireContext(), binding.editCantidad.getText().toString(), "gasto_cantidad");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.editMotivo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Preferences.savePreferenceString(requireContext(), binding.editMotivo.getText().toString(), "gasto_motivo");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.checkIngreso.setOnCheckedChangeListener((compoundButton, b) -> guardarIngresoPendiente(b));

        cargarGastoPendiente();

        binding.logout.setOnClickListener(v -> popupCerrarSesion());

        binding.ayuda.setOnClickListener(v -> Utils.popupAyuda(requireContext(), requireActivity(), new ArrayList<>(Arrays.asList(getResources().getString(R.string.ayuda_principal_1), getResources().getString(R.string.ayuda_principal_2)))));

        return binding.getRoot();
    }

    void guardarIngresoPendiente(boolean b){
        Preferences.savePreferenceString(requireContext(), "0", "gasto_ingreso");
        if(b){
            Preferences.savePreferenceString(requireContext(), "1", "gasto_ingreso");
        }
    }

    private void irACrearMoneda(){
        Preferences.savePreferenceString(requireContext(), ""+R.id.principalFragment, "id_fragment_anterior");
        navController.navigate(R.id.crearMonedaFragment);
    }

    void irATags() {
        Preferences.savePreferenceString(requireContext(), ""+R.id.principalFragment, "id_fragment_anterior");
        navController.navigate(R.id.action_principalFragment_to_tagsFragment);
    }

    void cargarGastoPendiente(){
        String fecha = Preferences.getPreferenceString(requireContext(), "gasto_fecha");
        String moneda_index =Preferences.getPreferenceString(requireContext(), "gasto_moneda_index");
        String cantidad = Preferences.getPreferenceString(requireContext(), "gasto_cantidad");
        String motivo = Preferences.getPreferenceString(requireContext(), "gasto_motivo");
        String ingreso = Preferences.getPreferenceString(requireContext(), "gasto_ingreso");
        String tags_concatenados = Preferences.getPreferenceString(requireContext(), "gasto_tags");

        binding.editFecha.setText(Utils.calendarToString(Calendar.getInstance()));
        if(!fecha.equals("")) {
            binding.editFecha.setText(fecha);
        }
        if(!moneda_index.equals("")){
            binding.spinnerMoneda.setSelection(Integer.parseInt(moneda_index));
        }
        if(!cantidad.equals("")) {
            binding.editCantidad.setText(cantidad);
        }
        if(!motivo.equals("")) {
            binding.editMotivo.setText(motivo);
        }
        if(ingreso.equals("1")) {
            binding.checkIngreso.setChecked(true);
        }
        StringBuilder tags_seleccionados = new StringBuilder();
        if(!tags_concatenados.equals("")) {
            tags.addAll(Arrays.asList(tags_concatenados.split(", ")));
            tags_seleccionados.append(Utils.arrayListToString(tags));

        }
        else{
            tags_seleccionados.append(getResources().getString(R.string.sin_seleccionados));
        }
        binding.txtTagSeleccionados.setText(tags_seleccionados);
    }


    @SuppressLint("SetTextI18n")
    private void llenarLayoutMonedas(){
        while(binding.layoutMonedas.getChildCount()!=0) {
            binding.layoutMonedas.removeViewAt(0);
        }
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        for(int x=0;x!=monedas.size();x++){
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_moneda_cantidad, null);
            @SuppressLint("CutPasteId") TextView moneda = view.findViewById(R.id.moneda);
            @SuppressLint("CutPasteId") TextView cantidad = view.findViewById(R.id.cantidad);
            moneda.setText(monedas.get(x));
            cantidad.setText(simbolos.get(x)+" "+Utils.formatoCantidad(cantidades.get(x)));
            binding.layoutMonedas.addView(view);
        }
    }

    private void scrollToTag(){
        binding.scrollview.post(() -> binding.scrollview.fullScroll(ScrollView.FOCUS_DOWN));
    }


    private void verificarCantidad(){
            if (!binding.editCantidad.getText().toString().equals("") && !binding.editCantidad.getText().toString().equals("0")) {
                if (binding.editCantidad.getText().toString().startsWith("0")) {
                    binding.editCantidad.setText(binding.editCantidad.getText().toString().substring(1));
                    binding.editCantidad.setSelection(binding.editCantidad.length());
                }
            }

    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {

            String selectedDate = year + "/" + Utils.twoDigits(month + 1) + "/" + Utils.twoDigits(day);
            final Calendar h = Calendar.getInstance();
            h.set(year,month,day);
            h.add(Calendar.DAY_OF_YEAR,1);
            binding.editFecha.setText(selectedDate);
            Preferences.savePreferenceString(requireContext(), binding.editFecha.getText().toString(), "gasto_fecha");
            }

        );
        newFragment.show(Objects.requireNonNull(requireActivity()).getSupportFragmentManager(), "datePicker");
    }

    public void addData() throws ParseException {
        if (Utils.fechaMayorQueHoy(binding.editFecha.getText().toString())) {
            Toast.makeText(requireContext(), R.string.error_fecha_futura, Toast.LENGTH_SHORT).show();
        }
        else {
            if (binding.editCantidad.getText().toString().equals("") || binding.editCantidad.getText().toString().equals("0")) {
                Toast.makeText(requireContext(), R.string.error_cantidad_null, Toast.LENGTH_SHORT).show();
            } else {
                String ingreso = "0";
                if (binding.checkIngreso.isChecked()) {
                    ingreso = "1";
                }
                boolean insertData = dataBase.addGastos(binding.editFecha.getText().toString(), binding.spinnerMoneda.getSelectedItem().toString(), Float.parseFloat(binding.editCantidad.getText().toString()), binding.editMotivo.getText().toString(), tags, ingreso);

                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
                }
                cleanAndUpdate();
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void cleanAndUpdate(){
        Preferences.deletePreferencesGastoPendiente(requireContext());
        binding.spinnerMoneda.setSelection(0);
        binding.editCantidad.setText("0");
        binding.editMotivo.setText("");
        binding.checkIngreso.setChecked(false);
        binding.txtTagSeleccionados.setText(getResources().getString(R.string.sin_seleccionados));

        tags.clear();
        monedas.clear();
        cantidades.clear();

        Cursor data = dataBase.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        while (data.moveToNext()) {
            monedas.add(data.getString(1));
            cantidades.add(data.getFloat(2));
            simbolos.add(data.getString(3));
        }
        llenarLayoutMonedas();

        binding.scrollview.fullScroll(ScrollView.FOCUS_UP);

    }


    private void popupCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.salir));
        builder.setMessage(getResources().getString(R.string.quieres_cerrar_sesion));
        DialogInterface.OnClickListener c = (dialogInterface, i) -> {
            Preferences.deleteFiltros(requireContext());
            Preferences.deleteUser(requireContext());
            Preferences.deletePreferencesGastoPendiente(requireContext());
            Preferences.deletePreferencesEdicionGasto(requireContext());
            Intent intent = new Intent(requireContext(), ActivityLogin.class);
            startActivity(intent);
            requireActivity().finish();
        };
        builder.setPositiveButton(getResources().getString(R.string.si), c);
        builder.setNegativeButton(getResources().getString(R.string.no), null);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}