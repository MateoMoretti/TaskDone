package my.life.tracker.finanzas.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import my.life.tracker.Utils
import my.life.tracker.finanzas.model.Currency
import java.util.*
import javax.inject.Inject


@HiltViewModel
class AddRecordViewModel@Inject constructor() : BaseRecordViewModel() {

    private val _date = MutableLiveData<String>()
    val date:LiveData<String> get() = _date

    private val _currencyIndex = MutableLiveData<Currency>()
    val currencyIndex:LiveData<Currency> get() = _currencyIndex

    private val _amount = MutableLiveData<Int>()
    val amount:LiveData<Int> get() = _amount


    private val _reason = MutableLiveData<String>()
    val reason:LiveData<String> get() = _reason

    private val _income = MutableLiveData<Boolean>()
    val income:LiveData<Boolean> get() = _income


    fun setDate(value:String){
        _date.value = value
    }
    fun getDate():String{
        return date.value?:Utils.calendarToString(Calendar.getInstance())
    }

    fun setCurrency(value:Currency){
        _currencyIndex.value = value
    }
    fun getCurrency():Currency{
        return currencyIndex.value?:Currency("", 0f, "")
    }

    fun setAmount(value:Int){
        _amount.value = value
    }
    fun getAmount():Int{
        return amount.value?:0
    }

    fun setReason(value:String){
        _reason.value = value
    }
    fun getReason():String{
        return reason.value?:""
    }

    fun setIncome(value:Boolean){
        _income.value = value
    }
    fun getIncome():Boolean{
        return income.value?:false
    }

}