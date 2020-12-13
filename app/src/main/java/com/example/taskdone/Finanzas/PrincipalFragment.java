package com.example.taskdone.Finanzas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.AdminFechas;
import com.example.taskdone.R;
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding;


public class PrincipalFragment extends Fragment {

    private FragmentFinanzasPrincipalBinding binding;

    private String titulo;
    NavController navController;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasPrincipalBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        binding.titulo.setText(AdminFechas.getDiaHoy());

        ArrayAdapter<CharSequence> adapter_tipo = ArrayAdapter.createFromResource(requireContext(),
                R.array.tipos_moneda, R.layout.spinner);
        adapter_meses.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.meses.setAdapter(adapter_meses);
        binding.spinnerTipo

        return binding.getRoot();
    }
}