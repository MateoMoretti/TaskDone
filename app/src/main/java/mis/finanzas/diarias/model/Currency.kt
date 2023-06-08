package mis.finanzas.diarias.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Moneda")
data class Currency (
    val nombre: String,
    val cantidad: String,
    val simbolo: String,
    @PrimaryKey(autoGenerate = true)
    val ID: Int = 0
)