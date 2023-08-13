package mis.finanzas.diarias.agenda.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.taskdone.R
import android.view.Gravity
import android.text.TextWatcher
import android.text.Editable
import mis.finanzas.diarias.DatePickerFragment
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.databinding.FragmentAgendaBinding
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.finanzas.ActivityFinanzas
import mis.finanzas.diarias.finanzas.model.Record
import mis.finanzas.diarias.finanzas.model.TagRecord
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewmodelFactory
import mis.finanzas.diarias.finanzas.viewmodels.AddRecordViewModel
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class AgendaFragment : Fragment() {
    private lateinit var binding: FragmentAgendaBinding
    private val databaseViewModel: DatabaseViewModel by activityViewModels{DatabaseViewmodelFactory(requireContext())}
    private val recordViewModel: AddRecordViewModel by activityViewModels()

    var tags: ArrayList<String>? = null

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgendaBinding.inflate(inflater, container, false)

        return binding.root
    }

}
