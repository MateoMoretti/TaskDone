package mis.finanzas.diarias.activities

import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.taskdone.R
import com.example.taskdone.databinding.ActivityFinanzasBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import mis.finanzas.diarias.Ads
import mis.finanzas.diarias.Preferences
import mis.finanzas.diarias.Utils

class ActivityFinanzas : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityFinanzasBinding

    private var fragment = R.id.principalFragment

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinanzasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController

        //Bottom bar
        val navView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        Ads.getInstance().cargarAnuncio(applicationContext)


        val pesos = navView.menu.findItem(R.id.nvg_principal)
        val stats = navView.menu.findItem(R.id.nvg_stats)
        val historial = navView.menu.findItem(R.id.nvg_historial)
        val drSignoPesos = ContextCompat.getDrawable(applicationContext, R.drawable.signo_pesos)
        val drStats = ContextCompat.getDrawable(applicationContext, R.drawable.stats)
        val drHistorial = ContextCompat.getDrawable(applicationContext, R.drawable.historial)
        drSignoPesos!!.mutate().setColorFilter(getColor(R.color.verde), PorterDuff.Mode.SRC_IN)
        drStats!!.mutate().setColorFilter(getColor(R.color.naranja), PorterDuff.Mode.SRC_IN)
        drHistorial!!.mutate().setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        pesos.icon = drSignoPesos
        stats.icon = drStats
        historial.icon = drHistorial
        Utils.setContext(applicationContext)
        Preferences.deletePreferenceString(applicationContext, "stats_desde")
        Preferences.deletePreferenceString(applicationContext, "stats_hasta")
        Preferences.deletePreferenceString(applicationContext, "historial_desde")
        Preferences.deletePreferenceString(applicationContext, "historial_hasta")
        Preferences.deletePreferenceString(applicationContext, "id_fragment_anterior")

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nvg_principal -> {
                    fragment = R.id.principalFragment
                }
                R.id.nvg_historial -> {
                    fragment = R.id.historialFragment
                }
                R.id.nvg_stats -> {
                    fragment = R.id.statsFragment
                }
            }
            navController.navigate(fragment)
            return@setOnItemSelectedListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    fun updateFragment(id: Int) { fragment = id}

    override fun onSupportNavigateUp(): Boolean {
        when(fragment) {
            R.id.principalFragment -> super.onBackPressedDispatcher.onBackPressed()
            R.id.tagsFragment -> fragment = R.id.principalFragment
            R.id.crearMonedaFragment -> fragment = R.id.principalFragment
            R.id.historialFragment -> fragment = R.id.principalFragment
            R.id.statsFragment -> fragment = R.id.principalFragment
            R.id.statsAvanzadosFragment -> fragment = R.id.statsFragment
            else -> return navController.navigateUp()
        }
        navController.navigate(fragment)
        return true
    }
}