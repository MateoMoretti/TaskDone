package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.database.MyDataBase
import mis.finanzas.diarias.model.Record
import java.util.*


class RecordViewModel(val context:Context) : ViewModel() {

    private val db by lazy {
        Room.databaseBuilder(
            context,
            MyDataBase::class.java,
            "DATABASE"
        ).allowMainThreadQueries().build()
    }

        fun addRecord(record: Record) {
            db.recordDao.addRecord(record)
        }

        fun deleteRecord(record: Record) {
            db.recordDao.deleteRecord(record)
        }

        fun getRecords(desde: Date, hasta: Date): List<Record> {
            return db.recordDao.getRecords(desde.time, hasta.time)
        }
}