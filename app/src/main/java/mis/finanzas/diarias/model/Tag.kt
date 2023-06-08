package mis.finanzas.diarias.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Tag")
data class Tag (
    var nombre: String,
    @PrimaryKey(autoGenerate = true)
    var ID: Int = 0
)