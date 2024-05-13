package my.life.tracker.agenda

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


interface AgendaPreferences {
    companion object PreferencesKey {
        val ACTIViDADES = "ACTIViDADES"
        val TIPOS = "TIPOS"

    }
    fun savePreferenceString(c: Context, b: String, key: String)

    fun getPreferenceString(c: Context, key: String): String?

    fun deletePreferenceString(c: Context, key: String)


    fun getActividades(c: Context): String
    fun getTipos(c: Context): String

}


