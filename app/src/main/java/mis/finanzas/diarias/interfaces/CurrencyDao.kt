package mis.finanzas.diarias.interfaces

import androidx.room.*
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.model.Record

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