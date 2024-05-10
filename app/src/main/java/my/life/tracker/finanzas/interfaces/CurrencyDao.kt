package my.life.tracker.finanzas.interfaces

import androidx.room.*
import my.life.tracker.finanzas.model.Currency

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