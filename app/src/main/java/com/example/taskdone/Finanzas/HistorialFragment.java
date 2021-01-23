package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskdone.DataBase;
import com.example.taskdone.DatePickerFragment;
import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentFinanzasHistorialBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class HistorialFragment extends Fragment {

    private FragmentFinanzasHistorialBinding binding;

    NavController navController;

    String selected_date_desde;
    String selected_date_hasta;

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

    @SuppressLint("SetTextI18n")
    private void cargarHistorial(Date desde_date, Date hasta_date) {
        cleanHistorial();


        Cursor data = dataBase.getGastosBySessionUser(Utils.dateToString(desde_date), Utils.dateToString(hasta_date));

        ArrayList<ItemHistorial> data_items = new ArrayList<>();
        ArrayList<String> fechas = new ArrayList<>();


        while (data.moveToNext()) {
            String fecha = data.getString(1);
            float total_gasto = data.getFloat(2);
            String motivo = data.getString(3);
            String ingreso = data.getString(4);
            //String nombre_moneda = data.getString(5);
            String simbolo_moneda = data.getString(6);

            if (ingreso.equals("0")) {
                ingreso = "-";
            } else {
                ingreso = "+";
            }
            if (!fechas.contains(fecha)) {
                fechas.add(fecha);
            }

            ItemHistorial i = new ItemHistorial();
            i.fecha = fecha;
            i.signo = ingreso;
            i.simbolo = simbolo_moneda;
            i.cantidad = Utils.formatoCantidad(total_gasto);
            i.motivo = motivo;

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
                ArrayList<ItemHistorial> data_items_fecha = new ArrayList<>();
                for (ItemHistorial i : data_items) {
                    if (i.fecha.equals(fechas.get(x))) {
                        data_items_fecha.add(i);
                    }
                }
                HistorialAdapter adapter = new HistorialAdapter(data_items_fecha, requireContext());
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.elementos_historial, null);
                binding.layoutHistorial.addView(view);
                TextView fecha = view.findViewById(R.id.fecha);
                RecyclerView rec = view.findViewById(R.id.recycler);
                rec.setLayoutManager(new LinearLayoutManager(getContext()));
                fecha.setText(Utils.getDia(fechas.get(x)));
                rec.setAdapter(adapter);
                if(x==0){
                    view.findViewById(R.id.linea).setVisibility(View.INVISIBLE);
                }
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



}