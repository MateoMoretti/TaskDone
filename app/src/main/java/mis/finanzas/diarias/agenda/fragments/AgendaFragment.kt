package mis.finanzas.diarias.agenda.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.taskdone.databinding.FragmentAgendaPrincipalBinding
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewmodelFactory
import mis.finanzas.diarias.finanzas.viewmodels.AddRecordViewModel
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class AgendaFragment : Fragment() {
    private lateinit var binding: FragmentAgendaPrincipalBinding
    private val databaseViewModel: DatabaseViewModel by activityViewModels{DatabaseViewmodelFactory(requireContext())}
    private val recordViewModel: AddRecordViewModel by activityViewModels()

    var tags: ArrayList<String>? = null

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgendaPrincipalBinding.inflate(inflater, container, false)

        return binding.root
    }

}
