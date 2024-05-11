package my.life.tracker.finanzas.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Record")
data class Record (
    var date: String,
    var amount: Int,
    var reason: String,
    var isIncome: Boolean,
    var idCurrency: Long,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0
)