package mis.finanzas.diarias.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("TagRecord")
data class TagRecord (
    var idTag: Int,
    var idRecord: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)