package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mis.finanzas.diarias.interfaces.RecordDao

class ViewModelFactory(private var context:Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecordViewModel::class.java)){
            return RecordViewModel(context) as T
        }
        throw java.lang.IllegalArgumentException("Viewmodel class not found bro.")
    }
}