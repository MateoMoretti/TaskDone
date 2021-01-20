package com.example.taskdone.Finanzas;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.Preferences;
import com.example.taskdone.R;
import com.example.taskdone.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ActivityFinanzas extends AppCompatActivity {

    NavController navController;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finanzas);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MenuItem pesos = navView.getMenu().findItem(R.id.nvg_principal);
        MenuItem stats = navView.getMenu().findItem(R.id.nvg_stats);
        MenuItem historial = navView.getMenu().findItem(R.id.nvg_historial);
        Drawable dr_signo_pesos = getDrawable(R.drawable.signo_pesos);
        Drawable dr_stats = getDrawable(R.drawable.stats);
        Drawable dr_historial = getDrawable(R.drawable.historial);
        dr_signo_pesos.mutate().setColorFilter(getColor(R.color.verde), PorterDuff.Mode.SRC_IN);
        dr_stats.mutate().setColorFilter(getColor(R.color.naranja), PorterDuff.Mode.SRC_IN);
        dr_historial.mutate().setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        pesos.setIcon(dr_signo_pesos);
        stats.setIcon(dr_stats);
        historial.setIcon(dr_historial);

        Utils.setContext(getApplicationContext());

        Preferences.deletePreferenceString(getApplicationContext(), "stats_desde");
        Preferences.deletePreferenceString(getApplicationContext(), "stats_hasta");
        Preferences.deletePreferenceString(getApplicationContext(), "historial_desde");
        Preferences.deletePreferenceString(getApplicationContext(), "historial_hasta");
        Preferences.deletePreferenceString(getApplicationContext(), "id_fragment_anterior");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nvg_principal:
                    navController.navigate(R.id.principalFragment);
                    return false;
                case R.id.nvg_historial:
                    navController.navigate(R.id.historialFragment);
                    return false;
                case R.id.nvg_stats:
                    navController.navigate(R.id.statsFragment);
                    return false;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //On back pressed if nav destination igual a los 3 principales: queres salir de sesion wacho?

    @Override
    public void onBackPressed() {
        ArrayList<Integer> destinos_principales = new ArrayList<>(Arrays.asList(R.id.principalFragment,R.id.historialFragment,R.id.statsFragment));
        if (destinos_principales.contains(navController.getCurrentDestination().getId())) {
            popupCerrarSesion();
        }
        else{
            navController.navigate(Integer.parseInt(Preferences.getPreferenceString(getApplicationContext(), "id_fragment_anterior")));
        }
    }
    private void popupCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.salir));
        builder.setMessage(getResources().getString(R.string.quieres_cerrar_sesion));
        DialogInterface.OnClickListener c = (dialogInterface, i) -> {
            Preferences.deleteAllPreferenceString(getApplicationContext());
            Preferences.cleanPreferencesGastoPendiente(getApplicationContext());
            finish();
        };
        builder.setPositiveButton(getResources().getString(R.string.si), c);
        builder.setNegativeButton(getResources().getString(R.string.no), null);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
