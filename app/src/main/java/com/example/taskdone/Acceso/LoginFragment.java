package com.example.taskdone.Acceso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.Finanzas.ActivityFinanzas;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.databinding.FragmentLoginBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

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
        cargarIdioma();


        database = new DataBase(requireContext());

        binding.iniciaSesion.setOnClickListener(v -> iniciarSesion());

        binding.crearCuenta.setOnClickListener(v -> crearCuenta());

        binding.contrasena.setInputType(129);
        binding.verPassword.setOnClickListener(v -> alternarVisibilidadPassword());

        binding.idioma.setOnClickListener(v -> abrirIdiomas());

        return binding.getRoot();
    }

    void abrirIdiomas(){

        final String[] idiomas = {getResources().getString(R.string.espanol), getResources().getString(R.string.ingles)};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.elige_idioma));
        builder.setSingleChoiceItems(idiomas, -1, (dialogInterface, i) -> {
            switch (i){
                case 0:
                    setIdioma("");
                    requireActivity().recreate();
                    break;
                case 1:
                    setIdioma("en");
                    requireActivity().recreate();
                    break;
            }
            dialogInterface.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void setIdioma(String lenguaje){
        Locale locale = new Locale(lenguaje);
        if(lenguaje.equals("")){
            Locale.getDefault();
        }
        else {
            Locale.setDefault(locale);
        }
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        Preferences.savePreferenceString(requireContext(), lenguaje,"idioma");
    }

    void cargarIdioma(){
        String idioma = Preferences.getPreferenceString(requireContext(), "idioma");
        setIdioma(idioma);

    }

    void crearCuenta(){
        Preferences.savePreferenceString(requireContext(), ""+R.id.loginFragment, "id_fragment_anterior");
        navController.navigate(R.id.action_loginFragment_to_crearCuentaFragment);
    }

    void iniciarSesion(){
        Cursor data = database.getUserByUsernameAndPassword(binding.usuario.getText().toString(), binding.contrasena.getText().toString());
        boolean permitido = false;
        String password ="";
        while (data.moveToNext()) {
            UsuarioSingleton.getInstance().setID(data.getInt(0));
            UsuarioSingleton.getInstance().setUsername(data.getString(1));
            permitido = true;
        }

        if(permitido) {
            if(binding.mantenerSesion.isChecked()){
                Preferences.savePreferenceString(requireContext(),UsuarioSingleton.getInstance().getUsername(),"usuario");
                Preferences.savePreferenceString(requireContext(),password,"password");
            }
            Intent i = new Intent(requireContext(), ActivityFinanzas.class);
            startActivity(i);
        }
        else{
            Toast.makeText(requireContext(), getResources().getString(R.string.credenciales_incorrectas), Toast.LENGTH_SHORT).show();
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