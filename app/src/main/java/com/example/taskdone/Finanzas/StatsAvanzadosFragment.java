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
    String year_seleccionado = "0";


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

        year_seleccionado = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        if(!monedas.isEmpty()){
            graficoBarrasGastosMensuales(year_seleccionado, "0", binding.chartGastosMensuales, monedas.get(0));
            graficoBarrasGastosMensuales(year_seleccionado, "1", binding.chartIngresosMensuales, monedas.get(0));
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
                moneda_seleccionada = i;
                if(!monedas.isEmpty()){
                    graficoBarrasGastosMensuales(year_seleccionado, "0", binding.chartGastosMensuales, monedas.get(moneda_seleccionada));
                    graficoBarrasGastosMensuales(year_seleccionado, "1", binding.chartIngresosMensuales, monedas.get(moneda_seleccionada));
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
                year_seleccionado = binding.spinnerYear.getSelectedItem().toString();
                if(!monedas.isEmpty()){
                    graficoBarrasGastosMensuales(year_seleccionado, "0", binding.chartGastosMensuales, monedas.get(moneda_seleccionada));
                    graficoBarrasGastosMensuales(year_seleccionado, "1", binding.chartIngresosMensuales, monedas.get(moneda_seleccionada));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        binding.tagsSeleccionados.setOnClickListener(v -> abrirSpinnerTags());

        return binding.getRoot();
    }


    @SuppressLint({"SetTextI18n", "InflateParams"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void graficoBarrasGastosMensuales(String year, String ingreso, BarChart chart, String nombre_moneda) {
        Cursor gastos_por_mes = database.getTotalGastosMensualesPorAñoMoneda(year,ingreso, nombre_moneda);

        ArrayList<HashMap<Integer, Integer>> fecha_total_gasto = new ArrayList<>();
        while (gastos_por_mes.moveToNext()) {
            String fecha = gastos_por_mes.getString(1);
            int mes = Integer.parseInt(fecha.substring(5, 7));

            HashMap<Integer, Integer> h = new HashMap<>();
            h.put(mes, gastos_por_mes.getInt(0));
            fecha_total_gasto.add(h);
        }


        ArrayList<String> meses = new ArrayList<>();
        ArrayList<BarEntry> gastos_mensuales_grafico = new ArrayList<>();
        for (int x = 0; x != 12; x++) {
            String mes = Utils.getMesPorNumero(x).substring(0,3);
            meses.add(mes.substring(0, 1).toUpperCase() + mes.substring(1));
            int gasto_mensual = 0;
            for (HashMap<Integer, Integer> h : fecha_total_gasto) {
                if (h.containsKey(x + 1)) {
                    gasto_mensual = h.get(x + 1);
                }
            }
            gastos_mensuales_grafico.add(new BarEntry(x, gasto_mensual));
        }


        BarDataSet barDataSet = new BarDataSet(gastos_mensuales_grafico, "Gastos por mes");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        chart.setFitBars(true);
        chart.setData(barData);
        chart.getDescription().setText("");
        chart.animateY(2000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(meses));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(meses.size());
    }


    public void GraficoTortaGastosPorTags(String mes, String ingreso, PieChart chart){
        //Cursor total_gastos_mes = database.getTotalGastosGroupByTagIngresoMoneda();
    }











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

        /*Cursor total_by_tags = database.getTotalGastosGroupByTagIngresoMoneda(Utils.calendarToString(desde), Utils.calendarToString(hasta));
        Cursor gastos = database.getGastosBySessionUser(Utils.calendarToString(desde), Utils.calendarToString(hasta));
        while (gastos.moveToNext()) {
            Cursor tag = database.getTagsByGastoId(gastos.getInt(0));
            while (tag.moveToNext()){
                tags_array.add(tag.getString(1));
            }
        }*/
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
    }




}