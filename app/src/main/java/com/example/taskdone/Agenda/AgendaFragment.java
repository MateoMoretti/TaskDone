package com.example.taskdone.Agenda;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskdone.Utils;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentAgendaBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgendaFragment extends Fragment {

    private FragmentAgendaBinding binding;


    private DiaAdapter adapter;
    private List<DiaItem> diaItems;
    //numeros para obtener seleccionado EJ: 2020 y 12
    private String ano;
    private String mes;

    //Fecha de hoy formato dd/mm/yyyy
    private String fecha_hoy;

    private String titulo;
    NavController navController;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAgendaBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        titulo = getArguments().getString("titulo");
        binding.titulo.setText(titulo);
        switch(titulo){
            case "Agenda":
                binding.icono.setImageResource(R.drawable.icono_carpeta);
                binding.icono2.setImageResource(R.drawable.icono_carpeta);
                break;
            case "Finanzas":
                binding.icono.setImageResource(R.drawable.icono_dinero);
                binding.icono2.setImageResource(R.drawable.icono_dinero);
                break;
            case "Comidas":
                binding.icono.setImageResource(R.drawable.icono_frutas);
                binding.icono2.setImageResource(R.drawable.icono_frutas);
                break;
        }


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date_hoy = new Date();
        fecha_hoy = formatter.format(date_hoy);


        ArrayAdapter<CharSequence> adapter_anos = ArrayAdapter.createFromResource(requireContext(),
                R.array.anos, R.layout.spinner);
        adapter_anos.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.anos.setAdapter(adapter_anos);

        ArrayAdapter<CharSequence> adapter_meses = ArrayAdapter.createFromResource(requireContext(),
                R.array.meses, R.layout.spinner);
        adapter_meses.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.meses.setAdapter(adapter_meses);


        binding.meses.setSelection(Integer.parseInt(fecha_hoy.substring(3,5))-1);
        binding.anos.setSelection(Integer.parseInt(fecha_hoy.substring(6,10))-2020);

        binding.recyclerDias.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.anos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.meses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void actualizar(){
        ano = binding.anos.getSelectedItem().toString();
        mes = binding.meses.getSelectedItem().toString();

        //Si está seleccionado el mes y año correspondiente a la fecha actual, scrolleo hasta el dia
        if(Integer.parseInt(fecha_hoy.substring(3,5)) == binding.meses.getSelectedItemPosition()+1 && fecha_hoy.substring(6,10).equals(binding.anos.getSelectedItem().toString())){
            binding.recyclerDias.scrollToPosition(Integer.parseInt(fecha_hoy.substring(0,2)));
        }

        generarListaDias(Utils.diasOrdenadosPorMesAno(Integer.parseInt(ano), binding.meses.getSelectedItemPosition()+1), Utils.diasMes(Integer.parseInt(ano), binding.meses.getSelectedItemPosition()+1));
    }

    private void generarListaDias(List<String> diasOrdenados, int cantidad_dias){
        diaItems = new ArrayList<>();

        int contador=0;
        for(int x = 0; x<cantidad_dias;x++){
            diaItems.add(generarItem(x, diasOrdenados.get(contador)));
            contador++;
            if(contador==7){
                contador=0;
            }
        }

        adapter = new DiaAdapter(diaItems, ano, Integer.toString(binding.meses.getSelectedItemPosition()+1), requireContext());
        binding.recyclerDias.setAdapter(adapter);
    }

    private DiaItem generarItem(int dia, String nombre_dia){
        DiaItem item = new DiaItem();
        item.numero_dia = Integer.toString(dia+1);
        item.nombre_dia = nombre_dia;
        return item;


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}