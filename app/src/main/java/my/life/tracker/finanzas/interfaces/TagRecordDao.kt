package my.life.tracker.finanzas.interfaces

import androidx.room.*
import my.life.tracker.finanzas.model.TagRecord

@Dao
interface TagRecordDao {

    @Upsert
    fun addTagRecord(tag: TagRecord)

    @Delete
    fun deleteTagRecord(tag: TagRecord)

    @Query("delete from TagRecord where id in (:idList)")
    fun deleteTagRecords(idList: List<Long>)

    @Query("SELECT * FROM TagRecord")
    fun getTagRecords() : List<TagRecord>

    @Query("SELECT * FROM TagRecord WHERE TagRecord.idRecord = :id")
    fun getAllTagRecordByRecordId(id:Long) : List<TagRecord>

    @Query("SELECT * FROM TagRecord WHERE TagRecord.idTag = :id")
    fun getAllTagRecordByTagId(id:Long) : List<TagRecord>

    @Query("SELECT * FROM TagRecord WHERE TagRecord.idRecord in (:idList)")
    fun getAllTagRecordByRecordIdList(idList:List<Long>) : List<TagRecord>


}