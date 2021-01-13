package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentCrearMonedaBinding;


public class CrearMonedaFragment extends Fragment {

    private FragmentCrearMonedaBinding binding;

    NavController navController;
    DataBase dataBaseFinanzas;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCrearMonedaBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        dataBaseFinanzas = new DataBase(requireContext());

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

        binding.volver.setOnClickListener(v-> navController.navigate(R.id.principalFragment));


        binding.buttonCrear.setOnClickListener(v -> crearMoneda());

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

    public void crearMoneda() {
        String nombre_moneda_persistir = binding.editMoneda.getText().toString();
        String cantidad_elegida = binding.editCantidad.getText().toString();
        String simbolo_persistir = binding.editSimbolo.getText().toString();

        int cantidad_persistir =0;
        if(!cantidad_elegida.equals("")){
            cantidad_persistir = Integer.parseInt(cantidad_elegida);
        }
        boolean insertData = dataBaseFinanzas.addMonedaCantidad(nombre_moneda_persistir, cantidad_persistir, simbolo_persistir);

        if (insertData) {
            Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
        }

        actualizar();
    }

    private void actualizar(){

    }
}