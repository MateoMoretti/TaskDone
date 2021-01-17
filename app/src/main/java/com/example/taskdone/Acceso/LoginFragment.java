package com.example.taskdone.Acceso;

import android.content.Intent;
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
import com.example.taskdone.Finanzas.ActivityFinanzas;
import com.example.taskdone.R;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    NavController navController;
    DataBase database;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);


        database = new DataBase(requireContext());

        binding.iniciaSesion.setOnClickListener(v -> iniciarSesion());

        binding.crearCuenta.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_crearCuentaFragment));

        binding.contrasena.setInputType(129);
        binding.verPassword.setOnClickListener(v -> alternarVisibilidadPassword());

        return binding.getRoot();
    }

    void iniciarSesion(){
        Cursor data = database.getUserByUsernameAndPassword(binding.usuario.getText().toString(), binding.contrasena.getText().toString());
        boolean permitido = false;
        while (data.moveToNext()) {
            UsuarioSingleton.getInstance().setID(data.getInt(0));
            UsuarioSingleton.getInstance().setUsername(data.getString(1));
            permitido = true;
        }

        if(permitido) {
            Intent i = new Intent(requireContext(), ActivityFinanzas.class);
            startActivity(i);
        }
        else{
            Toast.makeText(requireContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
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