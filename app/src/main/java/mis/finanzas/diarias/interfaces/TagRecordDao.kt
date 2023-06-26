package mis.finanzas.diarias.interfaces

import androidx.room.*
import mis.finanzas.diarias.model.TagRecord

@Dao
interface TagRecordDao {

    @Upsert
    fun addTagRecord(tag: TagRecord)

    @Delete
    fun deleteTagRecord(tag: TagRecord)

    @Query("SELECT * FROM TagRecord")
    fun getTagRecords() : List<TagRecord>

    @Query("SELECT * FROM TagRecord WHERE TagRecord.idRecord = :id")
    fun getAllTagRecordByRecordId(id:Int) : List<TagRecord>


}