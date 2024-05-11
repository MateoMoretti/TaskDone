package my.life.tracker.finanzas.interfaces

import androidx.room.*
import my.life.tracker.finanzas.model.Tag

@Dao
interface TagDao {

    @Upsert
    fun addTag(tag: Tag)
    @Query("SELECT * FROM Tag WHERE id = :id")
    fun getTagById(id:Long): Tag?

    @Delete
    fun deleteTag(tag: Tag)
    @Query("delete from Tag where id in (:idList)")
    fun deleteTags(idList: List<Long>)

    @Query("SELECT * FROM Tag")
    fun getTags() : List<Tag>

    @Query("SELECT * FROM Tag WHERE Tag.id IN (:tags)")
    fun getTagsByIdList(tags:List<Long>) : List<Tag>
}