package mis.finanzas.diarias.finanzas.interfaces

import androidx.room.*
import mis.finanzas.diarias.finanzas.model.TagRecord

@Dao
interface TagRecordDao {

    @Upsert
    fun addTagRecord(tag: TagRecord)

    @Delete
    fun deleteTagRecord(tag: TagRecord)

    @Query("delete from TagRecord where id in (:idList)")
    fun deleteTagRecords(idList: List<Int>)

    @Query("SELECT * FROM TagRecord")
    fun getTagRecords() : List<TagRecord>

    @Query("SELECT * FROM TagRecord WHERE TagRecord.idRecord = :id")
    fun getAllTagRecordByRecordId(id:Int) : List<TagRecord>

    @Query("SELECT * FROM TagRecord WHERE TagRecord.idTag = :id")
    fun getAllTagRecordByTagId(id:Int) : List<TagRecord>

    @Query("SELECT * FROM TagRecord WHERE TagRecord.idRecord in (:idList)")
    fun getAllTagRecordByRecordIdList(idList:List<Int>) : List<TagRecord>


}