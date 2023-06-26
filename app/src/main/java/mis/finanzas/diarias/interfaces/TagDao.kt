package mis.finanzas.diarias.interfaces

import androidx.room.*
import mis.finanzas.diarias.model.Tag

@Dao
interface TagDao {

    @Upsert
    fun addTag(tag: Tag)

    @Delete
    fun deleteTag(tag: Tag)

    @Query("SELECT * FROM Tag")
    fun getTags() : List<Tag>

    @Query("SELECT * FROM Tag WHERE Tag.id IN (:tags)")
    fun getTagsByIdList(tags:List<Int>) : List<Tag>
}