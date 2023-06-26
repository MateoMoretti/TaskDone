package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.database.MyDataBase
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.model.Record
import mis.finanzas.diarias.model.Tag
import mis.finanzas.diarias.model.TagRecord
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
    fun addRecord(record: Record): Long {
        // Update amount
        val currency = db.currencyDao.getCurrencyById(record.idCurrency)
        if(record.isIncome) currency!!.amount += record.amount
        else currency!!.amount -= record.amount
        db.currencyDao.addCurrency(currency!!)
        //add and return record
        return db.recordDao.addRecord(record)
    }

    fun deleteRecord(record: Record) {
        db.recordDao.deleteRecord(record)
        val tagRecords = getAllTagRecordByRecordId(record.id)
        for(tr in tagRecords){
            db.tagRecordDao.deleteTagRecord(tr)
        }
    }

    fun getRecords(from: Date, to: Date): List<Record> {
        return db.recordDao.getRecords(Utils.dateToString(from), Utils.dateToString(to))
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

    fun getCurrencyById(id:Int): Currency? {
        return db.currencyDao.getCurrencyById(id)
    }
    fun getCurrencyByName(name:String): Currency? {
        return db.currencyDao.getCurrencyByName(name)
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
    fun getTagsByIdList(tags:List<Int>): List<Tag> {
        return db.tagDao.getTagsByIdList(tags)
    }

    fun getAllTags(): List<Tag> {
        return db.tagDao.getTags()
    }

    // ----------- TAG-RECORD --------------
    fun addTagRecord(tagRecord: TagRecord) {
        db.tagRecordDao.addTagRecord(tagRecord)
    }
    fun deleteTagRecord(tagRecord: TagRecord) {
        db.tagRecordDao.deleteTagRecord(tagRecord)
    }
    fun getAllTagRecord(): List<TagRecord> {
        return db.tagRecordDao.getTagRecords()
    }
    fun getAllTagRecordByRecordId(id:Int): List<TagRecord> {
        return db.tagRecordDao.getAllTagRecordByRecordId(id)
    }
}