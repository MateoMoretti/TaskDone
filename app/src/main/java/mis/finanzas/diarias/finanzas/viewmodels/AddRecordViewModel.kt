package mis.finanzas.diarias.finanzas.viewmodels

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskdone.R
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.finanzas.model.Currency
import mis.finanzas.diarias.finanzas.model.Tag
import java.util.*
import kotlin.collections.ArrayList


class AddRecordViewModel : BaseRecordViewModel() {

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