package my.life.tracker.agenda

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import my.life.tracker.agenda.model.CellType
import javax.inject.Inject


interface AgendaPreferences {
    companion object PreferencesKey {
        val CELDA_NAME = "CELDA_NAME_"
        val CELDA_WIDTH = "CELDA_WIDTH_"
        val CELDA_HEIGHT = "CELDA_HEIGHT_"
        val CELDA_HINTS = "CELDA_HINTS_"
        val CELDA_TYPE_ = "CELDA_TYPE_"
    }
    fun saveCeldaName(c: Context, celdaIndexPosition:Int, name: String)
    fun saveCeldaSize(c: Context, celdaIndexPosition:Int, width: String, height: String)
    fun saveCeldaType(c: Context, celdaIndexPosition:Int, type: CellType)
    fun addCeldaHints(celdaIndexPosition:Int, hint: String)
    fun deleteCeldaHints(c: Context, celdaIndexPosition:Int, hint: String)

    fun deleteAllCeldaHints(c: Context, celdaIndexPosition:Int)


    fun getCeldaName(celdaIndexPosition:Int): String
    fun getCeldaWidth(celdaIndexPosition:Int): String
    fun getCeldaHeight(celdaIndexPosition:Int): String
    fun getCeldaHints(celdaIndexPosition:Int): ArrayList<String>
    fun getCeldaType(celdaIndexPosition:Int): CellType

}


