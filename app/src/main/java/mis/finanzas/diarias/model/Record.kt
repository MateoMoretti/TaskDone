package mis.finanzas.diarias.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Gasto")
data class Record (
    @ColumnInfo(name = "fecha") var fecha: String,
    @ColumnInfo(name = "id_moneda") var idMon: String,
    @ColumnInfo(name = "total_gasto") var totalGasto: Float,
    @ColumnInfo(name = "motivo") var motivo: String,
    @ColumnInfo(name = "ingreso") var ingreso: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int = 0
)