package mis.finanzas.diarias

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.taskdone.R
import com.example.taskdone.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.util.Calendar


class ActivityMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private var fragment = R.id.finanzasFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController
        Utils.setContext(applicationContext)

        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState()

        binding.sideBar.itemIconTintList = null
        binding.sideBar.setNavigationItemSelectedListener { onNavigationItemSelected(it) }


        binding.bottomBar.itemIconTintList = null
        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nvg_menu -> { fragment = R.id.agendaFragment }
                R.id.nvg_historial -> { fragment = R.id.agendaFragment }
                R.id.nvg_stats -> { fragment = R.id.agendaFragment }
            }
            navController.navigate(fragment)
            return@setOnItemSelectedListener true
        }
        setBottomNavBar()
    }

    private fun setBottomNavBar(){
        val iconStats = ContextCompat.getDrawable(applicationContext, R.drawable.stats)
        val center = binding.bottomBar.menu.findItem(R.id.nvg_menu)
        val right = binding.bottomBar.menu.findItem(R.id.nvg_stats)
        val left = binding.bottomBar.menu.findItem(R.id.nvg_historial)

        when(fragment){
            R.id.agendaFragment -> {
                center.icon = ContextCompat.getDrawable(applicationContext, R.drawable.agenda)
                iconStats!!.mutate().setColorFilter(getColor(R.color.azul_agenda), PorterDuff.Mode.SRC_IN)
                right.icon = iconStats
                left.icon = ContextCompat.getDrawable(applicationContext, R.drawable.historial)
            }
            R.id.finanzasFragment -> {
                center.icon = ContextCompat.getDrawable(applicationContext, R.drawable.signo_pesos)
                iconStats!!.mutate().setColorFilter(getColor(R.color.naranja_finanzas), PorterDuff.Mode.SRC_IN)
                right.icon = iconStats
                left.icon = ContextCompat.getDrawable(applicationContext, R.drawable.historial)
            }
            R.id.comidasFragment -> {
                center.icon = ContextCompat.getDrawable(applicationContext, R.drawable.comidas)
                iconStats!!.mutate().setColorFilter(getColor(R.color.violeta_comidas), PorterDuff.Mode.SRC_IN)
                right.icon = iconStats
                left.icon = ContextCompat.getDrawable(applicationContext, R.drawable.historial)
            }
        }
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
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun sideBarNavigate(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.principal -> {
                binding.toolbar.title = getString(R.string.principal)
                fragment = R.id.principalFragment
                binding.bottomBar.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.nvg_menu -> { fragment = R.id.agendaFragment }
                        R.id.nvg_historial -> { fragment = R.id.agendaFragment }
                        R.id.nvg_stats -> { fragment = R.id.agendaFragment }
                    }
                    navController.navigate(fragment)
                    return@setOnItemSelectedListener true
                }
            }
            R.id.agenda -> {
                binding.toolbar.title = getString(R.string.agenda)
                fragment = R.id.agendaFragment
                binding.bottomBar.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.nvg_menu -> { fragment = R.id.agendaFragment }
                        R.id.nvg_historial -> { fragment = R.id.agendaFragment }
                        R.id.nvg_stats -> { fragment = R.id.agendaFragment }
                    }
                    navController.navigate(fragment)
                    return@setOnItemSelectedListener true
                }
            }
            R.id.finanzas -> {
                binding.toolbar.title = getString(R.string.finanzas)
                fragment = R.id.finanzasFragment
                binding.bottomBar.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.nvg_menu -> { fragment = R.id.finanzasFragment }
                        R.id.nvg_historial -> { fragment = R.id.finanzasHistorialFragment }
                        R.id.nvg_stats -> { fragment = R.id.finanzasStatsFragment }
                    }
                    navController.navigate(fragment)
                    return@setOnItemSelectedListener true
                }
            }
            R.id.comidas -> {
                binding.toolbar.title = getString(R.string.comidas)
                fragment = R.id.comidasFragment
                binding.bottomBar.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.nvg_menu -> { fragment = R.id.comidasFragment }
                        R.id.nvg_historial -> { fragment = R.id.comidasFragment }
                        R.id.nvg_stats -> { fragment = R.id.comidasFragment }
                    }
                    navController.navigate(fragment)
                    return@setOnItemSelectedListener true
                }
            }
            R.id.objetivos -> {
                binding.toolbar.title = getString(R.string.objetivos)
            }
            R.id.extras -> {
                binding.toolbar.title = getString(R.string.extras)
            }
            R.id.compartir -> {
                binding.toolbar.title = getString(R.string.compartir)
            }
            R.id.configuracion -> {
                binding.toolbar.title = getString(R.string.configuracion)
            }
            else -> ""
        }
        setBottomNavBar()
        navController.navigate(fragment)
    }


    fun selectTime(editText: TextView, context:Context) {
        val mcurrentTime = Calendar.getInstance()
        val hourOfDay: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute: Int = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker = TimePickerDialog(context,
            { _, selectedHour, selectedMinute ->
                run {
                    val min = if (selectedMinute < 10) "0$selectedMinute" else selectedMinute
                    val hour = if (selectedHour < 10) "0$selectedHour" else selectedHour
                    editText.setText("$hour:$min")
                }
            },
            hourOfDay,
            minute,
            true
        )

        mTimePicker.setTitle("Selecciona la hora")
        mTimePicker.show()
    }
}