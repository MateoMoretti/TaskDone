package my.life.tracker.finanzas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("TagRecord")
data class TagRecord (
    var idTag: Long,
    var idRecord: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)