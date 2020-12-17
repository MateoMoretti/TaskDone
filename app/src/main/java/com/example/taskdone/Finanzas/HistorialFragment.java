package com.example.taskdone.Finanzas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.example.taskdone.databinding.FragmentFinanzasHistorialBinding;

import java.util.ArrayList;

public class HistorialFragment extends Fragment {

    private FragmentFinanzasHistorialBinding binding;

    private String titulo;
    NavController navController;

    DataBaseFinanzas dataBaseFinanzas;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasHistorialBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        dataBaseFinanzas = new DataBaseFinanzas(requireContext());

        populateRecycler();

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void populateRecycler(){
        Cursor data = dataBaseFinanzas.getDataGastos();

        ArrayList<ItemHistorial> data_items = new ArrayList<>();
        ArrayList<String> fechas = new ArrayList<>();


        while (data.moveToNext()){
            String fecha = data.getString(1);
            String escencial = data.getString(2);
            String tipo_moneda = data.getString(3);
            String cantidad = data.getString(4);
            String motivo = data.getString(5);
            String ingreso = data.getString(6);

            if(ingreso.equals("0")){
                ingreso = "-";
            }
            else{
                ingreso = "+";
            }

            if(tipo_moneda.equals("Pesos")){
                tipo_moneda = "$";
            }
            else if(tipo_moneda.equals("Dólares")){
                tipo_moneda = "U$D";
            }

            else if(tipo_moneda.equals("Euros")){
                tipo_moneda = "€";
            }

            if(!fechas.contains(fecha)){
                fechas.add(fecha);
            }

            ItemHistorial i = new ItemHistorial();
            i.fecha = fecha;
            i.signo = ingreso;
            i.tipo = tipo_moneda;
            i.cantidad = Utils.formatoCantidad(cantidad);
            i.motivo = motivo;

            data_items.add(i);
        }

        //LLENO LOS TITULOS DE FECHAS

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        for(int x=0;x!=fechas.size();x++){
            ArrayList<ItemHistorial> data_items_fecha = new ArrayList<>();
            for(ItemHistorial i:data_items){
                if(i.fecha.equals(fechas.get(x))){
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