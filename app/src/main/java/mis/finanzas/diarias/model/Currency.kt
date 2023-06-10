package mis.finanzas.diarias.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Moneda")
data class Currency (
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "cantidad") val cantidad: Float,
    @ColumnInfo(name = "simbolo") val simbolo: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0
)