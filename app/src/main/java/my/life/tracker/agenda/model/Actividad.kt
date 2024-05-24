package my.life.tracker.agenda.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Actividad")
class Actividad (
    @ColumnInfo(name = "date") var date: String="",
    @ColumnInfo(name = "actividad") var actividad: String="",
    @ColumnInfo(name = "tipo") var tipo: String="",
    @ColumnInfo(name = "comienzo") var comienzo: String="",
    @ColumnInfo(name = "fin") var fin: String="",
    @ColumnInfo(name = "importancia") var importancia: String="",
    @ColumnInfo(name = "comentarios") var comentarios: String="",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0
){

    fun isEmpty(): Boolean {
        return actividad == "" && tipo == "" && comienzo == "" && fin == "" && importancia == "" && comentarios == ""
    }
    fun getAttributes(): List<String> = arrayListOf(date, actividad, tipo, comienzo, fin, importancia, comentarios, id.toString())

    fun setAttributes(indexDefaulValueActividad: Int, value: String) {
        when(indexDefaulValueActividad){
            0 -> date = value
            1 -> actividad = value
            2 -> tipo = value
            3 -> comienzo = value
            4 -> fin = value
            5 -> importancia = value
            6 -> comentarios = value
            7 -> id = value.toLong()
        }
    }

}
