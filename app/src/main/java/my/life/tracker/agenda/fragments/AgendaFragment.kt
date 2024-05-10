package my.life.tracker.agenda.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import my.life.tracker.R
import my.life.tracker.databinding.FragmentAgendaBinding
import my.life.tracker.ActivityMain
import my.life.tracker.finanzas.viewmodels.DatabaseViewModel
import my.life.tracker.finanzas.viewmodels.DatabaseViewmodelFactory
import my.life.tracker.finanzas.viewmodels.AddRecordViewModel
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
        (activity as ActivityMain).updateFragment(R.id.agendaFragment)


        setListeners()
        return binding.root
    }

    private fun setListeners(){
        /*binding.editTime.let { edit ->
            edit.setOnClickListener {
                (activity as ActivityMain).selectTime(edit, requireContext())
            }
        }*/
    }
}
