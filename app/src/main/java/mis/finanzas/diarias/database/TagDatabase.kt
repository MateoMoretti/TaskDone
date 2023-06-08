package mis.finanzas.diarias.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mis.finanzas.diarias.interfaces.TagDao
import mis.finanzas.diarias.model.Tag


@Database(
    entities = [Tag::class],
    version = 1
)
abstract class TagDatabase : RoomDatabase() {

    abstract val dao: TagDao
}