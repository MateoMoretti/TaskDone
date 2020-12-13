package com.example.taskdone;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.Agenda.AgendaFragment;
import com.example.taskdone.Finanzas.ActivityFinanzas;
import com.example.taskdone.databinding.FragmentMenuBinding;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        binding.Agenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.menu_to_agenda, bundleAgenda());
            }
        });
        binding.Finanzas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAFinanzar();
            }
        });
        binding.Comidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.menu_to_agenda, bundleComidas());
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public Bundle bundleAgenda(){
        Bundle bundle = new Bundle();
        bundle.putString("titulo", "Agenda");
        return bundle;
    }

    public void irAFinanzar(){
        Intent i = new Intent(requireActivity(), ActivityFinanzas.class);
        startActivity(i);
        requireActivity().finish();
    }

    public Bundle bundleComidas(){
        Bundle bundle = new Bundle();
        bundle.putString("titulo", "Comidas");
        return bundle;
    }


}