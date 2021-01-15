package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.Utils;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


@RequiresApi(api = Build.VERSION_CODES.O)
public class PrincipalFragment extends Fragment {

    private FragmentFinanzasPrincipalBinding binding;

    private String titulo;
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

        Integer ad= UsuarioSingleton.getInstance().getID();
        Cursor data = dataBase.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        while (data.moveToNext()) {
            int id = data.getInt(0);
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

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, monedas){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                ((TextView) v).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                return v;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerTipo.setAdapter(spinnerArrayAdapter);
        binding.spinnerTipo.setBackground(getResources().getDrawable(R.drawable.fondo_blanco_redondeado));
        binding.spinnerTipo.setGravity(Gravity.CENTER);
        binding.agregarTag.setOnClickListener(v -> this.seleccionarTags());

        dataBase = new DataBase(requireContext());

        binding.ok.setOnClickListener(v -> addData());

        tags = new ArrayList<>();

        if(getArguments() != null){
            if(getArguments().getStringArrayList("tags") != null){
                tags = getArguments().getStringArrayList("tags");
            }
            binding.txtTagSeleccionados.setText(tags.size() + " seleccionados");
            binding.editFecha.setText(getArguments().getString("fecha"));
            binding.spinnerTipo.setSelection(Integer.parseInt(getArguments().getString("tipo_moneda")));
            binding.editCantidad.setText(getArguments().getString("cantidad"));
            binding.editMotivo.setText(getArguments().getString("motivo"));
            binding.checkIngreso.setChecked(getArguments().getBoolean("ingreso"));

            scrollToTag();

        }

        binding.editFecha.setOnClickListener(v -> showDatePickerDialog());
        binding.editFecha.setText(Utils.getFechaHoy());

        binding.agregarMoneda.setOnClickListener(v -> navController.navigate(R.id.crearMonedaFragment));

        binding.editCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificarCantidad();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void llenarLayoutMonedas(){
        binding.layoutMonedas.removeViewAt(0);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        for(int x=0;x!=monedas.size();x++){
            View view = inflater.inflate(R.layout.item_moneda_cantidad, null);
            @SuppressLint("CutPasteId") TextView moneda = view.findViewById(R.id.moneda);
            @SuppressLint("CutPasteId") TextView cantidad = view.findViewById(R.id.cantidad);
            moneda.setText(monedas.get(x) );
            cantidad.setText(simbolos.get(x)+cantidades.get(x));
            binding.layoutMonedas.addView(view);
        }
    }

    private final void scrollToTag(){
        binding.scrollview.post(() -> binding.scrollview.fullScroll(ScrollView.FOCUS_DOWN));
    }


    private boolean verificarCantidad(){
            if (!binding.editCantidad.getText().toString().equals("") && !binding.editCantidad.getText().toString().equals("0")) {
                if (binding.editCantidad.getText().toString().substring(0, 1).equals("0")) {
                    binding.editCantidad.setText(binding.editCantidad.getText().toString().substring(1));
                    binding.editCantidad.setSelection(binding.editCantidad.length());
                }
            }
            return true;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {

            String selectedDate = year + "-" + Utils.twoDigits(month + 1) + "-" + Utils.twoDigits(day);
                if(selectedDate != null){
                    final Calendar h = Calendar.getInstance();
                    h.set(year,month,day);
                    h.add(Calendar.DAY_OF_YEAR,1);
                    int years = h.get(Calendar.YEAR);
                    int months = h.get(Calendar.MONTH);
                    int days = h.get(Calendar.DAY_OF_MONTH);
                }else{
                    selectedDate = year + "-" + Utils.twoDigits(month + 1) + "-" + Utils.twoDigits(day);
                }
            binding.editFecha.setText(selectedDate);
            }

        );
        newFragment.show(Objects.requireNonNull(requireActivity()).getSupportFragmentManager(), "datePicker");
    }

    public void addData() {
        if (binding.editCantidad.getText().toString().equals("") || binding.editCantidad.getText().toString().equals("0")) {
            Toast.makeText(requireContext(), R.string.error_cantidad_null, Toast.LENGTH_SHORT).show();
        } else {
            String ingreso = "0";
            if (binding.checkIngreso.isChecked()) {
                ingreso = "1";
            }
            boolean insertData = dataBase.addGastos(binding.editFecha.getText().toString(), binding.spinnerTipo.getSelectedItem().toString(), binding.editCantidad.getText().toString(), binding.editMotivo.getText().toString(), tags, ingreso, UsuarioSingleton.getInstance().getID());

            if (insertData) {
                Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
            }
            cleanAndUpdate();
        }
    }

    @SuppressLint("SetTextI18n")
    private void cleanAndUpdate(){
        binding.spinnerTipo.setSelection(0);
        binding.editCantidad.setText("0");
        binding.editMotivo.setText("");
        binding.checkIngreso.setChecked(false);
        binding.txtTagSeleccionados.setText("0 seleccionados");

        tags.clear();
        monedas.clear();
        cantidades.clear();

        Cursor data = dataBase.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        while (data.moveToNext()) {
            monedas.add(data.getString(1));
            cantidades.add(data.getFloat(2));
        }

        binding.scrollview.fullScroll(ScrollView.FOCUS_UP);

    }



    void seleccionarTags() {
        Bundle bundle = new Bundle();
        bundle.putString("fecha", binding.editFecha.getText().toString());
        bundle.putString("tipo_moneda", Integer.toString(binding.spinnerTipo.getSelectedItemPosition()));
        bundle.putString("cantidad", binding.editCantidad.getText().toString());
        bundle.putString("motivo", binding.editMotivo.getText().toString());
        bundle.putStringArrayList("tags", tags);
        bundle.putBoolean("ingreso", binding.checkIngreso.isChecked());

        navController.navigate(R.id.action_principalFragment_to_tagsFragment, bundle);

    }









}