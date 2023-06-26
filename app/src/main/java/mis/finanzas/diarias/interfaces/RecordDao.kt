package mis.finanzas.diarias.interfaces

import androidx.room.*
import mis.finanzas.diarias.model.Record

@Dao
interface RecordDao {

    @Upsert
    fun addRecord(record:Record) :Long

    @Delete
    fun deleteRecord(record:Record)

    @Query("SELECT * FROM Record WHERE Record.date BETWEEN :from AND :to ORDER BY Record.date DESC")
    fun getRecords(from: String, to: String) : List<Record>

}