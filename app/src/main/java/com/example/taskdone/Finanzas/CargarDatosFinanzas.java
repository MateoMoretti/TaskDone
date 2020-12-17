package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentCargarDatosFinanzasBinding;
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding;

import java.util.Calendar;
import java.util.Objects;


public class CargarDatosFinanzas extends Fragment {

    private FragmentCargarDatosFinanzasBinding binding;

    NavController navController;
    DataBaseFinanzas dataBaseFinanzas;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCargarDatosFinanzasBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        dataBaseFinanzas = new DataBaseFinanzas(requireContext());

        binding.editPesos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificarCantidad(binding.editPesos);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editDolares.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificarCantidad(binding.editDolares);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.editEuros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificarCantidad(binding.editEuros);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.ok.setOnClickListener(v -> addData());


        return binding.getRoot();
    }

    private boolean verificarCantidad(EditText e){
            if (!e.getText().toString().equals("") && !e.getText().toString().equals("0")) {
                if (e.getText().toString().substring(0, 1).equals("0")) {
                    e.setText(e.getText().toString().substring(1));
                    e.setSelection(e.length());
                }
            }
            return true;
    }

    public void addData() {
        String p = binding.editPesos.getText().toString();
        String d = binding.editDolares.getText().toString();
        String e = binding.editEuros.getText().toString();
        int pesos =0;
        int dolares =0;
        int euros =0;
        if(!p.equals("")){
            pesos = Integer.parseInt(p);
        }
        if(!d.equals("")){
            dolares = Integer.parseInt(d);
        }
        if(!e.equals("")){
            euros = Integer.parseInt(e);
        }

        boolean insertData = dataBaseFinanzas.addDataUser(pesos, dolares, euros);

        if (insertData) {
            Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
        }
        navController.navigate(R.id.principalFragment);

    }
}