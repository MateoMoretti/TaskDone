package mis.finanzas.diarias.finanzas.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mis.finanzas.diarias.finanzas.interfaces.CurrencyDao
import mis.finanzas.diarias.finanzas.interfaces.RecordDao
import mis.finanzas.diarias.finanzas.interfaces.TagDao
import mis.finanzas.diarias.finanzas.interfaces.TagRecordDao
import mis.finanzas.diarias.finanzas.model.Currency
import mis.finanzas.diarias.finanzas.model.Record
import mis.finanzas.diarias.finanzas.model.Tag
import mis.finanzas.diarias.finanzas.model.TagRecord


@Database(
    entities = [Record::class, Currency::class, Tag::class, TagRecord::class],
    version = 1
)
abstract class MyDataBase : RoomDatabase() {

    abstract val tagDao: TagDao
    abstract val tagRecordDao: TagRecordDao
    abstract val recordDao: RecordDao
    abstract val currencyDao: CurrencyDao
}