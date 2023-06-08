package mis.finanzas.diarias.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Tag_Gasto")
data class TagRecord (
    var id_tag: Int,
    var id_gasto: Int,
    @PrimaryKey(autoGenerate = true)
    var ID: Int = 0
)