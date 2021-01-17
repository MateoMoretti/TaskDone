package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.Model.Gasto;
import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentFinanzasStatsBinding;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class StatsFragment extends Fragment {

    private FragmentFinanzasStatsBinding binding;
    NavController navController;
    DataBase database;

    ArrayList<Gasto> all_gastos = new ArrayList<>();


    ArrayList<String> monedas = new ArrayList<>();
    ArrayList<String> simbolos = new ArrayList<>();

    ArrayList<ArrayList<Gasto>> gastos = new ArrayList<>();
    ArrayList<ArrayList<Gasto>> ingresos = new ArrayList<>();

    String selected_date_desde = "Siempre";
    String selected_date_hasta = "Hoy";

    Long cantidad_dias = 0L;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasStatsBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        database = new DataBase(requireContext());
        Calendar c= Calendar.getInstance();
        c.add(Calendar.DATE, -29);

        try {
            cargarStats(c, Calendar.getInstance());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        binding.tituloDesde.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), binding.textDesde));
        binding.tituloHasta.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), binding.textHasta));
        binding.calendarDesde.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), binding.textDesde));
        binding.calendarHasta.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), binding.textHasta));
        binding.textDesde.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), binding.textDesde));
        binding.textHasta.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), binding.textHasta));

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarStats(Calendar desde, Calendar hasta) throws ParseException {
        cleanLayouts();
        HashMap<String, Float> total_tag_ingresos = new HashMap<>();
        HashMap<String, Float> total_tag_gastos = new HashMap<>();

        cantidad_dias = Utils.diferenciaDeDias(desde, hasta);


        Cursor data = database.getGastosBySessionUser(Utils.calendarToString(desde), Utils.calendarToString(hasta));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        while (data.moveToNext()) {
            int id = data.getInt(0);
            String fecha = data.getString(1);
            Float total_gasto = data.getFloat(2);
            String motivo = data.getString(3);
            String ingreso = data.getString(4);
            String nombre_moneda = data.getString(5);
            String simbolo_moneda = data.getString(6);

            Gasto g = new Gasto(fecha, nombre_moneda, total_gasto, motivo, ingreso);
            all_gastos.add(g);
            if (!monedas.contains(g.getNombre_moneda())) {
                monedas.add(nombre_moneda);
                simbolos.add(simbolo_moneda);
            }
        }
/*

            Cursor t_gasto = database.getTagsByGastoId(id);
            ArrayList<String> tags_asociados = new ArrayList<String>();
            while (t_gasto.moveToNext()) {
                tags_asociados.add(t_gasto.getString(1));
            }

            //Tag gastos
            if (ingreso.equals("0")) {
                //ALMACENO ingreso+tag. Ejemplo:   0Comida (indica un gasto en comida)   1trabajo (indica ingreso por trabajo)
                for(String t:tags_asociados) {
                    if (!t.equals("")) {
                        boolean ya_agregado = false;
                        for(HashMap<String, Float> h:total_tag_gastos) {
                            if (h.containsKey(ingreso + t)) {
                                h.replace(ingreso + t, h.get(ingreso + t) + total_gasto);
                                ya_agregado = true;
                            }
                        }
                        if(!ya_agregado){
                            HashMap<String, Float> agregar = new HashMap<>();
                            agregar.put(ingreso + t, total_gasto);
                            total_tag_gastos.add()
                        }
                    }
                }
            }
            //Tag ingresos
            else {
                //ALMACENO ingreso+tag. Ejemplo:   0Comida (indica un gasto en comida)   1trabajo (indica ingreso por trabajo)
                for(String t:tags_asociados) {
                    if(!t.equals("")){
                        if(!total_tag_ingresos.containsKey(ingreso+t)){
                            total_tag_ingresos.put(ingreso+t, total_gasto);
                        }
                        else {
                            total_tag_ingresos.replace(ingreso + t, total_tag_ingresos.get(ingreso + t) + total_gasto);
                        }
                    }
                }
            }
        }
*/

        //AGREGO LINEAS DE GASTOS e INGRESOS

        Cursor total_gastos = database.getTotalGastosBetweenFechasGroupByMonedas(Utils.calendarToString(desde), Utils.calendarToString(hasta), "0");
        Cursor total_ingresos = database.getTotalGastosBetweenFechasGroupByMonedas(Utils.calendarToString(desde), Utils.calendarToString(hasta),"1");

        boolean hay_gastos = false;
        boolean hay_ingresos = false;
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        while (total_gastos.moveToNext()){
            hay_gastos = true;
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutGastos.addView(view);

            agregarGastoIngreso(total_gastos.getString(2), total_gastos.getFloat(1), total_gastos.getString(3), binding.layoutGastos);
        }
        total_gastos.moveToFirst();

        if(!hay_gastos){
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView)view.findViewById(R.id.tipo)).setText("");
            ((TextView)view.findViewById(R.id.promedio)).setText("");
            ((TextView)view.findViewById(R.id.total)).setText(getResources().getString(R.string.sin_gastos));
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutGastos.addView(view);
        }

        while (total_ingresos.moveToNext()){
            hay_ingresos = true;
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutIngresos.addView(view);

            agregarGastoIngreso(total_ingresos.getString(2), total_ingresos.getFloat(1), total_ingresos.getString(3), binding.layoutIngresos);
        }
        total_ingresos.moveToFirst();

        if(!hay_ingresos){
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView)view.findViewById(R.id.tipo)).setText("");
            ((TextView)view.findViewById(R.id.promedio)).setText("");
            ((TextView)view.findViewById(R.id.total)).setText(getResources().getString(R.string.sin_gastos));
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutIngresos.addView(view);
        }

        //STATS AVANZADOS


        Cursor total_by_tags = database.getTotalGastosGroupByTagIngresoMoneda(Utils.calendarToString(desde), Utils.calendarToString(hasta));

        boolean hay_tags = false;
        boolean hay_gasto_tag = false;
        boolean hay_ingreso_tag = false;

        //No hay informacion de los tags
        View view = inflater.inflate(R.layout.item_stats, null);
        ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
        ((TextView) view.findViewById(R.id.total)).setText(getResources().getString(R.string.sin_info_sobre_tags));
        ((TextView) view.findViewById(R.id.promedio)).setText("");
        view.setPadding(0,0,0,10);
        binding.layoutAvanzado.addView(view);

        while (total_by_tags.moveToNext()) {
            if (!hay_tags) {
                binding.layoutAvanzado.removeViewAt(1);

                //Titulo que diga que hay informacion de los tags
                view = inflater.inflate(R.layout.item_stats, null);
                ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                ((TextView) view.findViewById(R.id.total)).setText("Información sobre Tags");
                ((TextView) view.findViewById(R.id.promedio)).setText("");
                view.setPadding(0, 0, 0, 15);
                binding.layoutAvanzado.addView(view);
                hay_tags = true;
            }
            Float total = total_by_tags.getFloat(1);
            String nombre_moneda = total_by_tags.getString(2);
            String simbolo = total_by_tags.getString(3);
            String nombre_tag = total_by_tags.getString(4);
            String ingreso = total_by_tags.getString(5);

            if (ingreso.equals("0")) {
                if (!hay_gasto_tag) {
                    view = inflater.inflate(R.layout.item_stats, null);
                    ((TextView) view.findViewById(R.id.tipo)).setText("Gastos");
                    ((TextView) view.findViewById(R.id.tipo)).setTextColor(getResources().getColor(R.color.rojo_egreso));
                    ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                    ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                    binding.layoutAvanzado.addView(view);
                    hay_gasto_tag = true;
                }
                view = inflater.inflate(R.layout.item_stats, null);
                ((TextView) view.findViewById(R.id.tipo)).setText(nombre_tag + ": ");
                ((TextView) view.findViewById(R.id.total)).setText(simbolo + " " + Utils.formatoCantidad(total));
                ((TextView) view.findViewById(R.id.promedio)).setText(simbolo + " " + Utils.formatoCantidad(total / cantidad_dias));
                binding.layoutAvanzado.addView(view);
            } else {
                if (!hay_ingreso_tag) {
                    view = inflater.inflate(R.layout.item_stats, null);
                    ((TextView) view.findViewById(R.id.tipo)).setText("Ingresos");
                    ((TextView) view.findViewById(R.id.tipo)).setTextColor(getResources().getColor(R.color.verde_ingreso));
                    ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                    ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                    view.setPadding(0, 15, 0, 0);
                    binding.layoutAvanzado.addView(view);
                    hay_ingreso_tag = true;
                }
                view = inflater.inflate(R.layout.item_stats, null);
                ((TextView) view.findViewById(R.id.tipo)).setText(nombre_tag + ": ");
                ((TextView) view.findViewById(R.id.total)).setText(simbolo + " " + Utils.formatoCantidad(total));
                ((TextView) view.findViewById(R.id.promedio)).setText(simbolo + " " + Utils.formatoCantidad(total / cantidad_dias));
                binding.layoutAvanzado.addView(view);
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private void agregarGastoIngreso(String moneda, Float total, String simbolo, LinearLayout layout){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        View view = inflater.inflate(R.layout.item_stats, null);
        ((TextView)view.findViewById(R.id.tipo)).setText(moneda + ": ");
        ((TextView)view.findViewById(R.id.total)).setText(simbolo + " "+ Utils.formatoCantidad(total));
        ((TextView)view.findViewById(R.id.promedio)).setText(simbolo + " "+ Utils.formatoCantidad(total/cantidad_dias));
        layout.addView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filtrar(String desde, String hasta) throws ParseException {

        String text_desde = "Siempre";
        String text_hasta = "Hoy";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        Date desde_date = sdf.parse("1900/01/01");
        Date hasta_date = Calendar.getInstance().getTime();

        if (!desde.equals(text_desde)) {
            desde_date = sdf.parse(desde);
            text_desde = desde;
        }
        if (!hasta.equals(text_hasta)) {
            hasta_date = sdf.parse(hasta);
            text_hasta = hasta;
        }
        
        cargarStats(Utils.dateToCalendar(desde_date), Utils.dateToCalendar(hasta_date));
        binding.textDesde.setText(text_desde);
        binding.textHasta.setText(text_hasta);
    }

    private void cleanLayouts(){
        cantidad_dias = 0L;
        gastos.clear();
        ingresos.clear();

        binding.textDesde.setText("Siempre");
        binding.textHasta.setText("Hoy");


        while(binding.layoutGastos.getChildCount() != 1){
            binding.layoutGastos.removeViewAt(1);
        }
        for(int x = 1; 1!= binding.layoutIngresos.getChildCount(); x++) {
            binding.layoutIngresos.removeViewAt(1);
        }
        for(int x = 1; 1!= binding.layoutAvanzado.getChildCount(); x++) {
            binding.layoutAvanzado.removeViewAt(1);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog(Boolean es_desde, FragmentActivity activity, TextView fecha_a_actualizar) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = year + "/" + twoDigits(month + 1) + "/" + twoDigits(day);

            if(es_desde){
                fecha_a_actualizar.setText(selectedDate);
                selected_date_desde = selectedDate;
            }else{
                fecha_a_actualizar.setText(selectedDate);
                selected_date_hasta = selectedDate;
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


}