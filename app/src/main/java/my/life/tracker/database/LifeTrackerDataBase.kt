package my.life.tracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.interfaces.ActividadDao
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.finanzas.interfaces.CurrencyDao
import my.life.tracker.finanzas.interfaces.RecordDao
import my.life.tracker.finanzas.interfaces.TagDao
import my.life.tracker.finanzas.interfaces.TagRecordDao
import my.life.tracker.finanzas.model.Currency
import my.life.tracker.finanzas.model.Record
import my.life.tracker.finanzas.model.Tag
import my.life.tracker.finanzas.model.TagRecord


@Database(
    entities = [Record::class, Currency::class, Tag::class, TagRecord::class,
                Actividad::class],
    version = 1
)
abstract class LifeTrackerDataBase : RoomDatabase() {

    //AGENDA
    abstract fun actividadDao() : ActividadDao

    //FINANZAS
    abstract fun tagDao() : TagDao
    abstract fun tagRecordDao() : TagRecordDao
    abstract fun recordDao() : RecordDao
    abstract fun currencyDao() : CurrencyDao
}