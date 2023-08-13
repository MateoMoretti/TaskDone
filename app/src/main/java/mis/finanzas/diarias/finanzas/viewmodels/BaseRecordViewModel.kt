package mis.finanzas.diarias.finanzas.viewmodels

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskdone.R
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.finanzas.interfaces.RecordInterface
import mis.finanzas.diarias.finanzas.model.Currency
import mis.finanzas.diarias.finanzas.model.Tag
import java.util.*
import kotlin.collections.ArrayList


open class BaseRecordViewModel : ViewModel(), RecordInterface {

    private val _tagsSelected = MutableLiveData<ArrayList<Tag>>().apply { postValue(ArrayList()) }
    val tagsSelected:LiveData<ArrayList<Tag>> get() = _tagsSelected

    override fun onClickTag(tag:Tag){
        if(tagsSelected.value?.contains(tag) == true) _tagsSelected.value!!.remove(tag)
        else _tagsSelected.value!!.add(tag)
    }

    fun setSelectedTags(value: ArrayList<Tag>) {
        _tagsSelected.value = value
    }
    override fun getSelectedTags(): ArrayList<Tag> {
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
}