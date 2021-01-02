package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.Model.Gasto;
import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentAgendaFisicoBinding;
import com.example.taskdone.databinding.FragmentFinanzasStatsBinding;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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

    ArrayList<Gasto> gastos_pesos = new ArrayList<>();
    ArrayList<Gasto> gastos_dolares = new ArrayList<>();
    ArrayList<Gasto> gastos_euros = new ArrayList<>();
    ArrayList<Gasto> ingresos_pesos = new ArrayList<>();
    ArrayList<Gasto> ingresos_dolares = new ArrayList<>();
    ArrayList<Gasto> ingresos_euros = new ArrayList<>();

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

        binding.calendar.setOnClickListener(v ->  abrir_popup_fechas());
        binding.fechaFiltro.setOnClickListener(v ->  abrir_popup_fechas());

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cargarStats(Calendar desde, Calendar hasta) throws ParseException {
        cleanLayouts();
        HashMap<String, Integer> total_tag_ingresos = new HashMap<>();
        HashMap<String, Integer> total_tag_gastos = new HashMap<>();

        Date desde_date = desde.getTime();
        Date hasta_date = hasta.getTime();

        cantidad_dias = Utils.diferenciaDeDias(desde, hasta);


        Cursor data = database.getAllGastos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        while(data.moveToNext()) {
            int id = data.getInt(0);
            String fecha = data.getString(1);
            String tipo_moneda = data.getString(2);
            String cantidad = data.getString(3);
            String motivo = data.getString(4);
            String ingreso = data.getString(5);

            Gasto g = new Gasto(fecha, tipo_moneda, cantidad, motivo, ingreso);
            all_gastos.add(g);

            Date fecha_gasto = sdf.parse(fecha);

            if ((desde_date.before(fecha_gasto)||desde_date.getDay()==fecha_gasto.getDay())  && (hasta_date.after(fecha_gasto) || hasta_date.getDay()==fecha_gasto.getDay())) {

                Cursor t_gasto = database.getTagsByGastoId(id);
                ArrayList<String> tags_asociados = new ArrayList();
                while (t_gasto.moveToNext()) {
                    tags_asociados.add(t_gasto.getString(1));
                }

                if (ingreso.equals("0")) {
                    //GUARDO ingreso+tag. Ejemplo:   0Comida (indica un gasto en comida)   1trabajo (indica ingreso por trabajo)
                    for(String t:tags_asociados) {
                        if (!t.equals("")) {
                            if (!total_tag_gastos.containsKey(ingreso + t)) {
                                total_tag_gastos.put(ingreso + t, Integer.parseInt(cantidad));
                            } else {
                                Integer a = total_tag_gastos.get(ingreso + t);
                                total_tag_gastos.replace(ingreso + t, total_tag_gastos.get(ingreso + t) + Integer.parseInt(cantidad));
                            }
                        }
                    }

                    switch (tipo_moneda) {
                        case "Pesos":
                            gastos_pesos.add(g);
                            break;
                        case "Dólares":
                            gastos_dolares.add(g);
                            break;
                        case "Euros":
                            gastos_euros.add(g);
                            break;
                    }
                } else {
                    //GUARDO ingreso+tag. Ejemplo:   0Comida (indica un gasto en comida)   1trabajo (indica ingreso por trabajo)
                    for(String t:tags_asociados) {
                        if(!t.equals("")){
                            if(!total_tag_ingresos.containsKey(ingreso+t)){
                                total_tag_ingresos.put(ingreso+t, Integer.parseInt(cantidad));
                            }
                            else {
                                total_tag_ingresos.replace(ingreso + t, total_tag_ingresos.get(ingreso + t) + Integer.parseInt(cantidad));
                            }
                        }
                    }

                    switch (tipo_moneda) {
                        case "Pesos":
                            ingresos_pesos.add(g);
                            break;
                        case "Dólares":
                            ingresos_dolares.add(g);
                            break;
                        case "Euros":
                            ingresos_euros.add(g);
                            break;
                    }
                }
            }
        }

            //AGREGO LINEAS DE GASTOS EJ:    PESOS  280 (TOTAL)   53,333 (PROMEDIO)

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if(gastos_pesos.size()>0 || gastos_dolares.size()>0 || gastos_euros.size()>0){
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView)view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView)view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutGastos.addView(view);

            agregarGastoIngreso("Pesos", binding.layoutGastos, gastos_pesos);
            agregarGastoIngreso("Dólares", binding.layoutGastos, gastos_dolares);
            agregarGastoIngreso("Euros", binding.layoutGastos, gastos_euros);
        }
        else{
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView)view.findViewById(R.id.tipo)).setText("");
            ((TextView)view.findViewById(R.id.promedio)).setText("");
            ((TextView)view.findViewById(R.id.total)).setText(getResources().getString(R.string.sin_gastos));
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutGastos.addView(view);
        }

        //LINEAS DE INGRESOS

        if(ingresos_pesos.size()>0 || ingresos_dolares.size()>0 || ingresos_euros.size()>0){
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView)view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView)view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutIngresos.addView(view);

            agregarGastoIngreso("Pesos", binding.layoutIngresos, ingresos_pesos);
            agregarGastoIngreso("Dólares", binding.layoutIngresos, ingresos_dolares);
            agregarGastoIngreso("Euros", binding.layoutIngresos, ingresos_euros);
        }
        else{
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView)view.findViewById(R.id.tipo)).setText("");
            ((TextView)view.findViewById(R.id.promedio)).setText("");
            ((TextView)view.findViewById(R.id.total)).setText(getResources().getString(R.string.sin_ingresos));
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            binding.layoutIngresos.addView(view);
        }

        //STATS AVANZADOS

        if(total_tag_gastos.size()>0 || total_tag_ingresos.size()>0) {

            //Titulo que diga que hay informacion de los tags
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) view.findViewById(R.id.total)).setText("Información sobre Tags");
            ((TextView) view.findViewById(R.id.promedio)).setText("");
            view.setPadding(0,0,0,15);
            binding.layoutAvanzado.addView(view);


            if (total_tag_gastos.size() > 0) {
                view = inflater.inflate(R.layout.item_stats, null);
                ((TextView) view.findViewById(R.id.tipo)).setText("Gastos");
                ((TextView) view.findViewById(R.id.tipo)).setTextColor(getResources().getColor(R.color.rojo_egreso));
                ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                binding.layoutAvanzado.addView(view);

                Iterator it = total_tag_gastos.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    view = inflater.inflate(R.layout.item_stats, null);
                    String t = pair.getKey().toString().substring(1,2).toUpperCase() + pair.getKey().toString().substring(2);
                    ((TextView) view.findViewById(R.id.tipo)).setText(t + ": ");
                    ((TextView) view.findViewById(R.id.total)).setText("$ " + Utils.formatoCantidad(pair.getValue().toString()));
                    float promedio = Float.parseFloat(pair.getValue().toString()) / cantidad_dias;
                    ((TextView) view.findViewById(R.id.promedio)).setText("$ " + Utils.formatoCantidad(promedio));
                    binding.layoutAvanzado.addView(view);
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
            if (total_tag_ingresos.size() > 0) {
                view = inflater.inflate(R.layout.item_stats, null);
                ((TextView) view.findViewById(R.id.tipo)).setText("Ingresos");
                ((TextView) view.findViewById(R.id.tipo)).setTextColor(getResources().getColor(R.color.verde_ingreso));
                ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
                ((TextView) view.findViewById(R.id.promedio)).setTypeface(Typeface.DEFAULT_BOLD);
                view.setPadding(0, 15, 0, 0);
                binding.layoutAvanzado.addView(view);

                Iterator it = total_tag_ingresos.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    view = inflater.inflate(R.layout.item_stats, null);
                    String t = pair.getKey().toString().substring(1,2).toUpperCase() + pair.getKey().toString().substring(2);
                    ((TextView) view.findViewById(R.id.tipo)).setText(t + ": ");
                    ((TextView) view.findViewById(R.id.total)).setText("$ " + Utils.formatoCantidad(pair.getValue().toString()));
                    float promedio = Float.parseFloat(pair.getValue().toString()) / cantidad_dias;
                    ((TextView) view.findViewById(R.id.promedio)).setText("$ " + Utils.formatoCantidad(promedio));
                    binding.layoutAvanzado.addView(view);
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
        else{
            //No hay informacion de los tags
            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView) view.findViewById(R.id.total)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) view.findViewById(R.id.total)).setText(getResources().getString(R.string.sin_info_sobre_tags));
            ((TextView) view.findViewById(R.id.promedio)).setText("");
            view.setPadding(0,0,0,10);
            binding.layoutAvanzado.addView(view);
        }
    }


    private void agregarGastoIngreso(String tipo, LinearLayout layout, ArrayList<Gasto> gasto_ingreso){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        if(gasto_ingreso.size()>0){
            int total = 0;
            for(Gasto g: gasto_ingreso){
                total += Integer.parseInt(g.getCantidad());
            }

            View view = inflater.inflate(R.layout.item_stats, null);
            ((TextView)view.findViewById(R.id.tipo)).setText(tipo + ": ");
            switch (tipo){
                case "Pesos":
                    ((TextView)view.findViewById(R.id.promedio)).setText("$ "+df.format((float)total/(float)cantidad_dias));
                    ((TextView)view.findViewById(R.id.total)).setText("$ "+df.format(total));
                    break;
                case "Dólares":
                    ((TextView)view.findViewById(R.id.promedio)).setText("U$D "+df.format((float)total/(float)cantidad_dias));
                    ((TextView)view.findViewById(R.id.total)).setText("U$D "+df.format(total));
                    break;
                case "Euros":
                    ((TextView)view.findViewById(R.id.promedio)).setText("€ "+df.format((float)total/(float)cantidad_dias));
                    ((TextView)view.findViewById(R.id.total)).setText("€ "+df.format(total));
                    break;
            }
            layout.addView(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void abrir_popup_fechas(){

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.popup_fecha_between, null);

        ImageView desde_button = view.findViewById(R.id.calendar_desde);
        ImageView hasta_button = view.findViewById(R.id.calendar_hasta);

        TextView desde_text = view.findViewById(R.id.text_desde);
        TextView hasta_text = view.findViewById(R.id.text_hasta);

        desde_text.setText(selected_date_desde);
        hasta_text.setText(selected_date_hasta);

        desde_button.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), desde_text));
        hasta_button.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), hasta_text));
        desde_text.setOnClickListener(v -> showDatePickerDialog(true, requireActivity(), desde_text));
        hasta_text.setOnClickListener(v -> showDatePickerDialog(false, requireActivity(), hasta_text));

        builder.setTitle("Filtrar");
        builder.setView(view)
                .setPositiveButton("Aceptar", (dialog, which) ->
                        {
                            try {
                                filtrar(desde_text.getText().toString(), hasta_text.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                );

        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER | Gravity.CENTER);
        }

        dialog.show();
        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filtrar(String desde, String hasta) throws ParseException {

        String text_desde = "Siempre";
        String text_hasta = "Hoy";
        String mensaje_filtro;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
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

        if(!desde.isEmpty() && !hasta.isEmpty()) {
            mensaje_filtro = text_desde + " - " + text_hasta;
        }

        else{
            if (!desde.isEmpty()) {
                mensaje_filtro = "Desde " + text_desde;
            }
            else if (!hasta.isEmpty()) {
                mensaje_filtro = "Hasta " + text_hasta;
            }
            else{
                mensaje_filtro = "Todo";
            }
        }

        binding.fechaFiltro.setText(mensaje_filtro);

        cargarStats(Utils.toCalendar(desde_date), Utils.toCalendar(hasta_date));
    }

    private void cleanLayouts(){
        cantidad_dias = 0L;
        gastos_pesos.clear();
        gastos_dolares.clear();
        gastos_euros.clear();

        ingresos_pesos.clear();
        ingresos_dolares.clear();
        ingresos_euros.clear();


        for(int x = 1; 1!= binding.layoutGastos.getChildCount(); x++) {
            binding.layoutGastos.removeViewAt(1);
        }
        for(int x = 1; 1!= binding.layoutIngresos.getChildCount(); x++) {
            binding.layoutIngresos.removeViewAt(1);
        }
        for(int x = 1; 1!= binding.layoutAvanzado.getChildCount(); x++) {
            binding.layoutAvanzado.removeViewAt(1);
        }
    }



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

        });
        newFragment.show(Objects.requireNonNull(activity).getSupportFragmentManager(), "datePicker");
    }


    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }


}