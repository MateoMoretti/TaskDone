package mis.finanzas.diarias.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import com.example.taskdone.R
import androidx.navigation.fragment.NavHostFragment
import mis.finanzas.diarias.Ads
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.ContextCompat
import android.graphics.PorterDuff
import android.content.DialogInterface
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.taskdone.databinding.ActivityFinanzasBinding
import mis.finanzas.diarias.DataBase
import mis.finanzas.diarias.Preferences
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.viewmodels.UserViewModel

class ActivityFinanzas : AppCompatActivity() {
    private var mNavController: NavController? = null
    //private var mDatabase: DataBase? = null
    private lateinit var mBinding: ActivityFinanzasBinding
    private val userViewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFinanzasBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        mNavController = navHostFragment!!.navController
        //Bottom bar
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        Ads.getInstance().cargarAnuncio(applicationContext)


        //mDatabase = DataBase(userViewModel, applicationContext)
        val loggedUser = Preferences.getPreferenceString(applicationContext, "usuario")
        val password = Preferences.getPreferenceString(applicationContext, "password")
        //logIn(loggedUser, password)

        val pesos = navView.menu.findItem(R.id.nvg_principal)
        val stats = navView.menu.findItem(R.id.nvg_stats)
        val historial = navView.menu.findItem(R.id.nvg_historial)
        val dr_signo_pesos = ContextCompat.getDrawable(applicationContext, R.drawable.signo_pesos)
        val dr_stats = ContextCompat.getDrawable(applicationContext, R.drawable.stats)
        val dr_historial = ContextCompat.getDrawable(applicationContext, R.drawable.historial)
        dr_signo_pesos!!.mutate().setColorFilter(getColor(R.color.verde), PorterDuff.Mode.SRC_IN)
        dr_stats!!.mutate().setColorFilter(getColor(R.color.naranja), PorterDuff.Mode.SRC_IN)
        dr_historial!!.mutate().setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        pesos.icon = dr_signo_pesos
        stats.icon = dr_stats
        historial.icon = dr_historial
        Utils.setContext(applicationContext)
        Preferences.deletePreferenceString(applicationContext, "stats_desde")
        Preferences.deletePreferenceString(applicationContext, "stats_hasta")
        Preferences.deletePreferenceString(applicationContext, "historial_desde")
        Preferences.deletePreferenceString(applicationContext, "historial_hasta")
        Preferences.deletePreferenceString(applicationContext, "id_fragment_anterior")
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nvg_principal -> {
                    mNavController!!.navigate(R.id.principalFragment)
                    return@OnNavigationItemSelectedListener false
                }
                R.id.nvg_historial -> {
                    mNavController!!.navigate(R.id.historialFragment)
                    return@OnNavigationItemSelectedListener false
                }
                R.id.nvg_stats -> {
                    mNavController!!.navigate(R.id.statsFragment)
                    return@OnNavigationItemSelectedListener false
                }
            }
            false
        }

    /*fun logIn(usuario: String, password: String) {
        //val data = mDatabase!!.getUserByUsernameAndPassword(usuario, password)
        var permitido = false
        while (data.moveToNext()) {
            userViewModel.setId(data.getInt(0))
            permitido = true
        }
        if (!permitido) {
            //crear

        }
    }*/

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

    private fun popupCerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.salir))
        builder.setMessage(resources.getString(R.string.quieres_cerrar_sesion))
        val c = DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
            Preferences.deleteFiltros(
                applicationContext
            )
            Preferences.deleteUser(applicationContext)
            Preferences.deletePreferencesGastoPendiente(applicationContext)
            Preferences.deletePreferencesEdicionGasto(applicationContext)
            finish()
        }
        builder.setPositiveButton(resources.getString(R.string.si), c)
        builder.setNegativeButton(resources.getString(R.string.no), null)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }
}