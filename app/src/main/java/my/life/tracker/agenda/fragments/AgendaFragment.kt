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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import my.life.tracker.ActivityMain
import my.life.tracker.R
import my.life.tracker.Utils
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.adapters.AgendaAdapter
import my.life.tracker.agenda.callbacks.SimpleItemTouchHelperCallback
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

    private fun loadActividades(){
        val adapter = AgendaAdapter(requireContext(),
            agendaViewModel.getActividades(),
            agendaPreferences
        )
        adapter.overdueListener { agendaViewModel.updateActividad(it) }
        adapter.addHintListener { agendaPreferences.addCeldaHints(it.index, it.value) }
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)

        binding.recyclerAgenda.let{
            touchHelper.attachToRecyclerView(it)
            it.setLayoutManager(LinearLayoutManager(context))
            it.adapter = adapter
        }
    }

    private fun setListeners(){
        agendaViewModel.date.observe(viewLifecycleOwner){
            binding.dia.text = Utils.getDia(agendaViewModel.getCalendar(), context)
            loadActividades()
        }

        binding.root.setOnClickListener{
            (binding.recyclerAgenda.adapter as AgendaAdapter).clearCells()
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
            (binding.recyclerAgenda.adapter as AgendaAdapter).addLinea(agendaViewModel.createActividad())
        }
    }
}
