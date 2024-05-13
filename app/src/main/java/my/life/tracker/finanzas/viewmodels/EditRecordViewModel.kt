package my.life.tracker.finanzas.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import my.life.tracker.finanzas.model.Record
import javax.inject.Inject


@HiltViewModel
class EditRecordViewModel@Inject constructor() : BaseRecordViewModel() {

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