package mis.finanzas.diarias.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mis.finanzas.diarias.interfaces.CurrencyDao
import mis.finanzas.diarias.model.Currency


@Database(
    entities = [Currency::class],
    version = 1
)
abstract class CurrencyDatabase : RoomDatabase() {

    abstract val dao: CurrencyDao
}