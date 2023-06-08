package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.database.CurrencyDatabase
import mis.finanzas.diarias.model.Currency


class CurrencyViewModel(val context:Context) : ViewModel() {

    private val db by lazy {
        Room.databaseBuilder(
            context,
            CurrencyDatabase::class.java,
            "DATABASE"
        ).allowMainThreadQueries().build()
    }

        fun addCurrency(currency: Currency) {
            db.dao.addCurrency(currency)
        }

        fun deleteCurrency(currency: Currency) {
            db.dao.deleteCurrency(currency)
        }

        fun getAllCurrency(): List<Currency> {
            return db.dao.getAllCurrency()
        }
}