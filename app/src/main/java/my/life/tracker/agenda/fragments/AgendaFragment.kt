package my.life.tracker.agenda.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import my.life.tracker.ActivityMain
import my.life.tracker.R
import my.life.tracker.Utils
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.adapters.AgendaAdapter
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.agenda.viewmodels.AgendaViewModel
import my.life.tracker.databinding.FragmentAgendaBinding
import java.util.*
import javax.inject.Inject


@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressLint("ClickableViewAccessibility")
@AndroidEntryPoint
class AgendaFragment : Fragment() {

    private lateinit var binding: FragmentAgendaBinding
    private val agendaViewModel: AgendaViewModel by activityViewModels()
    @Inject
    lateinit var agendaPreferences: AgendaPreferences

    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var reduceDay: Runnable
    private lateinit var addDay: Runnable
    private var delay :Long= 200
    private var delayCounter = 0



    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgendaBinding.inflate(inflater, container, false)
        (activity as ActivityMain).updateFragment(R.id.agendaFragment)
        reduceDay = Runnable {
            if (binding.previousDay.isPressed) {
                agendaViewModel.setDay(agendaViewModel.getCalendar().get(Calendar.DATE) - 1)
                delayCounter++
                if(delayCounter>10) {
                    delay /= 2
                    delayCounter = 0
                }
                mainHandler.postDelayed(reduceDay, delay/* OR the amount of time you want */);
            }
        }
        addDay = Runnable {
            if (binding.nextDay.isPressed) {
                agendaViewModel.setDay(agendaViewModel.getCalendar().get(Calendar.DATE) + 1)
                delayCounter++
                if(delayCounter>10) {
                    delay /= 2
                    delayCounter = 0
                }
                mainHandler.postDelayed(addDay, delay/* OR the amount of time you want */);
            }
        }
        setListeners()
        return binding.root
    }

    private fun restartCounter(){
        delayCounter = 0
        delay = 200
        mainHandler.removeCallbacksAndMessages(null);
    }

    private fun setListeners(){
        val defaultActividades = agendaPreferences.getPreferenceString(requireContext(), AgendaPreferences.ACTIViDADES)
        val defaultTipos = agendaPreferences.getPreferenceString(requireContext(), AgendaPreferences.TIPOS)

        agendaViewModel.date.observe(viewLifecycleOwner){
            binding.dia.text = Utils.getDia(agendaViewModel.getCalendar(), context)
        }

        binding.previousDay.setOnTouchListener { view, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    agendaViewModel.setDay(agendaViewModel.getCalendar().get(Calendar.DATE) - 1)
                    view.isPressed = true
                    mainHandler.postDelayed(reduceDay, 500/* OR the amount of time you want */);

                }
                MotionEvent.ACTION_UP -> {
                    restartCounter()
                    view.isPressed = false
                }
            }
            true
        }
        binding.nextDay.setOnTouchListener { view, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    agendaViewModel.setDay(agendaViewModel.getCalendar().get(Calendar.DATE) + 1)
                    mainHandler.postDelayed(addDay, 500)
                    view.isPressed = true
                }
                MotionEvent.ACTION_UP -> {
                    restartCounter()
                    view.isPressed = false
                }
            }
            true
        }

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
