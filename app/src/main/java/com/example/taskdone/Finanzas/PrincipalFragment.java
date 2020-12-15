package com.example.taskdone.Finanzas;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.Utils;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding;

import java.util.Calendar;
import java.util.Objects;


public class PrincipalFragment extends Fragment {

    private FragmentFinanzasPrincipalBinding binding;

    private String titulo;
    NavController navController;

    private TextView fecha;
    DataBaseFinanzas dataBaseFinanzas;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasPrincipalBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        boolean info_cargada = Preferences.getPreferenceBoolean(requireContext(), Preferences.PREFERENCE_INFO_CARGADA);

        if(!info_cargada){
            Preferences.savePreferenceBoolean(requireContext(), true, Preferences.PREFERENCE_INFO_CARGADA);
            Preferences.savePreferenceString(requireContext(), "56850", Preferences.PREFERENCE_PESOS);
            Preferences.savePreferenceString(requireContext(), "300", Preferences.PREFERENCE_DOLARES);
            Preferences.savePreferenceString(requireContext(), "0", Preferences.PREFERENCE_EUROS);
        }


        binding.titulo.setText(Utils.getDiaHoy());
        binding.fecha.setText(Utils.getFechaHoy());

        binding.editFecha.setText(Utils.getFechaHoy());
        binding.pesos.setText("Pesos: $ "  + Preferences.getPreferenceString(requireContext(), Preferences.PREFERENCE_PESOS));
        binding.dolares.setText("Dólares: U$D "  + Preferences.getPreferenceString(requireContext(), Preferences.PREFERENCE_DOLARES));
        binding.euros.setText("Euros: € " + Preferences.getPreferenceString(requireContext(), Preferences.PREFERENCE_EUROS));

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

        dataBaseFinanzas = new DataBaseFinanzas(requireContext());

        binding.ok.setOnClickListener(v -> addData());


        return binding.getRoot();
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
            String escencial = "0";
            String ingreso = "0";
            if (binding.checkEscencial.isChecked()) {
                escencial = "1";
            }
            if (binding.checkIngreso.isChecked()) {
                ingreso = "1";
            }
            boolean insertData = dataBaseFinanzas.addData(binding.fecha.getText().toString(), binding.spinnerTipo.getSelectedItem().toString(), binding.editCantidad.getText().toString(), binding.editMotivo.getText().toString(), escencial, ingreso);

            if (insertData) {
                Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
            }
            cleanAndUpdate();
        }
    }

    private void cleanAndUpdate(){
        binding.editFecha.setText(Utils.getFechaHoy());
        binding.spinnerTipo.setSelection(0);
        binding.editCantidad.setText("0");
        binding.editMotivo.setText("");
        binding.checkEscencial.setChecked(false);
        binding.checkIngreso.setChecked(false);

        binding.editFecha.setText(Utils.getFechaHoy());
        binding.pesos.setText("Pesos: $ "  + Preferences.getPreferenceString(requireContext(), Preferences.PREFERENCE_PESOS));
        binding.dolares.setText("Dólares: U$D "  + Preferences.getPreferenceString(requireContext(), Preferences.PREFERENCE_DOLARES));
        binding.euros.setText("Euros: € " + Preferences.getPreferenceString(requireContext(), Preferences.PREFERENCE_EUROS));

        binding.scrollview.fullScroll(ScrollView.FOCUS_UP);

    }
}