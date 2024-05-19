package my.life.tracker.agenda.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import my.life.tracker.ActivityMain
import my.life.tracker.DatePickerFragment
import my.life.tracker.R
import my.life.tracker.Utils
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.adapters.AgendaAdapter
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.agenda.viewmodels.AgendaViewModel
import my.life.tracker.databinding.FragmentAgendaBinding
import my.life.tracker.finanzas.model.Record
import java.util.*
import javax.inject.Inject


@RequiresApi(api = Build.VERSION_CODES.O)
@AndroidEntryPoint
class AgendaFragment : Fragment() {
    private lateinit var binding: FragmentAgendaBinding

    @Inject
    lateinit var agendaPreferences: AgendaPreferences

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
        val defaultActividades = agendaPreferences.getPreferenceString(requireContext(), AgendaPreferences.ACTIViDADES)
        val defaultTipos = agendaPreferences.getPreferenceString(requireContext(), AgendaPreferences.TIPOS)
        binding.addLine.setOnClickListener {
            addLine()
        }
        val adapter = AgendaAdapter(requireContext(),
            arrayListOf(),
            agendaPreferences.getActividades(requireContext()),
            agendaPreferences.getTipos(requireContext())

        )

        binding.recyclerAgenda.setLayoutManager(LinearLayoutManager(context))
        binding.recyclerAgenda.adapter = adapter
    }

    private fun addLine(){
        val newActividad = Actividad()
        newActividad.id = agendaViewModel.addActividad(newActividad)
        (binding.recyclerAgenda.adapter as AgendaAdapter).addLinea(newActividad)
    }



}
