package mis.finanzas.diarias.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TagViewmodelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TagViewModel(context) as T
    }

}