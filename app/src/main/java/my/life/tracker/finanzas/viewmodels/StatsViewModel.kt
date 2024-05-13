package my.life.tracker.finanzas.viewmodels

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import my.life.tracker.R
import my.life.tracker.Utils
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StatsViewModel@Inject constructor() : ViewModel() {

    private val _date_desde = MutableLiveData<Date>()
    val dateDesde: LiveData<Date> get() = _date_desde

    private val _date_hasta = MutableLiveData<Date>()
    val dateHasta: LiveData<Date> get() = _date_hasta

    fun setDesdeDate(value: Date) {
        _date_desde.value = value
    }
    fun setHastaDate(value: Date) {
        _date_hasta.value = value
    }

    fun getDesdeDate(): Date {
        dateDesde.value?.let {
            return it
        }
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, -30)
        return c.time
    }


    fun getHastaDate(): Date {
        dateHasta.value?.let { return it }
        return Calendar.getInstance().time
    }
    fun formatDate(date: Date, resources: Resources): String {
        if(Utils.isMonthAgo(Utils.dateToString(date))) return resources.getString(R.string.hace_un_mes)
        if(Utils.isHoy(Utils.dateToString(date))) return resources.getString(R.string.hoy)
        return Utils.dateToString(date)
    }

    fun getDesdeString(resources: Resources):String = formatDate(getDesdeDate(), resources)
    fun getHastaString(resources: Resources):String = formatDate(getHastaDate(), resources)

}