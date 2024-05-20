package my.life.tracker.agenda.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import my.life.tracker.agenda.interfaces.ActividadDao
import my.life.tracker.agenda.model.Actividad
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class AgendaViewModel@Inject constructor(private val actividadDao: ActividadDao) : ViewModel () {

    private val _actividades = MutableLiveData<ArrayList<Actividad>>()
    val actividades:LiveData<ArrayList<Actividad>> get() = _actividades

    private val _idPreviousLineSelected = MutableLiveData<View>()
    val idPreviousLineSelected:LiveData<View> get() = _idPreviousLineSelected

    private val _idLineSelected = MutableLiveData<View>()
    val idLineSelected:LiveData<View> get() = _idLineSelected

    private val calendar = Calendar.getInstance()

    private val _date = MutableLiveData<Date>().default(calendar.time)
    val date:LiveData<Date> get() = _date

    init {
        _actividades.value = arrayListOf()
    }

    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

    fun getCalendar() = calendar

    fun setDate(date: Int){
        calendar.set(Calendar.DATE, date)
        _date.value = calendar.time
    }
    fun setDay(day: Int){
        calendar.set(Calendar.DAY_OF_MONTH, day)
        _date.value = calendar.time
    }
    fun setMonth(month: Int){
        calendar.set(Calendar.MONTH, month)
        _date.value = calendar.time
    }

    fun setYear(year: Int) {
        calendar.set(Calendar.YEAR, year)
        _date.value = calendar.time
    }

    fun selectLine(view: View){
        if(_idLineSelected.value != null){
            _idPreviousLineSelected.value = view
        }
       _idLineSelected.value = view
    }

    fun addActividadToScreen(actividad: Actividad,){
        _actividades.value!!.add(actividad)
    }
    fun addActividad(actividad: Actividad): Long = actividadDao.addActividad(actividad)

}