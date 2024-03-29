package mis.finanzas.diarias.finanzas.interfaces

import androidx.room.*
import mis.finanzas.diarias.finanzas.model.Tag

@Dao
interface TagDao {

    @Upsert
    fun addTag(tag: Tag)
    @Query("SELECT * FROM Tag WHERE id = :id")
    fun getTagById(id:Int): Tag?

    @Delete
    fun deleteTag(tag: Tag)
    @Query("delete from Tag where id in (:idList)")
    fun deleteTags(idList: List<Int>)

    @Query("SELECT * FROM Tag")
    fun getTags() : List<Tag>

    @Query("SELECT * FROM Tag WHERE Tag.id IN (:tags)")
    fun getTagsByIdList(tags:List<Int>) : List<Tag>
}