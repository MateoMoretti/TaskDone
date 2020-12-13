package com.example.taskdone.Agenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.databinding.FragmentAgendaVidaBinding;

import java.util.List;

public class VidaFragment extends Fragment {

    private FragmentAgendaVidaBinding binding;

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
        binding = FragmentAgendaVidaBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        return binding.getRoot();
    }
}