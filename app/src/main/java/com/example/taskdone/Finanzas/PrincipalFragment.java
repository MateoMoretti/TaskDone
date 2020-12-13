package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.AdminFechas;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding;

import java.util.Calendar;
import java.util.Objects;


public class PrincipalFragment extends Fragment {

    private FragmentFinanzasPrincipalBinding binding;

    private String titulo;
    NavController navController;

    private TextView fecha;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasPrincipalBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        binding.titulo.setText(AdminFechas.getDiaHoy());
        binding.fecha.setText(AdminFechas.getFechaHoy());
        binding.editFecha.setText(AdminFechas.getFechaHoy());

        ArrayAdapter<CharSequence> adapter_tipo = ArrayAdapter.createFromResource(requireContext(),
                R.array.tipos_monedas, R.layout.spinner);
        adapter_tipo.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerTipo.setAdapter(adapter_tipo);

        binding.editFecha.setOnClickListener(v -> showDatePickerDialog());

        return binding.getRoot();
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {

            String selectedDate = year + "-" + AdminFechas.twoDigits(month + 1) + "-" + AdminFechas.twoDigits(day);
                if(selectedDate != null){
                    final Calendar h = Calendar.getInstance();
                    h.set(year,month,day);
                    h.add(Calendar.DAY_OF_YEAR,1);
                    int years = h.get(Calendar.YEAR);
                    int months = h.get(Calendar.MONTH);
                    int days = h.get(Calendar.DAY_OF_MONTH);
                }else{
                    selectedDate = year + "-" + AdminFechas.twoDigits(month + 1) + "-" + AdminFechas.twoDigits(day);
                }
            binding.editFecha.setText(selectedDate);
            }

        );
        newFragment.show(Objects.requireNonNull(requireActivity()).getSupportFragmentManager(), "datePicker");
    }
}