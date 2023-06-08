package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.launch
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
            viewModelScope.launch {
                db.dao.addCurrency(currency)
            }
        }

        fun deleteCurrency(currency: Currency) {
            viewModelScope.launch {
                db.dao.deleteCurrency(currency)
            }
        }

        fun getAllCurrency(): List<Currency> {
            return db.dao.getAllCurrency()
        }
}