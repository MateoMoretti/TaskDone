package com.example.taskdone.Acceso;

import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentCrearCuentaBinding;

public class CrearCuentaFragment extends Fragment {

    private FragmentCrearCuentaBinding binding;
    NavController navController;
    DataBase database;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCrearCuentaBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        database = new DataBase(requireContext());

        binding.crearCuenta.setOnClickListener(v -> crearCuenta());

        binding.contrasena.setInputType(129);
        binding.verPassword.setOnClickListener(v -> alternarVisibilidadPassword());

        return binding.getRoot();
    }


    void crearCuenta() {
        Cursor data = database.getUserByUsername(binding.usuario.getText().toString());
        boolean existe = false;
        while (data.moveToNext()) {
            existe = true;
        }
        if (existe) {
            Toast.makeText(requireContext(), "Éste usuario ya existe", Toast.LENGTH_SHORT).show();
        } else {
            boolean result = database.addUser(binding.usuario.getText().toString(), binding.contrasena.getText().toString());
            if (result) {
                Toast.makeText(requireContext(), "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_crearCuentaFragment_to_loginFragment);
            } else {
                Toast.makeText(requireContext(), "Error al crear usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void alternarVisibilidadPassword(){
        if(binding.contrasena.getInputType() == 129){
            binding.contrasena.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else{
            binding.contrasena.setInputType(129);
        }
    }
}