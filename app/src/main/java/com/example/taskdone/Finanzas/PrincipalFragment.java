package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.Utils;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class PrincipalFragment extends Fragment {

    private FragmentFinanzasPrincipalBinding binding;

    private String titulo;
    NavController navController;

    DataBase dataBase;
    ArrayList<String> tags;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasPrincipalBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        dataBase = new DataBase(requireContext());

        Cursor data = dataBase.getAllUsers();
        String info_cargada = "0";
        int pesos = 0;
        int dolares=0;
        int euros = 0;
        while (data.moveToNext()) {
            pesos = data.getInt(1);
            dolares = data.getInt(2);
            euros = data.getInt(3);
            info_cargada = data.getString(4);
        }
        if(info_cargada.equals("0")){
            navController.navigate(R.id.cargarDatosFinanzas);
        }


        binding.editFecha.setText(Utils.getFechaHoy());
        binding.pesos.setText("Pesos: $ "  + Utils.formatoCantidad(Integer.toString(pesos)));
        binding.dolares.setText("Dólares: U$D "  + Utils.formatoCantidad(Integer.toString(dolares)));
        binding.euros.setText("Euros: € " + Utils.formatoCantidad(Integer.toString(euros)));

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

        ArrayAdapter<CharSequence> adapter_tipo = ArrayAdapter.createFromResource(requireContext(),
                R.array.tipos_monedas, R.layout.spinner);
        adapter_tipo.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerTipo.setAdapter(adapter_tipo);

        binding.editFecha.setOnClickListener(v -> showDatePickerDialog());

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

        return binding.getRoot();
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
            boolean insertData = dataBase.addGastos(binding.editFecha.getText().toString(), binding.spinnerTipo.getSelectedItem().toString(), binding.editCantidad.getText().toString(), binding.editMotivo.getText().toString(), tags, ingreso);

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

        Cursor data = dataBase.getAllUsers();
        int pesos = 0;
        int dolares=0;
        int euros = 0;
        while (data.moveToNext()) {
            pesos = data.getInt(1);
            dolares = data.getInt(2);
            euros = data.getInt(3);
        }

        binding.pesos.setText("Pesos: $ "  + Utils.formatoCantidad(Integer.toString(pesos)));
        binding.dolares.setText("Dólares: U$D "  + Utils.formatoCantidad(Integer.toString(dolares)));
        binding.euros.setText("Euros: € " + Utils.formatoCantidad(Integer.toString(euros)));


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