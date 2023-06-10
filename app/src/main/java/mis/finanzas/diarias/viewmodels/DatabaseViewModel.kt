package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.database.MyDataBase
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.model.Record
import mis.finanzas.diarias.model.Tag
import java.util.*


class DatabaseViewModel(val context:Context) : ViewModel() {

    private val db by lazy {
        Room.databaseBuilder(

            context,
            MyDataBase::class.java,
            "DATABASE"
        ).allowMainThreadQueries().build()
    }

    // ----------- RECORDS --------------
    fun addRecord(record: Record) {
        db.recordDao.addRecord(record)
    }

    fun deleteRecord(record: Record) {
        db.recordDao.deleteRecord(record)
    }

    fun getRecords(desde: Date, hasta: Date): List<Record> {
        return db.recordDao.getRecords(desde.time, hasta.time)
    }

    // ----------- CURRENCIES --------------
    fun addCurrency(currency: Currency) {
        db.currencyDao.addCurrency(currency)
    }

    fun deleteCurrency(currency: Currency) {
        db.currencyDao.deleteCurrency(currency)
    }

    fun getAllCurrency(): List<Currency> {
        return db.currencyDao.getAllCurrency()
    }

    // ----------- TAGS --------------
    fun addTag(tag: Tag) {
        db.tagDao.addTag(tag)
    }

    fun deleteTag(tag: Tag) {
        db.tagDao.deleteTag(tag)
    }

    fun getAllTags(): List<Tag> {
        return db.tagDao.getTags()
    }
}