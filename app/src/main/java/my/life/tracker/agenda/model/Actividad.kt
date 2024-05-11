package my.life.tracker.agenda.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Actividad")
data class Actividad (
    @ColumnInfo(name = "actividad") var actividad: String,
    @ColumnInfo(name = "tipo") var tipo: String,
    @ColumnInfo(name = "comienza") var comienza: String,
    @ColumnInfo(name = "fin") var fin: String,
    @ColumnInfo(name = "importancia") var importancia: Int,
    @ColumnInfo(name = "comentarios") var comentarios: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0
)
