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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import my.life.tracker.App
import my.life.tracker.R
import my.life.tracker.agenda.interfaces.ActividadDao
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.database.LifeTrackerDataBase
import javax.inject.Inject


@HiltViewModel
class AgendaViewModel@Inject constructor(private val actividadDao: ActividadDao) : ViewModel () {

    private val _actividades = MutableLiveData<ArrayList<Actividad>>()
    val actividades:LiveData<ArrayList<Actividad>> get() = _actividades

    private val _idPreviousLineSelected = MutableLiveData<View>()
    val idPreviousLineSelected:LiveData<View> get() = _idPreviousLineSelected

    private val _idLineSelected = MutableLiveData<View>()
    val idLineSelected:LiveData<View> get() = _idLineSelected

    init {
        _actividades.value = arrayListOf()
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
    fun addActividad(actividad: Actividad): Long {
        val newActividad = actividadDao.addActividad(actividad)
        return newActividad
    }

}