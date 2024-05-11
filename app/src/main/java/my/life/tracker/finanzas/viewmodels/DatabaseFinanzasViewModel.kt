package my.life.tracker.finanzas.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import my.life.tracker.App
import my.life.tracker.Utils
import my.life.tracker.finanzas.model.Currency
import my.life.tracker.finanzas.model.Record
import my.life.tracker.finanzas.model.Tag
import my.life.tracker.finanzas.model.TagRecord
import java.util.*
import java.util.stream.Collectors


class DatabaseFinanzasViewModel(app: Application) : AndroidViewModel(app) {

    private val database = (app as App).lifeTrackerDataBase
    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> get() = _currencies


    // ----------- RECORDS --------------
    fun addRecord(record: Record): Long {
        // Update amount
        val currency = database.currencyDao.getCurrencyById(record.idCurrency)
        if(record.isIncome) currency!!.amount += record.amount
        else currency!!.amount -= record.amount
        database.currencyDao.addCurrency(currency!!)
        return database.recordDao.addRecord(record)
    }
    fun updateRecord(oldRecord: Record, newRecord: Record): Long {
        // Update amount
        val currency = database.currencyDao.getCurrencyById(newRecord.idCurrency)
        currency!!.amount += if(oldRecord.isIncome) -oldRecord.amount else oldRecord.amount
        currency!!.amount += if(newRecord.isIncome) newRecord.amount else -newRecord.amount

        database.currencyDao.addCurrency(currency!!)
        return database.recordDao.addRecord(newRecord)
    }

    fun deleteRecord(record: Record) {
        database.recordDao.deleteRecord(record)
        val tagRecordsId = getAllTagRecordByRecordId(record.id)
            .stream()
            .map { it.id }
            .collect(Collectors.toList())

        deleteTagRecords(tagRecordsId)
    }

    fun getRecords(from: Date, to: Date): List<Record> {
        return database.recordDao.getRecords(Utils.dateToString(from), Utils.dateToString(to))
    }
    fun getRecordById(id:Long): Record {
        return database.recordDao.getRecordById(id)
    }

    // ----------- CURRENCIES --------------
    fun addCurrency(currency: Currency) {
        database.currencyDao.addCurrency(currency)
        refreshAllCurrency()
    }

    fun deleteCurrency(currency: Currency) {
        database.currencyDao.deleteCurrency(currency)
        refreshAllCurrency()
    }

    fun getCurrencyById(id:Long): Currency? {
        return database.currencyDao.getCurrencyById(id)
    }
    fun getCurrencyByName(name:String): Currency? {
        return database.currencyDao.getCurrencyByName(name)
    }

    fun getAllCurrency(): List<Currency> {
        _currencies.value?.let { return it }
        return refreshAllCurrency()
    }

    fun refreshAllCurrency(): List<Currency>{
        val result = database.currencyDao.getAllCurrency()
        _currencies.value = result
        return result
    }

    // ----------- TAGS --------------
    fun addTag(tag: Tag) {
        database.tagDao.addTag(tag)
    }
    fun getTagById(id:Long):Tag? {
        return database.tagDao.getTagById(id)
    }

    fun deleteTag(tag: Tag) {
        val tagRecordsId = getAllTagRecordByTagId(tag.id)
            .stream()
            .map { it.id }
            .collect(Collectors.toList())

        deleteTagRecords(tagRecordsId)
        database.tagDao.deleteTag(tag)
    }
    fun getTagsByIdList(tags:List<Long>): List<Tag> {
        return database.tagDao.getTagsByIdList(tags)
    }

    fun getAllTags(): List<Tag> {
        return database.tagDao.getTags()
    }

    // ----------- TAG-RECORD --------------
    fun addTagRecord(tagRecord: TagRecord) {
        database.tagRecordDao.addTagRecord(tagRecord)
    }
    fun deleteTagRecord(tagRecord: TagRecord) {
        database.tagRecordDao.deleteTagRecord(tagRecord)
    }
    fun deleteTagRecords(tagRecordIdList: List<Long>) {
        database.tagRecordDao.deleteTagRecords(tagRecordIdList)
    }
    fun getAllTagRecord(): List<TagRecord> {
        return database.tagRecordDao.getTagRecords()
    }
    fun getAllTagRecordByRecordId(id:Long): List<TagRecord> {
        return database.tagRecordDao.getAllTagRecordByRecordId(id)
    }
    fun getAllTagRecordByTagId(id:Long): List<TagRecord> {
        return database.tagRecordDao.getAllTagRecordByTagId(id)
    }
    fun getAllTagRecordByRecordIdList(idList:List<Long>): List<TagRecord> {
        return database.tagRecordDao.getAllTagRecordByRecordIdList(idList)
    }
}