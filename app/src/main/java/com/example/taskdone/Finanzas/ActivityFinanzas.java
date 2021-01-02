package com.example.taskdone.Finanzas;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.taskdone.MenuPrincipalActivity;
import com.example.taskdone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack();
    }
}
