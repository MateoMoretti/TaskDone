package my.life.tracker.agenda.viewmodels

import android.app.Application
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.findFragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.life.tracker.App
import my.life.tracker.R
import my.life.tracker.agenda.model.Actividad


class AgendaViewModel(app: Application) : AndroidViewModel(app) {

    private val database = (app as App).lifeTrackerDataBase

    private val _actividades = MutableLiveData<MutableMap<Actividad, View>>()
    val actividades:LiveData<MutableMap<Actividad, View>> get() = _actividades


    private val _idLineSelected = MutableLiveData<Long>()
    val idLineSelected:LiveData<Long> get() = _idLineSelected

    init {
        _actividades.value = mutableMapOf()
    }


    fun selectLine(context: Context, value:Long){
        val actividadAsociada = _actividades.value!!.filter { it.key.id == _idLineSelected.value }.map { it.key }
        if(actividadAsociada.isNotEmpty()){
            _actividades.value!![actividadAsociada[0]]!!.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
       _idLineSelected.value = value

    }

    fun addActividadToScreen(actividad: Actividad, view: View){
        _actividades.value!![actividad] = view
    }
    fun addActividad(actividad: Actividad): Long {
        val newActividad = database.actividadDao.addActividad(actividad)
        return newActividad
    }

}