package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentFinanzasStatsBinding;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    String selected_date_desde;
    String selected_date_hasta;

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

        String selected_date_desde = Preferences.getPreferenceString(requireContext(), "stats_desde");
        String selected_date_hasta = Preferences.getPreferenceString(requireContext(), "stats_hasta");

        if(selected_date_desde.equals("")) {
            selected_date_desde = getResources().getString(R.string.comienzo_de_mes);
        }
        else if(Utils.isHoy(selected_date_desde)){
            selected_date_desde = getResources().getString(R.string.hoy);
        }

        if(selected_date_hasta.equals("") || Utils.isHoy(selected_date_hasta)) {
            selected_date_hasta = getResources().getString(R.string.hoy);
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

        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarStats(Calendar desde, Calendar hasta)  {
        cleanLayouts();

        cantidad_dias = Utils.diferenciaDeDias(desde, hasta);


        Cursor data = database.getGastosBySessionUser(Utils.calendarToString(desde), Utils.calendarToString(hasta));

        while (data.moveToNext()) {
            //int id_gasto = data.getInt(0);
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

        //AGREGO LINEAS DE GASTOS e INGRESOS

        Cursor total_gastos = database.getTotalGastosBetweenFechasGroupByMonedas(Utils.calendarToString(desde), Utils.calendarToString(hasta), "0");
        Cursor total_ingresos = database.getTotalGastosBetweenFechasGroupByMonedas(Utils.calendarToString(desde), Utils.calendarToString(hasta),"1");

        boolean hay_gastos = false;
        boolean hay_ingresos = false;
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        while (total_gastos.moveToNext()){
            if(!hay_gastos) {
                @SuppressLint("InflateParams") View view_tipo = inflater.inflate(R.layout.item_linea_simple, null);
                binding.layoutTipoGastos.addView(view_tipo);
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_stats, null);
                ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                binding.layoutGastos.addView(view);
                hay_gastos = true;
            }

            agregarGastoIngreso(total_gastos.getString(2), total_gastos.getFloat(1), total_gastos.getString(3), binding.layoutGastos, binding.layoutTipoGastos);
        }
        total_gastos.moveToFirst();

        if(!hay_gastos){
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_linea_simple, null);
            ((TextView)view.findViewById(R.id.texto)).setText(getResources().getString(R.string.sin_gastos));
            ((TextView) view.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutTituloGasto.addView(view);
        }

        while (total_ingresos.moveToNext()){
            if(!hay_ingresos) {
                hay_ingresos = true;
                @SuppressLint("InflateParams") View view_tipo = inflater.inflate(R.layout.item_linea_simple, null);
                binding.layoutTipoIngresos.addView(view_tipo);
                View view = inflater.inflate(R.layout.item_stats, null);
                ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                binding.layoutIngresos.addView(view);
            }

            agregarGastoIngreso(total_ingresos.getString(2), total_ingresos.getFloat(1), total_ingresos.getString(3), binding.layoutIngresos, binding.layoutTipoIngresos);
        }
        total_ingresos.moveToFirst();

        if(!hay_ingresos){
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_linea_simple, null);
            ((TextView)view.findViewById(R.id.texto)).setText(getResources().getString(R.string.sin_ingresos));
            ((TextView) view.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutTituloIngresos.addView(view);
        }

        //STATS AVANZADOS


        Cursor total_by_tags = database.getTotalGastosGroupByTagIngresoMoneda(Utils.calendarToString(desde), Utils.calendarToString(hasta));

        boolean hay_tags = false;
        boolean hay_gasto_tag = false;
        boolean hay_ingreso_tag = false;

        //No hay informacion de los tags
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_linea_simple, null);
        ((TextView) view.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
        ((TextView) view.findViewById(R.id.texto)).setText(getResources().getString(R.string.sin_info_sobre_tags));
        view.setPadding(0,0,0,10);
        binding.layoutTituloAvanzado.addView(view);

        while (total_by_tags.moveToNext()) {
            if (!hay_tags) {
                binding.layoutTituloAvanzado.removeViewAt(1);
                //Titulo que diga que hay informacion de los tags
                view = inflater.inflate(R.layout.item_linea_simple, null);
                ((TextView) view.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
                ((TextView) view.findViewById(R.id.texto)).setText(getResources().getString(R.string.informacion_sobre_tags));
                view.setPadding(0, 0, 0, 15);
                binding.layoutTituloAvanzado.addView(view);
                hay_tags = true;
            }
            float total = total_by_tags.getFloat(1);
            //String nombre_moneda = total_by_tags.getString(2);
            String simbolo = total_by_tags.getString(3);
            String nombre_tag = total_by_tags.getString(4);
            String ingreso = total_by_tags.getString(5);

            if (ingreso.equals("0")) {
                if (!hay_gasto_tag) {
                    @SuppressLint("InflateParams") View view_tipo = inflater.inflate(R.layout.item_linea_simple, null);
                    ((TextView) view_tipo.findViewById(R.id.texto)).setText(getResources().getString(R.string.gastos));
                    ((TextView) view_tipo.findViewById(R.id.texto)).setTextColor(getResources().getColor(R.color.rojo_egreso));
                    ((TextView) view_tipo.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
                    binding.layoutTipoAvanzado.addView(view_tipo);

                    view = inflater.inflate(R.layout.item_stats, null);
                    ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                    ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                    binding.layoutAvanzado.addView(view);
                    hay_gasto_tag = true;
                }
            } else {
                if (!hay_ingreso_tag) {
                    @SuppressLint("InflateParams") View view_tipo = inflater.inflate(R.layout.item_linea_simple, null);
                    ((TextView) view_tipo.findViewById(R.id.texto)).setText(getResources().getString(R.string.ingresos));
                    ((TextView) view_tipo.findViewById(R.id.texto)).setTextColor(getResources().getColor(R.color.verde_ingreso));
                    ((TextView) view_tipo.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
                    view_tipo.setPadding(0, 20, 0, 0);
                    binding.layoutTipoAvanzado.addView(view_tipo);

                    view = inflater.inflate(R.layout.item_stats, null);
                    ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                    ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                    view.setPadding(0, 20, 0, 0);
                    binding.layoutAvanzado.addView(view);
                    hay_ingreso_tag = true;
                }
            }
            @SuppressLint("InflateParams") View view_tipo = inflater.inflate(R.layout.item_linea_simple, null);
            ((TextView) view_tipo.findViewById(R.id.texto)).setText(nombre_tag + ": ");
            ((TextView) view_tipo.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutTipoAvanzado.addView(view_tipo);
            view = inflater.inflate(R.layout.item_stats, null);
            ((TextView) view.findViewById(R.id.total)).setText(simbolo + " " + Utils.formatoCantidad(total));
            ((TextView) view.findViewById(R.id.promedio)).setText(simbolo + " " + Utils.formatoCantidad(total / cantidad_dias));
            binding.layoutAvanzado.addView(view);
        }
    }


    @SuppressLint("SetTextI18n")
    private void agregarGastoIngreso(String moneda, Float total, String simbolo, LinearLayout layout, LinearLayout layout_tipo){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        @SuppressLint("InflateParams") View view_tipo = inflater.inflate(R.layout.item_linea_simple, null);
        ((TextView)view_tipo.findViewById(R.id.texto)).setText(moneda + ": ");
        ((TextView) view_tipo.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);
        layout_tipo.addView(view_tipo);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_stats, null);
        ((TextView)view.findViewById(R.id.total)).setText(simbolo + " "+ Utils.formatoCantidad(total));
        ((TextView)view.findViewById(R.id.promedio)).setText(simbolo + " "+ Utils.formatoCantidad(total/cantidad_dias));
        layout.addView(view);
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
        
        cargarStats(Utils.dateToCalendar(desde_date), Utils.dateToCalendar(hasta_date));
        binding.textDesde.setText(text_desde);
        binding.textHasta.setText(text_hasta);
    }

    private void cleanLayouts(){
        cantidad_dias = 0L;
        gastos.clear();
        ingresos.clear();

        binding.textDesde.setText(getResources().getString(R.string.comienzo_de_mes));
        binding.textHasta.setText(getResources().getString(R.string.hoy));

        while(binding.layoutTituloGasto.getChildCount() != 1){
            binding.layoutTituloGasto.removeViewAt(1);
        }
        while(binding.layoutTituloIngresos.getChildCount() != 1){
            binding.layoutTituloIngresos.removeViewAt(1);
        }
        while(binding.layoutTituloAvanzado.getChildCount() != 1){
            binding.layoutTituloAvanzado.removeViewAt(1);
        }

        while(binding.layoutGastos.getChildCount() != 0){
            binding.layoutGastos.removeViewAt(0);
            binding.layoutTipoGastos.removeViewAt(0);
        }
        while(binding.layoutIngresos.getChildCount() != 0){
            binding.layoutIngresos.removeViewAt(0);
            binding.layoutTipoIngresos.removeViewAt(0);
        }

        while(binding.layoutAvanzado.getChildCount() != 0){
            binding.layoutAvanzado.removeViewAt(0);
            binding.layoutTipoAvanzado.removeViewAt(0);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog(Boolean es_desde, FragmentActivity activity, TextView fecha_a_actualizar) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = year + "/" + twoDigits(month + 1) + "/" + twoDigits(day);

            if(es_desde){
                fecha_a_actualizar.setText(selectedDate);
                selected_date_desde = selectedDate;
                Preferences.savePreferenceString(requireContext(), selectedDate, "stats_desde");
                if(Utils.isHoy(selected_date_desde)){
                    fecha_a_actualizar.setText(getResources().getString(R.string.hoy));
                }
            }else{
                fecha_a_actualizar.setText(selectedDate);
                selected_date_hasta = selectedDate;
                Preferences.savePreferenceString(requireContext(), selectedDate, "stats_hasta");
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


}