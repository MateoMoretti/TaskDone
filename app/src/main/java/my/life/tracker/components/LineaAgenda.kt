package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.findFragment
import my.life.tracker.R
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.agenda.viewmodels.AgendaViewModel
import my.life.tracker.databinding.LineaAgendaBinding

class LineaAgenda : LinearLayout {

    private var isSelected = false
    private var isSelectable = false
    private val colorSelectable = R.color.azul_notificacion

    var binding: LineaAgendaBinding = LineaAgendaBinding.inflate(LayoutInflater.from(context), this, true)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.LineaAgenda);
        if (attributes != null) {
            isSelectable = attributes.getBoolean(R.styleable.LineaAgenda_isSelectable, true)
            attributes.recycle()
        }
        setListeners()
    }
    constructor(context: Context?) : super(context){
        isSelectable = true
        setListeners()
    }

    private fun showDetails(){
        //binding.detalles.visibility = if (binding.detalles.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        //isShowingDetails = binding.detalles.visibility == View.VISIBLE
    }

    private fun setListeners(){
        if(isSelectable) {
            binding.actividad.setOnClickListener() {
                selectCell(it)
            }
            /*agendaViewModel.idPreviousLineSelected.observe(findFragment()){
                binding.findViewById<View>(it).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }*/
        }
    }

    fun selectCell(view: View){
        //agendaViewModel.selectLine(view)
        view.setBackgroundColor(ContextCompat.getColor(context, colorSelectable))
    }

    fun unselectCell(view: View){
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }
}