package com.example.taskdone.Acceso;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentCrearCuentaBinding;

import java.util.ArrayList;
import java.util.Arrays;

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

        binding.volver.setOnClickListener(v -> navController.navigate(R.id.action_crearCuentaFragment_to_loginFragment));

        binding.ayuda.setOnClickListener(v -> Utils.popupAyuda(requireContext(), requireActivity(), new ArrayList<>(Arrays
                .asList(getResources().getString(R.string.ayuda_crear_cuenta_1), getResources().getString(R.string.ayuda_crear_cuenta_2)))));

        return binding.getRoot();
    }


    void crearCuenta() {
        Cursor data = database.getUserByUsername(binding.usuario.getText().toString());
        boolean existe = false;
        while (data.moveToNext()) {
            existe = true;
        }
        if (existe) {
            Toast.makeText(requireContext(), getResources().getString(R.string.error_usuario_existente), Toast.LENGTH_SHORT).show();
        } else {
            if(binding.usuario.getText().toString().equals("")) {
                Toast.makeText(requireContext(), getResources().getString(R.string.error_usuario_vacio), Toast.LENGTH_SHORT).show();
            }
            else {
                boolean result = database.addUser(binding.usuario.getText().toString(), binding.contrasena.getText().toString());
                if (result) {
                    Toast.makeText(requireContext(), getResources().getString(R.string.usuario_creado), Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_crearCuentaFragment_to_loginFragment);
                } else {
                    Toast.makeText(requireContext(), getResources().getString(R.string.error_crear_usuario), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @SuppressLint("RtlHardcoded")
    public void popupAyuda(){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View vista = inflater.inflate(R.layout.popup_informacion, null);

        TextView texto_1 = vista.findViewById(R.id.texto_1);
        TextView texto_2 = vista.findViewById(R.id.texto_2);
        texto_1.setText(getResources().getString(R.string.ayuda_crear_cuenta_1));
        texto_2.setText(getResources().getString(R.string.ayuda_crear_cuenta_2));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.informacion));
        builder.setView(vista)
                .setPositiveButton(getResources().getString(R.string.aceptar), (dialog, which) ->
                        dialog.dismiss()
                );
        builder.setCancelable(true);


        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER | Gravity.RIGHT);
        }

        dialog.show();
        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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