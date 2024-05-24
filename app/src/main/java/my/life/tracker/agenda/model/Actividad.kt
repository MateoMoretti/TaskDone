package my.life.tracker.agenda.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Actividad")
class Actividad (
    @ColumnInfo(name = "actividad") var actividad: String="",
    @ColumnInfo(name = "tipo") var tipo: String="",
    @ColumnInfo(name = "comienzo") var comienzo: String="",
    @ColumnInfo(name = "fin") var fin: String="",
    @ColumnInfo(name = "importancia") var importancia: String="",
    @ColumnInfo(name = "comentarios") var comentarios: String="",
    @ColumnInfo(name = "date") var date: String="",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0
){

    fun isEmpty(): Boolean {
        return actividad == "" && tipo == "" && comienzo == "" && fin == "" && importancia == "" && comentarios == ""
    }
    fun getAttributes(): List<String> = arrayListOf(actividad, tipo, comienzo, fin, importancia, comentarios, date, id.toString())

    fun setAttributes(indexDefaulValueActividad: Int, value: String) {
        when(indexDefaulValueActividad){
            0 -> actividad = value
            1 -> tipo = value
            2 -> comienzo = value
            3 -> fin = value
            4 -> importancia = value
            5 -> comentarios = value
            6 -> date = value
            7 -> id = value.toLong()
        }
    }

}
