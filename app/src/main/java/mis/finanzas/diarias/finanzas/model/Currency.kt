package mis.finanzas.diarias.finanzas.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Currency")
data class Currency (
    var name: String,
    var amount: Float,
    var symbol: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0
)