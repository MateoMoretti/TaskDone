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
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.agenda.viewmodels.AgendaViewModel
import my.life.tracker.components.LineaAgenda
import my.life.tracker.finanzas.viewmodels.DatabaseFinanzasViewModel
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class AgendaFragment : Fragment() {
    private lateinit var binding: FragmentAgendaBinding
    private val databaseViewModel: DatabaseFinanzasViewModel by activityViewModels()
    private val agendaViewModel: AgendaViewModel by activityViewModels()

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
        binding.addLine.setOnClickListener(){
            addLine()
        }
        /*binding.editTime.let { edit ->
            edit.setOnClickListener {
                (activity as ActivityMain).selectTime(edit, requireContext())
            }
        }*/
    }

    private fun addLine(){

        val newActividad = Actividad("", "", "", "", -1, "")
        newActividad.id = agendaViewModel.addActividad(newActividad)
        val lineaAgenda = LineaAgenda(
            requireContext(),
            agendaViewModel,
            newActividad)
        binding.layoutAgenda.addView(lineaAgenda)
        agendaViewModel.addActividadToScreen(newActividad, lineaAgenda)
    }
}
