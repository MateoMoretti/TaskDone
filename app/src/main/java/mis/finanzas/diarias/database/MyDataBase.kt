package mis.finanzas.diarias.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mis.finanzas.diarias.interfaces.CurrencyDao
import mis.finanzas.diarias.interfaces.RecordDao
import mis.finanzas.diarias.interfaces.TagDao
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.model.Record
import mis.finanzas.diarias.model.Tag


@Database(
    entities = [Record::class, Currency::class, Tag::class],
    version = 1
)
abstract class MyDataBase : RoomDatabase() {

    abstract val tagDao: TagDao
    abstract val recordDao: RecordDao
    abstract val currencyDao: CurrencyDao
}