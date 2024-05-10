package my.life.tracker.finanzas.database

import androidx.room.Database
import androidx.room.RoomDatabase
import my.life.tracker.finanzas.interfaces.CurrencyDao
import my.life.tracker.finanzas.interfaces.RecordDao
import my.life.tracker.finanzas.interfaces.TagDao
import my.life.tracker.finanzas.interfaces.TagRecordDao
import my.life.tracker.finanzas.model.Currency
import my.life.tracker.finanzas.model.Record
import my.life.tracker.finanzas.model.Tag
import my.life.tracker.finanzas.model.TagRecord


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