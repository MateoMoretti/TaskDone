package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.DataBase;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.Model.Gasto;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.UsuarioSingleton;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentStatsAvanzadosBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class StatsAvanzadosFragment extends Fragment {

    private FragmentStatsAvanzadosBinding binding;
    NavController navController;
    DataBase database;

    boolean[] tags_checkeados;
    ArrayList<String> tags_creados_por_usuario = new ArrayList<>();
    ArrayList<String> tags_seleccionados  = new ArrayList<>();

    ArrayList<Integer> tags_seleccionados_index = new ArrayList<>();


    ArrayList<String> monedas = new ArrayList<>();
    ArrayList<Float> cantidades = new ArrayList<>();
    ArrayList<String> simbolos = new ArrayList<>();

    int moneda_seleccionada = 0;
    String year_seleccionado = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
    String mes_seleccionado = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1);

    //Se utiliza para que los spinners no ejecuten OnClickListener al iniciar el fragment
    int check_for_spinners = 0;


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStatsAvanzadosBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        database = new DataBase(requireContext());

        Cursor data = database.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        while (data.moveToNext()) {
            monedas.add(data.getString(1));
            cantidades.add(data.getFloat(2));
            simbolos.add(data.getString(3));
        }

        if(!monedas.isEmpty()){
            graficoBarrasGastosMensuales(year_seleccionado, "0", binding.chartGastosMensuales, monedas.get(0));
            graficoBarrasGastosMensuales(year_seleccionado, "1", binding.chartIngresosMensuales, monedas.get(0));
            graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "0", binding.piechartGastosTag,  monedas.get(0));
            graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "1", binding.piechartIngresosTag,  monedas.get(0));
        }

        ArrayAdapter<String> adapterSpinnerMonedas = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, monedas){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                return v;
            }
        };
        adapterSpinnerMonedas.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerMoneda.setAdapter(adapterSpinnerMonedas);
        binding.spinnerMoneda.setBackground(getResources().getDrawable(R.drawable.fondo_blanco_redondeado));
        binding.spinnerMoneda.setGravity(Gravity.CENTER);
        binding.spinnerMoneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(check_for_spinners > 0) {
                    moneda_seleccionada = i;
                    if (!monedas.isEmpty()) {
                        graficoBarrasGastosMensuales(year_seleccionado, "0", binding.chartGastosMensuales, monedas.get(moneda_seleccionada));
                        graficoBarrasGastosMensuales(year_seleccionado, "1", binding.chartIngresosMensuales, monedas.get(moneda_seleccionada));
                        graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "0", binding.piechartGastosTag, monedas.get(moneda_seleccionada));
                        graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "1", binding.piechartIngresosTag, monedas.get(moneda_seleccionada));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayList<String> years = new ArrayList<>();
        for(int x=2021; x!=Calendar.getInstance().get(Calendar.YEAR)+1 ;x++){
            years.add(Integer.toString(x));
        }
        ArrayAdapter<String> adapterSpinnerYear = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, years){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                return v;
            }
        };
        adapterSpinnerYear.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerYear.setAdapter(adapterSpinnerYear);
        binding.spinnerYear.setBackground(getResources().getDrawable(R.drawable.fondo_blanco_redondeado));
        binding.spinnerYear.setGravity(Gravity.CENTER);
        binding.spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(check_for_spinners > 0) {
                    year_seleccionado = binding.spinnerYear.getSelectedItem().toString();
                    if (!monedas.isEmpty()) {
                        graficoBarrasGastosMensuales(year_seleccionado, "0", binding.chartGastosMensuales, monedas.get(moneda_seleccionada));
                        graficoBarrasGastosMensuales(year_seleccionado, "1", binding.chartIngresosMensuales, monedas.get(moneda_seleccionada));
                        graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "0", binding.piechartGastosTag, monedas.get(moneda_seleccionada));
                        graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "1", binding.piechartIngresosTag, monedas.get(moneda_seleccionada));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        binding.spinnerYear.setSelection(Calendar.getInstance().get(Calendar.YEAR)-2021);





        ArrayList<String> meses = new ArrayList<>();
        for(int x=0; x!=12;x++){
            meses.add(Utils.getMesPorNumero(x));
        }
        ArrayAdapter<String> adapterSpinnerMeses = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, meses){
            @RequiresApi(api = Build.VERSION_CODES.O)
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(20);
                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                return v;
            }
        };
        adapterSpinnerMeses.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinnerMes.setAdapter(adapterSpinnerMeses);
        binding.spinnerMes.setBackground(getResources().getDrawable(R.drawable.fondo_blanco_redondeado));
        binding.spinnerMes.setGravity(Gravity.CENTER);
        binding.spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(check_for_spinners > 0){
                    mes_seleccionado = Integer.toString(i + 1);
                    if (!monedas.isEmpty()) {
                        graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "0", binding.piechartGastosTag, monedas.get(moneda_seleccionada));
                        graficoTortaGastosPorTags(year_seleccionado, mes_seleccionado, "1", binding.piechartIngresosTag, monedas.get(moneda_seleccionada));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        binding.spinnerMes.setSelection(Integer.parseInt(mes_seleccionado)-1);

        check_for_spinners = 1;

        binding.volver.setOnClickListener(v-> navController.navigate(Integer.parseInt(Preferences.getPreferenceString(requireContext(), "id_fragment_anterior"))));

        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "InflateParams"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void graficoBarrasGastosMensuales(String year, String ingreso, BarChart chart, String nombre_moneda) {
        Cursor gastos_por_mes = database.getTotalGastosMensualesPorYearMoneda(year,ingreso, nombre_moneda);

        ArrayList<HashMap<Integer, Float>> fecha_total_gasto = new ArrayList<>();
        while (gastos_por_mes.moveToNext()) {
            String fecha = gastos_por_mes.getString(1);
            int mes = Integer.parseInt(fecha.substring(5, 7));

            HashMap<Integer, Float> h = new HashMap<>();
            h.put(mes, gastos_por_mes.getFloat(0));
            fecha_total_gasto.add(h);
        }


        ArrayList<String> meses = new ArrayList<>();
        ArrayList<BarEntry> gastos_mensuales_grafico = new ArrayList<>();
        for (int x = 0; x != 12; x++) {
            String mes = Utils.getMesPorNumero(x).substring(0,3);
            meses.add(mes.substring(0, 1).toUpperCase() + mes.substring(1));
            float gasto_mensual = 0;
            for (HashMap<Integer, Float> h : fecha_total_gasto) {
                //Ignorar warning, pues si lo contiene nunca dará error
                if (h.containsKey(x + 1)) {
                    gasto_mensual = h.get(x + 1);
                }
            }
            gastos_mensuales_grafico.add(new BarEntry(x, gasto_mensual));
        }

        BarDataSet barDataSet = new BarDataSet(gastos_mensuales_grafico, getResources().getString(R.string.gastos_por_mes));
        if(ingreso.equals("1")){
            barDataSet = new BarDataSet(gastos_mensuales_grafico, getResources().getString(R.string.ingresos_por_mes));
        }
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        chart.setFitBars(true);
        chart.setData(barData);
        chart.getDescription().setText("");
        chart.animateY(2000);
        chart.setNoDataTextColor(Color.BLACK);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(meses));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(meses.size());
    }


    public void graficoTortaGastosPorTags(String year, String mes, String ingreso, PieChart chart, String nombre_moneda){
        Cursor total_gastos_mes = database.getTotalGastosPorYearMesMoneda(year, mes, nombre_moneda, ingreso);
        float total_mes = 0;
        while(total_gastos_mes.moveToNext()){
            total_mes = total_gastos_mes.getFloat(0);
            String a =  total_gastos_mes.getString(1);
            String w ="a";
        }

        Cursor total_gastos_mes_por_tag = database.getTotalGastosPorYearMesMonedaGroupByTags(year, mes, nombre_moneda, ingreso);


        ArrayList<PieEntry> entries = new ArrayList<>();
        while (total_gastos_mes_por_tag.moveToNext()){
            entries.add(new PieEntry(total_gastos_mes_por_tag.getFloat(0), total_gastos_mes_por_tag.getString(1)));
            total_mes -= total_gastos_mes_por_tag.getFloat(0);
        }
        if(total_mes!=0) {
            entries.add(new PieEntry(total_mes, getResources().getString(R.string.sin_tag)));
        }


        if(!entries.isEmpty()) {
            PieDataSet pieDataSet = new PieDataSet(entries, getResources().getString(R.string.gastos_por_tags));
            chart.setCenterText(getResources().getString(R.string.gastos_por_tags));
            chart.setCenterTextColor(Color.RED);
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            if (ingreso.equals("1")) {
                pieDataSet = new PieDataSet(entries, getResources().getString(R.string.ingresos_por_tags));
                chart.setCenterText(getResources().getString(R.string.ingresos_por_tags));
                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                chart.setCenterTextColor(Color.GREEN);
            }
            pieDataSet.setValueTextColor(Color.WHITE);
            pieDataSet.setValueTextSize(16f);

            PieData pieData = new PieData(pieDataSet);

            chart.setEntryLabelColor(Color.WHITE);
            chart.setHoleColor(Color.BLACK);
            chart.setData(pieData);
            chart.getDescription().setEnabled(false);
            chart.animate();
        }
        else{
            chart.setNoDataTextColor(Color.BLACK);
            chart.clear();
        }
    }









/*
    private void cargarTags(Calendar desde, Calendar hasta){
        Cursor tags = database.getTagsByUserId(UsuarioSingleton.getInstance().getID());
        while(tags.moveToNext()){
            tags_creados_por_usuario.add(tags.getString(1));
        }

        if(tags_seleccionados.isEmpty()){
            binding.tagsSeleccionados.setText(getResources().getString(R.string.sin_seleccionados));
        }
        else {
            binding.tagsSeleccionados.setText(Utils.arrayListToString(tags_seleccionados));
        }

        *//*Cursor total_by_tags = database.getTotalGastosGroupByTagIngresoMoneda(Utils.calendarToString(desde), Utils.calendarToString(hasta));
        Cursor gastos = database.getGastosBySessionUser(Utils.calendarToString(desde), Utils.calendarToString(hasta));
        while (gastos.moveToNext()) {
            Cursor tag = database.getTagsByGastoId(gastos.getInt(0));
            while (tag.moveToNext()){
                tags_array.add(tag.getString(1));
            }
        }*//*
        tags_checkeados = new boolean[tags_creados_por_usuario.size()];
    }




    private void abrirSpinnerTags(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireActivity());
        mBuilder.setTitle(getResources().getString(R.string.seleciona));
        //i es la posicion del click, b es booleano. Por si ya está checkeado
        mBuilder.setMultiChoiceItems(tags_creados_por_usuario.toArray(new CharSequence[0]), tags_checkeados, (dialogInterface, i, b) -> {
            if(b) {
                tags_seleccionados_index.add(i);
            }
            else{
                tags_seleccionados_index.remove(Integer.valueOf(i));
            }

            tags_seleccionados.clear();
            for(int x =0;x!=tags_seleccionados_index.size();x++){
                tags_seleccionados.add(tags_creados_por_usuario.get(tags_seleccionados_index.get(x)));
            }
            if(tags_seleccionados.isEmpty()){
                binding.tagsSeleccionados.setText(getResources().getString(R.string.sin_seleccionados));
            }
            else {
                binding.tagsSeleccionados.setText(Utils.arrayListToString(tags_seleccionados));
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
            StringBuilder item = new StringBuilder();
            for(int x = 0; x < tags_seleccionados_index.size(); x++){
                item.append(tags_creados_por_usuario.get(tags_seleccionados_index.get(x)));
                if(x != tags_seleccionados_index.size()-1){
                    item.append(", ");
                }

            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }*/




}