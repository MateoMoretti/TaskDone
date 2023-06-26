package mis.finanzas.diarias.viewmodels

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskdone.R
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.model.Tag
import java.util.*
import kotlin.collections.ArrayList


class AddRecordViewModel : ViewModel() {

    private val _date = MutableLiveData<String>()
    val date:LiveData<String> get() = _date

    private val _currencyIndex = MutableLiveData<Currency>()
    val currencyIndex:LiveData<Currency> get() = _currencyIndex

    private val _amount = MutableLiveData<Int>()
    val amount:LiveData<Int> get() = _amount

    private val _tagsSelected = MutableLiveData<ArrayList<Tag>>().apply { postValue(ArrayList()) }
    val tagsSelected:LiveData<ArrayList<Tag>> get() = _tagsSelected

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

    fun onClickTag(tag:Tag){
        if(tagsSelected.value?.contains(tag) == true) _tagsSelected.value!!.remove(tag)
        else _tagsSelected.value!!.add(tag)
    }

    fun setSelectedTags(value: ArrayList<Tag>) {
        _tagsSelected.value = value
    }
    fun getSelectedTags(): ArrayList<Tag> {
        return tagsSelected.value?: arrayListOf()
    }

    fun getTagsString(resources:Resources):String{
        tagsSelected.value.let {
            if (it != null && it.isNotEmpty()) {
                return Utils.arrayListToString(it.map { tag -> tag.nombre })
            }
        }
        return resources.getString(R.string.sin_seleccionados)
    }

    fun setIncome(value:Boolean){
        _income.value = value
    }
    fun getIncome():Boolean{
        return income.value?:false
    }

}