package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.database.MyDataBase
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.model.Record
import mis.finanzas.diarias.model.Tag
import java.util.*


class DatabaseViewModel(val context:Context) : ViewModel() {

    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> get() = _currencies

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
        val currency = db.currencyDao.getCurrencyById(record.idCurrency)[0]
        if(record.isIncome) currency.amount += record.amount
        else currency.amount -= record.amount
        db.currencyDao.addCurrency(currency)
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
        refreshAllCurrency()
    }

    fun deleteCurrency(currency: Currency) {
        db.currencyDao.deleteCurrency(currency)
        refreshAllCurrency()
    }

    fun getAllCurrency(): List<Currency> {
        _currencies.value?.let { return it }
        return refreshAllCurrency()
    }

    fun refreshAllCurrency(): List<Currency>{
        val result = db.currencyDao.getAllCurrency()
        _currencies.value = result
        return result
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