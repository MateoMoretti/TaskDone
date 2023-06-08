package mis.finanzas.diarias.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mis.finanzas.diarias.interfaces.RecordDao
import mis.finanzas.diarias.model.Record


@Database(
    entities = [Record::class],
    version = 1
)
abstract class RecordDatabase : RoomDatabase() {

    abstract val dao: RecordDao
}