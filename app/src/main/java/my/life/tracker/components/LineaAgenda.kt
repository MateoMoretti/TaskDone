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
    private lateinit var agendaViewModel: AgendaViewModel
    private lateinit var actividad: Actividad

    var binding: LineaAgendaBinding = LineaAgendaBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.LineaAgenda);
        if (attributes != null) {
            isSelectable = attributes.getBoolean(R.styleable.LineaAgenda_isSelectable, true)
            attributes.recycle()
        }
        setListeners()
    }
    constructor(context: Context?, agendaViewModel: AgendaViewModel, actividad: Actividad) : super(context){
        isSelectable = true
        this.agendaViewModel = agendaViewModel
        this.actividad = actividad
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
        }
    }

    private fun selectCell(view: View){
        agendaViewModel.selectLine(context, actividad.id)
        view.setBackgroundColor(ContextCompat.getColor(context, colorSelectable))

    }
}