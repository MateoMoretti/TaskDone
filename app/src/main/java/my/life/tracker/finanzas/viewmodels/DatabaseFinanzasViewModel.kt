package my.life.tracker.finanzas.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import my.life.tracker.Utils
import my.life.tracker.finanzas.interfaces.CurrencyDao
import my.life.tracker.finanzas.interfaces.RecordDao
import my.life.tracker.finanzas.interfaces.TagDao
import my.life.tracker.finanzas.interfaces.TagRecordDao
import my.life.tracker.finanzas.model.Currency
import my.life.tracker.finanzas.model.Record
import my.life.tracker.finanzas.model.Tag
import my.life.tracker.finanzas.model.TagRecord
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject


@HiltViewModel
class DatabaseFinanzasViewModel@Inject constructor(private val currencyDao: CurrencyDao,
                                                   private val recordDao: RecordDao,
                                                   private val tagDao: TagDao,
                                                   private val tagRecordDao: TagRecordDao,
                                                   app: Application)
    : AndroidViewModel(app) {

    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> get() = _currencies


    // ----------- RECORDS --------------
    fun addRecord(record: Record): Long {
        // Update amount
        val currency = currencyDao.getCurrencyById(record.idCurrency)
        if(record.isIncome) currency!!.amount += record.amount
        else currency!!.amount -= record.amount
        currencyDao.addCurrency(currency!!)
        return recordDao.addRecord(record)
    }
    fun updateRecord(oldRecord: Record, newRecord: Record): Long {
        // Update amount
        val currency = currencyDao.getCurrencyById(newRecord.idCurrency)
        currency!!.amount += if(oldRecord.isIncome) -oldRecord.amount else oldRecord.amount
        currency!!.amount += if(newRecord.isIncome) newRecord.amount else -newRecord.amount

        currencyDao.addCurrency(currency!!)
        return recordDao.addRecord(newRecord)
    }

    fun deleteRecord(record: Record) {
        recordDao.deleteRecord(record)
        val tagRecordsId = getAllTagRecordByRecordId(record.id)
            .stream()
            .map { it.id }
            .collect(Collectors.toList())

        deleteTagRecords(tagRecordsId)
    }

    fun getRecords(from: Date, to: Date): List<Record> {
        return recordDao.getRecords(Utils.dateToString(from), Utils.dateToString(to))
    }
    fun getRecordById(id:Long): Record {
        return recordDao.getRecordById(id)
    }

    // ----------- CURRENCIES --------------
    fun addCurrency(currency: Currency) {
        currencyDao.addCurrency(currency)
        refreshAllCurrency()
    }

    fun deleteCurrency(currency: Currency) {
        currencyDao.deleteCurrency(currency)
        refreshAllCurrency()
    }

    fun getCurrencyById(id:Long): Currency? {
        return currencyDao.getCurrencyById(id)
    }
    fun getCurrencyByName(name:String): Currency? {
        return currencyDao.getCurrencyByName(name)
    }

    fun getAllCurrency(): List<Currency> {
        _currencies.value?.let { return it }
        return refreshAllCurrency()
    }

    fun refreshAllCurrency(): List<Currency>{
        val result = currencyDao.getAllCurrency()
        _currencies.value = result
        return result
    }

    // ----------- TAGS --------------
    fun addTag(tag: Tag) {
        tagDao.addTag(tag)
    }
    fun getTagById(id:Long):Tag? {
        return tagDao.getTagById(id)
    }

    fun deleteTag(tag: Tag) {
        val tagRecordsId = getAllTagRecordByTagId(tag.id)
            .stream()
            .map { it.id }
            .collect(Collectors.toList())

        deleteTagRecords(tagRecordsId)
        tagDao.deleteTag(tag)
    }
    fun getTagsByIdList(tags:List<Long>): List<Tag> {
        return tagDao.getTagsByIdList(tags)
    }

    fun getAllTags(): List<Tag> {
        return tagDao.getTags()
    }

    // ----------- TAG-RECORD --------------
    fun addTagRecord(tagRecord: TagRecord) {
        tagRecordDao.addTagRecord(tagRecord)
    }
    fun deleteTagRecord(tagRecord: TagRecord) {
        tagRecordDao.deleteTagRecord(tagRecord)
    }
    fun deleteTagRecords(tagRecordIdList: List<Long>) {
        tagRecordDao.deleteTagRecords(tagRecordIdList)
    }
    fun getAllTagRecord(): List<TagRecord> {
        return tagRecordDao.getTagRecords()
    }
    fun getAllTagRecordByRecordId(id:Long): List<TagRecord> {
        return tagRecordDao.getAllTagRecordByRecordId(id)
    }
    fun getAllTagRecordByTagId(id:Long): List<TagRecord> {
        return tagRecordDao.getAllTagRecordByTagId(id)
    }
    fun getAllTagRecordByRecordIdList(idList:List<Long>): List<TagRecord> {
        return tagRecordDao.getAllTagRecordByRecordIdList(idList)
    }
}