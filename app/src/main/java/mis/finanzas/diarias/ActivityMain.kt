package mis.finanzas.diarias

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.taskdone.R
import com.example.taskdone.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class ActivityMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private var fragment = R.id.finanzasPrincipalFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController

        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState()
        binding.sideBar.setNavigationItemSelectedListener { onNavigationItemSelected(it) }
        val menuItem: MenuItem = binding.sideBar.menu.getItem(1)
        onNavigationItemSelected(menuItem)
        menuItem.isChecked = true

        Ads.getInstance().cargarAnuncio(applicationContext)


        val pesos = binding.bottomBar.menu.findItem(R.id.nvg_principal)
        val stats = binding.bottomBar.menu.findItem(R.id.nvg_stats)
        val historial = binding.bottomBar.menu.findItem(R.id.nvg_historial)

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

        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nvg_principal -> {
                    fragment = R.id.finanzasPrincipalFragment
                }

                R.id.nvg_historial -> {
                    fragment = R.id.finanzasHistorialFragment
                }

                R.id.nvg_stats -> {
                    fragment = R.id.finanzasStatsFragment
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

    fun updateFragment(id: Int) {
        fragment = id
    }

    fun getFragmentId() = fragment

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        sideBarNavigate(menuItem)
        menuItem.isChecked = true
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true
    }

    private fun sideBarNavigate(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.agenda -> {
                title = getString(R.string.agenda)
                fragment = R.id.agendaFragment
            }
            R.id.finanzas -> {
                getString(R.string.finanzas)
                fragment = R.id.finanzasPrincipalFragment
            }
            R.id.comidas -> {
                getString(R.string.comidas)
            }
            R.id.objetivos -> {
                getString(R.string.objetivos)
            }
            R.id.extras -> {
                getString(R.string.extras)
            }
            R.id.compartir -> {
                getString(R.string.compartir)
            }
            R.id.configuracion -> {
                getString(R.string.configuracion)
            }
            else -> ""
        }
        navController.navigate(fragment)
    }


    /*override fun onSupportNavigateUp(): Boolean {
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
    }*/
}