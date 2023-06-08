package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.database.TagDatabase
import mis.finanzas.diarias.model.Tag


class TagViewModel(val context:Context) : ViewModel() {

    private val db by lazy {
        Room.databaseBuilder(
            context,
            TagDatabase::class.java,
            "DATABASE"
        ).allowMainThreadQueries().build()
    }

        fun addTag(tag: Tag) {
            db.dao.addTag(tag)
        }

        fun deleteTag(tag: Tag) {
            db.dao.deleteTag(tag)
        }

        fun getAllTags(): List<Tag> {
            return db.dao.getTags()
        }
}