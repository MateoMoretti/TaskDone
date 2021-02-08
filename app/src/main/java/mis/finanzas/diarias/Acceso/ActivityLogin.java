package mis.finanzas.diarias.Acceso;


import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import mis.finanzas.diarias.DataBase;
import mis.finanzas.diarias.Finanzas.ActivityFinanzas;
import mis.finanzas.diarias.Preferences;
import com.example.taskdone.R;
import mis.finanzas.diarias.UsuarioSingleton;

import java.util.Locale;
import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {

    NavController navController;
    DataBase database;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        Preferences.deletePreferencesGastoPendiente(getApplicationContext());
        Preferences.deletePreferencesEdicionGasto(getApplicationContext());

        database = new DataBase(getApplicationContext());

        cargarIdioma();

        String usuario_loggeado = Preferences.getPreferenceString(getApplicationContext(), "usuario");
        String password = Preferences.getPreferenceString(getApplicationContext(), "password");
        ingresarDirectoSesion(usuario_loggeado, password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (R.id.loginFragment == Objects.requireNonNull(navController.getCurrentDestination()).getId()) {
            finish();
        }
        else{
            navController.navigate(Integer.parseInt(Preferences.getPreferenceString(getApplicationContext(), "id_fragment_anterior")));
        }
    }
    void ingresarDirectoSesion(String usuario, String password){
        Cursor data = database.getUserByUsernameAndPassword(usuario,password);
        boolean permitido = false;
        while (data.moveToNext()) {
            UsuarioSingleton.getInstance().setID(data.getInt(0));
            UsuarioSingleton.getInstance().setUsername(data.getString(1));
            permitido = true;
        }

        if(permitido) {
            Intent i = new Intent(getApplicationContext(), ActivityFinanzas.class);
            startActivity(i);
        }
    }

    void setIdioma(String idioma){
        Locale locale = new Locale(idioma);
        if(idioma.equals("")){
            locale = new Locale("es_419");
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        Preferences.savePreferenceString(getApplicationContext(), idioma,"idioma");
    }

    void cargarIdioma(){
        String idioma = Preferences.getPreferenceString(getApplicationContext(), "idioma");
        setIdioma(idioma);
    }
}
