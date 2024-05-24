package my.life.tracker.agenda

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import my.life.tracker.agenda.model.CellType
import javax.inject.Inject
import javax.inject.Singleton


private const val SHARED_PREFERENCES_NAME = "agenda_preferences"
class AgendaPreferencesImpl
@Inject constructor(@ApplicationContext context: Context) : AgendaPreferences{

    private val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private fun savePreferenceString(b: String, key: String) {
        preferences.edit().putString(key, b).apply()
    }

    private fun getPreferenceString(key: String): String {
        return preferences.getString(key, "").toString()
    }

    private fun deletePreferenceString(key: String) {
        preferences.edit().remove(key).apply()
    }

    override fun saveCeldaName(c: Context, celdaIndexPosition:Int, name: String) = savePreferenceString(name, AgendaPreferences.CELDA_NAME+celdaIndexPosition)
    override fun saveCeldaSize(c: Context, celdaIndexPosition:Int, width: String, height: String) {
        savePreferenceString(width, AgendaPreferences.CELDA_WIDTH+celdaIndexPosition)
        savePreferenceString(height, AgendaPreferences.CELDA_HEIGHT+celdaIndexPosition)
    }

    override fun saveCeldaType(c: Context, celdaIndexPosition: Int, type: CellType) = savePreferenceString(type.toString(), AgendaPreferences.CELDA_TYPE_+celdaIndexPosition)


    override fun addCeldaHints(celdaIndexPosition:Int,  hint: String) {
        var hints = getPreferenceString(AgendaPreferences.CELDA_HINTS+celdaIndexPosition)
        hints += "_$hint"
        savePreferenceString(hints, AgendaPreferences.CELDA_HINTS+celdaIndexPosition)
    }

    override fun deleteCeldaHints(c: Context, celdaIndexPosition:Int, hint: String) {
        TODO("Not yet implemented")
    }

    override fun deleteAllCeldaHints(c: Context,celdaIndexPosition:Int) {
        TODO("Not yet implemented")
    }

    override fun getCeldaName(celdaIndexPosition:Int): String = getPreferenceString(AgendaPreferences.CELDA_NAME+celdaIndexPosition)

    override fun getCeldaHints(celdaIndexPosition:Int): ArrayList<String> {
        val arraylist = arrayListOf<String>()
        arraylist.addAll(getPreferenceString(AgendaPreferences.CELDA_HINTS+celdaIndexPosition).split("_"))
        return arraylist
    }
    override fun getCeldaType(celdaIndexPosition:Int): CellType{
        var cellType :CellType = CellType.TEXTO
        val customCellType = CellType.valueOf(getPreferenceString(AgendaPreferences.CELDA_TYPE_+celdaIndexPosition))
        if(customCellType.toString().isNotEmpty()){
            cellType = customCellType
        }else {
            when (celdaIndexPosition) {
                0, 1 -> cellType = CellType.SPINNER
                2, 3 -> cellType = CellType.HORA
                4 -> cellType = CellType.SLIDER
                5 -> cellType = CellType.TEXTO
            }
        }
        return cellType
    }
    override fun getCeldaWidth(celdaIndexPosition:Int): String = getPreferenceString(AgendaPreferences.CELDA_WIDTH+celdaIndexPosition)
    override fun getCeldaHeight(celdaIndexPosition:Int): String = getPreferenceString(AgendaPreferences.CELDA_HEIGHT+celdaIndexPosition)
}