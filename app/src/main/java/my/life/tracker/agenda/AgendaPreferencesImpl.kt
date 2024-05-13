package my.life.tracker.agenda

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


private const val SHARED_PREFERENCES_NAME = "agenda_preferences"
class AgendaPreferencesImpl
@Inject constructor(@ApplicationContext context: Context) : AgendaPreferences{

    private val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun savePreferenceString(c: Context, b: String, key: String) {
        preferences.edit().putString(key, b).apply()
    }

    override fun getPreferenceString(c: Context, key: String): String? {
        return preferences.getString(key, "")
    }

    override fun deletePreferenceString(c: Context, key: String) {
        preferences.edit().remove(key).apply()
    }

    override fun getActividades(c: Context): String = getPreferenceString(c, AgendaPreferences.ACTIViDADES)!!


    override fun getTipos(c: Context): String = getPreferenceString(c, AgendaPreferences.TIPOS)!!

}