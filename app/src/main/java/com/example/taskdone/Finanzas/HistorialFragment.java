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
import com.example.taskdone.R;
import com.example.taskdone.UsuarioSingleton;
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

    String selected_date_desde = "Siempre";
    String selected_date_hasta = "Hoy";

    DataBase dataBase;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasHistorialBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        dataBase = new DataBase(requireContext());

        Calendar c= Calendar.getInstance();
        c.add(Calendar.DATE, -29);
        try {
            cargarHistorial(c.getTime(), Calendar.getInstance().getTime());
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
    private void cargarHistorial(Date desde_date, Date hasta_date) throws ParseException {
        cleanHistorial();


        Cursor data = dataBase.getGastosBySessionUser(Utils.dateToString(desde_date), Utils.dateToString(hasta_date));

        ArrayList<ItemHistorial> data_items = new ArrayList<>();
        ArrayList<String> fechas = new ArrayList<>();


        while (data.moveToNext()) {
            String fecha = data.getString(1);
            Float total_gasto = data.getFloat(2);
            String motivo = data.getString(3);
            String ingreso = data.getString(4);
            String nombre_moneda = data.getString(5);
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
                View view = inflater.inflate(R.layout.elementos_historial, null);
                binding.layoutHistorial.addView(view);
                TextView fecha = view.findViewById(R.id.fecha);
                RecyclerView rec = view.findViewById(R.id.recycler);
                rec.setLayoutManager(new LinearLayoutManager(getContext()));
                fecha.setText(Utils.getDiaFormateado(fechas.get(x)));
                rec.setAdapter(adapter);
            }
        }
    }

    private void cleanHistorial(){
        binding.textDesde.setText("Siempre");
        binding.textHasta.setText("Hoy");

        for(int x=0;0!=binding.layoutHistorial.getChildCount(); x++) {
            binding.layoutHistorial.removeViewAt(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filtrar(String desde, String hasta) throws ParseException {
        String text_desde = "Siempre";
        String text_hasta = "Hoy";

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