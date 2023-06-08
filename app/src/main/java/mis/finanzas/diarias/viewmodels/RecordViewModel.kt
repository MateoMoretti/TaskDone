package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.launch
import mis.finanzas.diarias.database.RecordDatabase
import mis.finanzas.diarias.model.Record
import java.util.*


class RecordViewModel(val context:Context) : ViewModel() {

    private val db by lazy {
        Room.databaseBuilder(
            context,
            RecordDatabase::class.java,
            "DATABASE"
        ).allowMainThreadQueries().build()
    }

        fun addRecord(record: Record) {
            viewModelScope.launch {
                db.dao.addRecord(record)
            }
        }

        fun deleteRecord(record: Record) {
            viewModelScope.launch {
                db.dao.deleteRecord(record)
            }
        }

        fun getRecords(desde: Date, hasta: Date): List<Record> {
            return db.dao.getRecords(desde.time, hasta.time)
        }
}