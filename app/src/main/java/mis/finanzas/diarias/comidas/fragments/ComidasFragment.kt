package mis.finanzas.diarias.comidas.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.taskdone.R
import com.example.taskdone.databinding.FragmentComidasBinding
import mis.finanzas.diarias.ActivityMain
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewmodelFactory
import mis.finanzas.diarias.finanzas.viewmodels.AddRecordViewModel
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class ComidasFragment : Fragment() {
    private lateinit var binding: FragmentComidasBinding
    private val databaseViewModel: DatabaseViewModel by activityViewModels{DatabaseViewmodelFactory(requireContext())}
    private val recordViewModel: AddRecordViewModel by activityViewModels()

    var tags: ArrayList<String>? = null

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComidasBinding.inflate(inflater, container, false)
        (activity as ActivityMain).updateFragment(R.id.comidasFragment)

        return binding.root
    }

}