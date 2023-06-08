package mis.finanzas.diarias.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Tag_Gasto")
data class TagRecord (
    var idTag: Int,
    var idGasto: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)