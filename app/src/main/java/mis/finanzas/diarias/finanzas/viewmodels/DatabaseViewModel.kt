package mis.finanzas.diarias.finanzas.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.finanzas.database.MyDataBase
import mis.finanzas.diarias.finanzas.model.Currency
import mis.finanzas.diarias.finanzas.model.Record
import mis.finanzas.diarias.finanzas.model.Tag
import mis.finanzas.diarias.finanzas.model.TagRecord
import java.util.*
import java.util.stream.Collectors


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
        return db.recordDao.addRecord(record)
    }
    fun updateRecord(oldRecord: Record, newRecord: Record): Long {
        // Update amount
        val currency = db.currencyDao.getCurrencyById(newRecord.idCurrency)
        currency!!.amount += if(oldRecord.isIncome) -oldRecord.amount else oldRecord.amount
        currency!!.amount += if(newRecord.isIncome) newRecord.amount else -newRecord.amount

        db.currencyDao.addCurrency(currency!!)
        return db.recordDao.addRecord(newRecord)
    }

    fun deleteRecord(record: Record) {
        db.recordDao.deleteRecord(record)
        val tagRecordsId = getAllTagRecordByRecordId(record.id)
            .stream()
            .map { it.id }
            .collect(Collectors.toList())

        deleteTagRecords(tagRecordsId)
    }

    fun getRecords(from: Date, to: Date): List<Record> {
        return db.recordDao.getRecords(Utils.dateToString(from), Utils.dateToString(to))
    }
    fun getRecordById(id:Int): Record {
        return db.recordDao.getRecordById(id)
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
    fun getTagById(id:Int):Tag? {
        return db.tagDao.getTagById(id)
    }

    fun deleteTag(tag: Tag) {
        val tagRecordsId = getAllTagRecordByTagId(tag.id)
            .stream()
            .map { it.id }
            .collect(Collectors.toList())

        deleteTagRecords(tagRecordsId)
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
    fun deleteTagRecords(tagRecordIdList: List<Int>) {
        db.tagRecordDao.deleteTagRecords(tagRecordIdList)
    }
    fun getAllTagRecord(): List<TagRecord> {
        return db.tagRecordDao.getTagRecords()
    }
    fun getAllTagRecordByRecordId(id:Int): List<TagRecord> {
        return db.tagRecordDao.getAllTagRecordByRecordId(id)
    }
    fun getAllTagRecordByTagId(id:Int): List<TagRecord> {
        return db.tagRecordDao.getAllTagRecordByTagId(id)
    }
    fun getAllTagRecordByRecordIdList(idList:List<Int>): List<TagRecord> {
        return db.tagRecordDao.getAllTagRecordByRecordIdList(idList)
    }
}