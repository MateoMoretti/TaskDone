package mis.finanzas.diarias.finanzas.interfaces

import androidx.room.*
import mis.finanzas.diarias.finanzas.model.Currency
import mis.finanzas.diarias.finanzas.model.Record

@Dao
interface CurrencyDao {

    @Upsert
    fun addCurrency(currency: Currency)

    @Delete
    fun deleteCurrency(currency: Currency)

    @Query("SELECT * FROM Currency")
    fun getAllCurrency():List<Currency>

    @Query("SELECT * FROM Currency WHERE id = :id")
    fun getCurrencyById(id:Int):Currency?

    @Query("SELECT * FROM Currency WHERE name = :name")
    fun getCurrencyByName(name:String):Currency?
}