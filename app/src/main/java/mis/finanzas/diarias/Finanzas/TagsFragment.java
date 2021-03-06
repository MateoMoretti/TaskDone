package mis.finanzas.diarias.Finanzas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
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

import mis.finanzas.diarias.DataBase;
import mis.finanzas.diarias.Preferences;
import com.example.taskdone.R;
import mis.finanzas.diarias.UsuarioSingleton;
import mis.finanzas.diarias.Utils;
import com.example.taskdone.databinding.FragmentTagsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsFragment extends Fragment {

    private FragmentTagsBinding binding;
    NavController navController;
    DataBase database;

    ArrayList<String> tags_actual = new ArrayList<>();

    ListaTagsAdapter adapter_tags;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTagsBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);


        if(Long.parseLong(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")) == R.id.principalFragment){
            String tags_concatenados = Preferences.getPreferenceString(requireContext(), "gasto_tags");
            if (!tags_concatenados.equals("")) {
                tags_actual.addAll(Arrays.asList(tags_concatenados.split(", ")));
            }
        }
        else if(Long.parseLong(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")) == R.id.historialFragment) {
            String tags_concatenados = Preferences.getPreferenceString(requireContext(), "edicion_gasto_tags");
            if (!tags_concatenados.equals("")) {
                tags_actual.addAll(Arrays.asList(tags_concatenados.split(", ")));
            }
        }

        database = new DataBase(requireContext());

        cargarTags();

        binding.volver.setOnClickListener(v -> volverAPrincipal(false));

        //Cambiar
        binding.buttonEliminar.setOnClickListener(v -> popupBorrarTags());

        binding.buttonCrearTag.setOnClickListener(v -> popupCrearTag());

        binding.buttonAceptar.setOnClickListener(v -> volverAPrincipal(true));


        binding.ayuda.setOnClickListener(v -> Utils.popupAyuda(requireContext(), requireActivity(), new ArrayList<>(Arrays
                .asList(getResources().getString(R.string.ayuda_tags_1), getResources().getString(R.string.ayuda_tags_2)))));

        return binding.getRoot();
    }


    private void popupBorrarTags() {
        if(tags_actual.isEmpty()){
            Toast.makeText(requireContext(), getResources().getString(R.string.sin_tags_seleccionados), Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(getResources().getString(R.string.eliminar));
            builder.setMessage(getResources().getString(R.string.estas_seguro_tags));

            DialogInterface.OnClickListener c = (dialogInterface, i) -> borrarTags();

            builder.setPositiveButton(getResources().getString(R.string.si), c);
            builder.setNegativeButton(getResources().getString(R.string.no), null);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void borrarTags(){
        boolean resultado = true;
        for(String s:tags_actual) {
            boolean result = database.deleteTagsByNombre(s);
            if(!result){
                resultado = false;
            }
        }
        if(resultado){
            Toast.makeText(requireContext(), getResources().getString(R.string.eliminado_exito), Toast.LENGTH_SHORT).show();

            if(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")) == R.id.principalFragment){
                Preferences.savePreferenceString(requireContext(), "","gasto_tags");
            }
            else if(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")) == R.id.historialFragment){
                Preferences.savePreferenceString(requireContext(), "","edicion_gasto_tags");
            }

            navController.navigate(R.id.tagsFragment);
        }
        else{
            Toast.makeText(requireContext(), getResources().getString(R.string.error_eliminado), Toast.LENGTH_SHORT).show();
        }
    }


    private void cargarTags(){
        Cursor tags = database.getTagsByUserId(UsuarioSingleton.getInstance().getID()); //Reemplazar por getTagsByUsuarioId si permito más cuentas en un futuro
        List<ItemTag> tags_nombre = new ArrayList<>();
        while (tags.moveToNext()) {
            ItemTag i = new ItemTag();
            i.nombre_tag = tags.getString(1);
            tags_nombre.add(i);
        }

        adapter_tags = new ListaTagsAdapter(tags_nombre, tags_actual, requireContext(), this);

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
            if(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")) == R.id.principalFragment){
                Preferences.savePreferenceString(requireContext(), Utils.arrayListToString(tags_actual), "gasto_tags");
            }
            else if(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")) == R.id.historialFragment){
                Preferences.savePreferenceString(requireContext(), Utils.arrayListToString(tags_actual), "edicion_gasto_tags");
            }
        }
        bundle.putBoolean("scroll_tags", true);
        navController.navigate(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")), bundle);
    }

    @SuppressLint("RtlHardcoded")
    public void popupEditarTag(String tag_viejo){

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View vista = inflater.inflate(R.layout.popup_crear_tag, null);

        EditText editar_tag = vista.findViewById(R.id.edit_crear_tag);
        editar_tag.setText(tag_viejo);

        builder.setTitle(getResources().getString(R.string.editar_tag));
        builder.setView(vista)
                .setPositiveButton(getResources().getString(R.string.aceptar), (dialog, which) ->
                        editarTag(tag_viejo, editar_tag.getText().toString())
                );
        builder.setNegativeButton(getResources().getString(R.string.cancelar), null);
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

    private void editarTag(String tag_viejo, String tag_nuevo){
        if(tag_nuevo.equals("")){
            Toast.makeText(requireContext(), R.string.tag_vacio, Toast.LENGTH_SHORT).show();
        }
        else {
            Cursor tag_existente = database.getTagByNombre(tag_nuevo);
            boolean tag_existe = false;
            while (tag_existente.moveToNext()){
                tag_existe = true;
            }
            if(tag_existe){
                Toast.makeText(requireContext(), R.string.error_tag_existente, Toast.LENGTH_SHORT).show();
            }
            else {
                boolean insertData = database.updateTag(tag_viejo, tag_nuevo);

                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
                }
                cargarTags();
            }
        }
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
            Cursor tag_existente = database.getTagByNombre(tag);
            boolean tag_existe = false;
            while (tag_existente.moveToNext()){
                tag_existe = true;
            }
            if(tag_existe){
                Toast.makeText(requireContext(), R.string.error_tag_existente, Toast.LENGTH_SHORT).show();
            }
            else {
                boolean insertData = database.addTag(tag);

                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT).show();
                }
                cargarTags();
            }
        }
    }
}