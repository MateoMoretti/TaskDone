package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskdone.DataBase;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentTagsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsFragment extends Fragment {

    private FragmentTagsBinding binding;
    NavController navController;
    DataBase database;

    ArrayList<String> tags_actual = new ArrayList<String>();

    ListaTagsAdapter adapter_tags;
    DataBase dataBase;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTagsBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);


        String tags_concatenados = Preferences.getPreferenceString(requireContext(), "gasto_tags");
        if(!tags_concatenados.equals("")){
            tags_actual.addAll(Arrays.asList(tags_concatenados.split(", ")));
        }

        database = new DataBase(requireContext());

        cargarTags();

        binding.volver.setOnClickListener(v -> volverAPrincipal(false));

        //Cambiar
        binding.buttonEliminar.setOnClickListener(v -> volverAPrincipal(false));

        binding.buttonCrearTag.setOnClickListener(v -> popupCrearTag());

        binding.buttonAceptar.setOnClickListener(v -> volverAPrincipal(true));

        return binding.getRoot();
    }

    private void cargarTags(){
        Cursor tags = database.getTagsByUserId(UsuarioSingleton.getInstance().getID()); //Reemplazar por getTagsByUsuarioId si permito más cuentas en un futuro
        List<ItemTag> tags_nombre = new ArrayList<>();
            while (tags.moveToNext()) {
            ItemTag i = new ItemTag();
            i.nombre_tag = tags.getString(1);
            tags_nombre.add(i);
        }

        adapter_tags = new ListaTagsAdapter(tags_nombre, tags_actual, requireContext());

        if(tags_nombre.isEmpty()){
            binding.txtSinTags.setVisibility(View.VISIBLE);
        }
        else{
            binding.txtSinTags.setVisibility(View.INVISIBLE);
        }
        binding.recyclerTags.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerTags.setAdapter(adapter_tags);
    }

    private void volverAPrincipal(boolean aceptar){
        Bundle bundle = new Bundle();

        if(aceptar) {
            Preferences.savePreferenceString(requireContext(), Utils.arrayListToString(tags_actual), "gasto_tags");
        }
        bundle.putBoolean("scroll_tags", true);
        navController.navigate(R.id.action_tagsFragment_to_principalFragment, bundle);
    }

    @SuppressLint("RtlHardcoded")
    private void popupCrearTag(){

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View vista = inflater.inflate(R.layout.popup_crear_tag, null);

        EditText crear_tag = vista.findViewById(R.id.edit_crear_tag);

        builder.setTitle(getResources().getString(R.string.crear_nuevo_tag));
        builder.setView(vista)
                .setPositiveButton(getResources().getString(R.string.aceptar), (dialog, which) ->
                        crearTag(crear_tag.getText().toString())
                );

        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER | Gravity.RIGHT);
        }

        dialog.show();
        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void crearTag(String tag){
        if(tag.equals("")){
            Toast.makeText(requireContext(), R.string.tag_vacio, Toast.LENGTH_SHORT).show();
        }
        else {
            dataBase = new DataBase(requireContext());
            boolean insertData = dataBase.addTag(tag);

            if (insertData) {
                Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
            }
            cargarTags();
        }
    }
}