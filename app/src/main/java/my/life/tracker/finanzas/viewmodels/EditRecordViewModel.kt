package my.life.tracker.finanzas.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import my.life.tracker.finanzas.model.Record


class EditRecordViewModel : BaseRecordViewModel() {

    private val _record = MutableLiveData<Record?>()
    val record: LiveData<Record?> get() = _record

    fun setRecord(value: Record) {
        _record.value = value
    }

    fun getRecord(): Record? {
        return record.value
    }
    fun endEdit() {
        _record.value = null
    }
}