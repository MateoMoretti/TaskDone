package mis.finanzas.diarias.agenda

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
import com.example.taskdone.databinding.ActivityAgendaBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import mis.finanzas.diarias.Utils

class ActivityAgenda : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityAgendaBinding

    private var fragment = R.id.principalFragment

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController

        //Bottom bar
        val navView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        val principal = navView.menu.findItem(R.id.nvg_principal)
        val stats = navView.menu.findItem(R.id.nvg_stats)
        val historial = navView.menu.findItem(R.id.nvg_historial)

        val agendaIcon = ContextCompat.getDrawable(applicationContext, R.drawable.agenda)
        val statsIcon = ContextCompat.getDrawable(applicationContext, R.drawable.stats)
        val historialIcon = ContextCompat.getDrawable(applicationContext, R.drawable.historial)
        agendaIcon!!.mutate().setColorFilter(getColor(R.color.naranja), PorterDuff.Mode.SRC_IN)
        statsIcon!!.mutate().setColorFilter(getColor(R.color.naranja), PorterDuff.Mode.SRC_IN)
        historialIcon!!.mutate().setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        principal.icon = agendaIcon
        stats.icon = statsIcon
        historial.icon = historialIcon
        Utils.setContext(applicationContext)

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nvg_principal -> {
                    fragment = R.id.agendaFragment
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
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    fun updateFragment(id: Int) { fragment = id}

    fun getFragmentId() = fragment
}