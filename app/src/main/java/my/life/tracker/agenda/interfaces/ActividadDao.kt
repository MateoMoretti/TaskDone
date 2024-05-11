package my.life.tracker.agenda.interfaces

import androidx.room.*
import my.life.tracker.agenda.model.Actividad



@Dao
interface ActividadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addActividad(actividad: Actividad) : Long
    @Query("SELECT * FROM Actividad WHERE id = :id")
    fun getActividadById(id:Int): Actividad?

    @Delete
    fun deleteActividad(actividad: Actividad)
    @Query("delete from Actividad where id in (:idList)")
    fun deleteActividades(idList: List<Int>)

    @Query("SELECT * FROM Actividad")
    fun getActividades() : List<Actividad>

    @Query("SELECT * FROM Actividad WHERE Actividad.id IN (:actividades)")
    fun getActividadesByIdList(actividades:List<Int>) : List<Actividad>
}