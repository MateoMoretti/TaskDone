package mis.finanzas.diarias.finanzas.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Tag")
data class Tag (
    @ColumnInfo(name = "nombre") var nombre: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int = 0
)