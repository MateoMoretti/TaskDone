package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentFinanzasHistorialBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class HistorialFragment extends Fragment {

    private FragmentFinanzasHistorialBinding binding;

    NavController navController;

    String selected_date_desde;
    String selected_date_hasta;

    ArrayList<String> tags = new ArrayList<>();

    ArrayList<String> monedas = new ArrayList<>();
    ArrayList<Float> cantidades = new ArrayList<>();
    ArrayList<String> simbolos = new ArrayList<>();

    DataBase dataBase;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasHistorialBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);
        String selected_date_desde = Preferences.getPreferenceString(requireContext(), "historial_desde");
        String selected_date_hasta = Preferences.getPreferenceString(requireContext(), "historial_hasta");

        if(selected_date_desde.equals("")) {
            selected_date_desde = getResources().getString(R.string.comienzo_de_mes);
        }
        else if(Utils.isHoy(selected_date_desde)){
            selected_date_desde = getResources().getString(R.string.hoy);
        }


        if(selected_date_hasta.equals("") || Utils.isHoy(selected_date_hasta)) {
            selected_date_hasta = getResources().getString(R.string.hoy);
        }

        dataBase = new DataBase(requireContext());

        Cursor data = dataBase.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        while (data.moveToNext()) {
            monedas.add(data.getString(1));
            cantidades.add(data.getFloat(2));
            simbolos.add(data.getString(3));
        }

        try {
            filtrar(selected_date_desde, selected_date_hasta);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        binding.tituloDesde.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), binding.textDesde));
        binding.tituloHasta.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), binding.textHasta));
        binding.calendarDesde.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), binding.textDesde));
        binding.calendarHasta.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), binding.textHasta));
        binding.textDesde.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), binding.textDesde));
        binding.textHasta.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), binding.textHasta));


        if(!Preferences.getPreferenceString(requireContext(), "edicion_gasto_fecha").equals("")){
            ItemHistorial i = new ItemHistorial();
            irAEditarGasto(i);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void cargarHistorial(Date desde_date, Date hasta_date) {
        cleanHistorial();


        Cursor data = dataBase.getGastosBySessionUser(Utils.dateToString(desde_date), Utils.dateToString(hasta_date));

        ArrayList<ItemHistorial> data_items = new ArrayList<>();
        ArrayList<String> fechas = new ArrayList<>();


        while (data.moveToNext()) {
            ItemHistorial i = new ItemHistorial();
            i.id = data.getInt(0);;
            i.fecha = data.getString(1);
            i.cantidad_float = data.getFloat(2);
            i.cantidad = Utils.formatoCantidad(i.cantidad_float);
            i.motivo = data.getString(3);
            i.signo = data.getString(4);        //Llega como 0 | 1, que se transforma a - | +
            i.nombre_moneda = data.getString(5);
            i.simbolo = data.getString(6);

            if (i.signo.equals("0")) {
                i.signo = "-";
            } else {
                i.signo = "+";
            }
            if (!fechas.contains(i.fecha)) {
                fechas.add(i.fecha);
            }
            data_items.add(i);
        }

        //LLENO LOS TITULOS DE FECHAS

        if(fechas.size()==0){
            binding.sinDatos.setVisibility(View.VISIBLE);
        }
        else {
            binding.sinDatos.setVisibility(View.INVISIBLE);
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            for (int x = 0; x != fechas.size(); x++) {
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.elementos_historial, null);
                if(x==0){
                    view.findViewById(R.id.linea).setVisibility(View.INVISIBLE);
                }

                LinearLayout layout_items = view.findViewById(R.id.layout_item_historial);
                binding.layoutHistorial.addView(view);
                TextView fecha = view.findViewById(R.id.fecha);
                fecha.setText(Utils.getDia(fechas.get(x)));

                for (ItemHistorial i : data_items) {
                    if (i.fecha.equals(fechas.get(x))) {
                        @SuppressLint("InflateParams") View item = inflater.inflate(R.layout.item_historial, null);

                        if(i.signo.equals("-")) {
                            ((TextView)item.findViewById(R.id.signo_ingreso)).setTextColor(requireContext().getResources().getColor(R.color.rojo_egreso));
                            ((TextView)item.findViewById(R.id.txt_simbolo)).setTextColor(requireContext().getResources().getColor(R.color.rojo_egreso));
                            ((TextView)item.findViewById(R.id.txt_cantidad)).setTextColor(requireContext().getResources().getColor(R.color.rojo_egreso));
                        }
                        ((TextView)item.findViewById(R.id.signo_ingreso)).setText(i.signo);
                        ((TextView)item.findViewById(R.id.txt_simbolo)).setText(i.simbolo);
                        ((TextView)item.findViewById(R.id.txt_cantidad)).setText(i.cantidad);
                        ((TextView)item.findViewById(R.id.txt_motivo)).setText(i.motivo);

                        item.findViewById(R.id.editar).setOnClickListener(v-> irAEditarGasto(i));

                        layout_items.addView(item);
                    }
                }
            }
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    private void irAEditarGasto(ItemHistorial item){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.popup_edicion_gasto, null);

        if(Preferences.getPreferenceString(requireContext(), "edicion_gasto_id").equals("")) {
            Preferences.savePreferenceString(requireContext(), Integer.toString(item.id), "edicion_gasto_id");
        }

        if(!Preferences.getPreferenceString(requireContext(), "edicion_gasto_fecha").equals("")) {
            ((TextView)view.findViewById(R.id.edit_fecha)).setText(Preferences.getPreferenceString(requireContext(), "edicion_gasto_fecha"));
        }
        else {
            Preferences.savePreferenceString(requireContext(), item.fecha, "edicion_gasto_fecha");
            ((TextView) view.findViewById(R.id.edit_fecha)).setText(item.fecha);
        }
        ((TextView)view.findViewById(R.id.edit_fecha)).setOnClickListener(v -> showDatePickerDialog(((TextView)view.findViewById(R.id.edit_fecha))));


        if(!Preferences.getPreferenceString(requireContext(), "edicion_gasto_motivo").equals("")) {
            ((EditText)view.findViewById(R.id.edit_motivo)).setText(Preferences.getPreferenceString(requireContext(), "edicion_gasto_motivo"));
        }
        else {
            Preferences.savePreferenceString(requireContext(), item.motivo, "edicion_gasto_motivo");
            ((EditText) view.findViewById(R.id.edit_motivo)).setText(item.motivo);
        }
        ((EditText)view.findViewById(R.id.edit_motivo)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Preferences.savePreferenceString(requireContext(), ((EditText)view.findViewById(R.id.edit_motivo)).getText().toString(), "edicion_gasto_motivo");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        if(!Preferences.getPreferenceString(requireContext(), "edicion_gasto_cantidad").equals("")) {
            ((EditText)view.findViewById(R.id.edit_cantidad)).setText(Preferences.getPreferenceString(requireContext(), "edicion_gasto_cantidad"));
        }
        else {
            Preferences.savePreferenceString(requireContext(), item.cantidad, "edicion_gasto_cantidad");
            ((EditText) view.findViewById(R.id.edit_cantidad)).setText(item.cantidad);
        }
        ((EditText)view.findViewById(R.id.edit_cantidad)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificarCantidad(((EditText)view.findViewById(R.id.edit_cantidad)));
                Preferences.savePreferenceString(requireContext(), ((EditText)view.findViewById(R.id.edit_cantidad)).getText().toString(), "edicion_gasto_cantidad");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        String ingreso = Preferences.getPreferenceString(requireContext(), "edicion_gasto_ingreso");
        if(!ingreso.equals("")) {
            if(ingreso.equals("1")){
                ((CheckBox)view.findViewById(R.id.check_ingreso)).setChecked(true);
            }
        }
        else {
            if(item.signo.equals("+")){
                ((CheckBox)view.findViewById(R.id.check_ingreso)).setChecked(true);
                Preferences.savePreferenceString(requireContext(), "1", "edicion_gasto_ingreso");
            }
            else{
                Preferences.savePreferenceString(requireContext(), "0", "edicion_gasto_ingreso");
            }
        }

        ((CheckBox)view.findViewById(R.id.check_ingreso)).setOnCheckedChangeListener((compoundButton, b) -> guardarIngresoPendiente(b));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, monedas){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                ((TextView) v).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                return v;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        Spinner spinnerMoneda = ((Spinner)view.findViewById(R.id.spinner_moneda));
        spinnerMoneda.setAdapter(spinnerArrayAdapter);
        spinnerMoneda.setBackground(getResources().getDrawable(R.drawable.fondo_blanco_redondeado));
        spinnerMoneda.setGravity(Gravity.CENTER);
        spinnerMoneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Preferences.savePreferenceString(requireContext(), Integer.toString(spinnerMoneda.getSelectedItemPosition()), "edicion_gasto_moneda_index");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        String moneda_index = Preferences.getPreferenceString(requireContext(), "edicion_gasto_moneda_index");
        if(!moneda_index.equals("")){
            spinnerMoneda.setSelection(Integer.parseInt(moneda_index));
        }
        else {
            for (int x = 0; x != monedas.size(); x++) {
                if (monedas.get(x).equals(item.nombre_moneda)) {
                    spinnerMoneda.setSelection(x);
                }
            }
        }

        ((Button)view.findViewById(R.id.agregar_moneda)).setOnClickListener(v -> irACrearMoneda());

        ((Button)view.findViewById(R.id.agregar_tag)).setOnClickListener(v -> this.irATags());

        if(Preferences.getPreferenceString(requireContext(), "edicion_gasto_tags").equals("")) {
            DataBase database = new DataBase(requireContext());
            Cursor tags = database.getTagsByGastoId(item.id);
            boolean hay_tags = false;

            ArrayList<String> tags_seleccionados = new ArrayList<>();
            while (tags.moveToNext()) {
                hay_tags = true;
                tags_seleccionados.add(tags.getString(1));
            }
            ((TextView) view.findViewById(R.id.txt_tag_seleccionados)).setText(Utils.arrayListToString(tags_seleccionados));
            Preferences.savePreferenceString(requireContext(), Utils.arrayListToString(tags_seleccionados),"edicion_gasto_tags");
            if(!hay_tags){
                ((TextView) view.findViewById(R.id.txt_tag_seleccionados)).setText(getResources().getString(R.string.sin_seleccionados));
                Preferences.savePreferenceString(requireContext(), "","edicion_gasto_tags");
            }
        }
        else{
            StringBuilder tags_seleccionados = new StringBuilder();
            tags.addAll(Arrays.asList(Preferences.getPreferenceString(requireContext(), "edicion_gasto_tags").split(", ")));
            tags_seleccionados.append(Utils.arrayListToString(tags));
            ((TextView) view.findViewById(R.id.txt_tag_seleccionados)).setText(tags_seleccionados.toString());

        }

        ((ImageView) view.findViewById(R.id.cerrar)).setOnClickListener(v -> cerrarEdicion());

        ((ImageView) view.findViewById(R.id.borrar)).setOnClickListener(v -> popupCerrarSesion(view));

        ((Button) view.findViewById(R.id.ok)).setOnClickListener(v -> {
            try {
                guardarYCerrarEdicion(view);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        binding.constraintEdicion.addView(view);
    }


    private void popupCerrarSesion(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.eliminar));
        builder.setMessage(getResources().getString(R.string.estas_seguro));
        DialogInterface.OnClickListener c = (dialogInterface, i) -> {
            borrarGasto(view);
        };
        builder.setPositiveButton(getResources().getString(R.string.si), c);
        builder.setNegativeButton(getResources().getString(R.string.no), null);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void borrarGasto(View view){
        boolean result = dataBase.deleteGastoById(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "edicion_gasto_id")), Float.parseFloat(Preferences.getPreferenceString(requireContext(), "edicion_gasto_cantidad")), Preferences.getPreferenceString(requireContext(), "edicion_gasto_ingreso"), ((Spinner)view.findViewById(R.id.spinner_moneda)).getSelectedItem().toString());
        if(result){
            Toast.makeText(requireContext(), getResources().getString(R.string.eliminado_exito), Toast.LENGTH_SHORT).show();
            tags.clear();
            Preferences.deletePreferencesEdicionGasto(requireContext());
            navController.navigate(R.id.historialFragment);
        }
        else{
            Toast.makeText(requireContext(), getResources().getString(R.string.error_eliminado), Toast.LENGTH_SHORT).show();
        }

    }


    private void guardarYCerrarEdicion(View view) throws ParseException {
        this.editarGasto(view);
        tags.clear();
        Preferences.deletePreferencesEdicionGasto(requireContext());
        navController.navigate(R.id.historialFragment);
    }

    private void cerrarEdicion(){
        tags.clear();
        Preferences.deletePreferencesEdicionGasto(requireContext());
        binding.constraintEdicion.removeViewAt(0);
    }

    private void irACrearMoneda(){
        Preferences.savePreferenceString(requireContext(), ""+R.id.historialFragment, "id_fragment_anterior");
        navController.navigate(R.id.crearMonedaFragment);
    }

    void irATags() {
        Preferences.savePreferenceString(requireContext(), ""+R.id.historialFragment, "id_fragment_anterior");
        navController.navigate(R.id.tagsFragment);
    }

    void guardarIngresoPendiente(boolean b){
        Preferences.savePreferenceString(requireContext(), "0", "edicion_gasto_ingreso");
        if(b){
            Preferences.savePreferenceString(requireContext(), "1", "edicion_gasto_ingreso");
        }
    }

    private void showDatePickerDialog(TextView t) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {

                    String selectedDate = year + "/" + Utils.twoDigits(month + 1) + "/" + Utils.twoDigits(day);
                    final Calendar h = Calendar.getInstance();
                    h.set(year,month,day);
                    h.add(Calendar.DAY_OF_YEAR,1);
                    t.setText(selectedDate);
                    Preferences.savePreferenceString(requireContext(), t.getText().toString(), "edicion_gasto_fecha");
                }

        );
        newFragment.show(Objects.requireNonNull(requireActivity()).getSupportFragmentManager(), "datePicker");
    }


    private void verificarCantidad(EditText e){
        if (!e.getText().toString().equals("") && !e.getText().toString().equals("0")) {
            if (e.getText().toString().substring(0, 1).equals("0")) {
                e.setText(e.getText().toString().substring(1));
                e.setSelection(e.length());
            }
        }

    }

    private void cleanHistorial(){
        binding.textDesde.setText(getResources().getString(R.string.comienzo_de_mes));
        binding.textHasta.setText(getResources().getString(R.string.hoy));

        while(binding.layoutHistorial.getChildCount()!=0){
            binding.layoutHistorial.removeViewAt(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filtrar(String desde, String hasta) throws ParseException {
        String text_desde = getResources().getString(R.string.comienzo_de_mes);
        String text_hasta = getResources().getString(R.string.hoy);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        Date desde_date = sdf.parse(Utils.calendarToString(Utils.getPrimerDiaDelMes()));
        Date hasta_date = Calendar.getInstance().getTime();

        if (!desde.equals(text_desde)) {
            if(desde.equals(text_hasta)){
                text_desde = text_hasta;
                desde_date = hasta_date;
            }
            else {
                desde_date = sdf.parse(desde);
                text_desde = desde;
            }
        }
        if (!hasta.equals(text_hasta)) {
            hasta_date = sdf.parse(hasta);
            text_hasta = hasta;
        }
        cargarHistorial(desde_date, hasta_date);
        binding.textDesde.setText(text_desde);
        binding.textHasta.setText(text_hasta);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog(Boolean es_desde, FragmentActivity activity, TextView fecha_a_actualizar) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = year + "/" + twoDigits(month + 1) + "/" + twoDigits(day);

            if(es_desde){
                fecha_a_actualizar.setText(selectedDate);
                selected_date_desde = selectedDate;
                Preferences.savePreferenceString(requireContext(), selectedDate,"historial_desde");
                if(Utils.isHoy(selected_date_desde)){
                    fecha_a_actualizar.setText(getResources().getString(R.string.hoy));
                }
            }else{
                fecha_a_actualizar.setText(selectedDate);
                selected_date_hasta = selectedDate;
                Preferences.savePreferenceString(requireContext(), selectedDate,"historial_hasta");
                if(Utils.isHoy(selected_date_hasta)){
                    fecha_a_actualizar.setText(getResources().getString(R.string.hoy));
                }
            }
            try {
                filtrar(binding.textDesde.getText().toString(), binding.textHasta.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
        newFragment.show(Objects.requireNonNull(activity).getSupportFragmentManager(), "datePicker");
    }


    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    public void editarGasto(View view) throws ParseException {
        if (Utils.fechaMayorQueHoy(((TextView)view.findViewById(R.id.edit_fecha)).getText().toString())) {
            Toast.makeText(requireContext(), R.string.error_fecha_futura, Toast.LENGTH_SHORT).show();
        }
        else {
            if (((EditText)view.findViewById(R.id.edit_cantidad)).getText().toString().equals("") || ((EditText)view.findViewById(R.id.edit_cantidad)).getText().toString().equals("0")) {
                Toast.makeText(requireContext(), R.string.error_cantidad_null, Toast.LENGTH_SHORT).show();
            } else {
                String ingreso = "0";
                if (((CheckBox)view.findViewById(R.id.check_ingreso)).isChecked()) {
                    ingreso = "1";
                }
                String cantidad_gasto = (((EditText)view.findViewById(R.id.edit_cantidad)).getText().toString());
                boolean editado_exito = dataBase.editGasto(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "edicion_gasto_id")), ((TextView)view.findViewById(R.id.edit_fecha)).getText().toString(), ((Spinner)view.findViewById(R.id.spinner_moneda)).getSelectedItem().toString(), Utils.stringToFloat(cantidad_gasto).floatValue(), ((EditText)view.findViewById(R.id.edit_motivo)).getText().toString(), tags, ingreso);

                if (editado_exito) {
                    Toast.makeText(requireContext(), R.string.editado_exito, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.error_editado, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}