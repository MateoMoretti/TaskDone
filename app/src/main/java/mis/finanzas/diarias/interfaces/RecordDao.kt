package mis.finanzas.diarias.interfaces

import androidx.room.*
import mis.finanzas.diarias.model.Record

@Dao
interface RecordDao {

    @Upsert
    fun addRecord(record:Record)

    @Delete
    fun deleteRecord(record:Record)

    @Query("SELECT * FROM Record WHERE Record.date BETWEEN :desde AND :hasta ORDER BY Record.date DESC")
    fun getRecords(desde: Long, hasta: Long) : List<Record>

}