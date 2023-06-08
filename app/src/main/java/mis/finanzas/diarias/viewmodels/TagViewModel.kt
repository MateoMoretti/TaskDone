package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import mis.finanzas.diarias.database.MyDataBase
import mis.finanzas.diarias.model.Tag


class TagViewModel(val context:Context) : ViewModel() {

    private val db by lazy {
        Room.databaseBuilder(
            context,
            MyDataBase::class.java,
            "DATABASE"
        ).allowMainThreadQueries().build()
    }

        fun addTag(tag: Tag) {
            db.tagDao.addTag(tag)
        }

        fun deleteTag(tag: Tag) {
            db.tagDao.deleteTag(tag)
        }

        fun getAllTags(): List<Tag> {
            return db.tagDao.getTags()
        }
}