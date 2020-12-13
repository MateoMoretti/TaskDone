package com.example.taskdone.Finanzas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.Agenda.DiaAdapter;
import com.example.taskdone.Agenda.DiaItem;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentFinanzasHistorialBinding;

import java.util.List;
import java.util.Objects;

public class HistorialFragment extends Fragment {

    private FragmentFinanzasHistorialBinding binding;

    private String titulo;
    NavController navController;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasHistorialBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        return binding.getRoot();
    }
}