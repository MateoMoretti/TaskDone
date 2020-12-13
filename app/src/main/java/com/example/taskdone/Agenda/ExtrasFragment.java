package com.example.taskdone.Agenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.databinding.FragmentAgendaExtrasBinding;

public class ExtrasFragment extends Fragment {

    private FragmentAgendaExtrasBinding binding;
    NavController navController;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAgendaExtrasBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        return binding.getRoot();
    }
}