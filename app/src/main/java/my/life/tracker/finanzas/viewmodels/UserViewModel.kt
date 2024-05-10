package my.life.tracker.finanzas.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class UserViewModel : ViewModel() {

    private val _id = MutableLiveData(0)
    val id: LiveData<Int> get() = _id


    fun setId(value:Int){
        _id.value = value
    }
    fun getId():Int{
        return id.value?:0
    }
}