package my.life.tracker.agenda.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Actividad")
data class Actividad (
    @ColumnInfo(name = "actividad") var actividad: String="actividad",
    @ColumnInfo(name = "tipo") var tipo: String="tipo",
    @ColumnInfo(name = "comienzo") var comienzo: String="comienzo",
    @ColumnInfo(name = "fin") var fin: String="fin",
    @ColumnInfo(name = "importancia") var importancia: String="importancia",
    @ColumnInfo(name = "comentarios") var comentarios: String="comentarios",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0
)
