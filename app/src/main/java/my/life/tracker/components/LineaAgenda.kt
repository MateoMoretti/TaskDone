package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.agenda.model.CellType
import my.life.tracker.databinding.LineaAgendaBinding

class LineaAgenda : LinearLayout {

    var binding: LineaAgendaBinding = LineaAgendaBinding.inflate(LayoutInflater.from(context), this, true)

    lateinit var celdaClickListener: CeldaClickListener


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, celdaClickListener: CeldaClickListener) : super(context){
        this.celdaClickListener = celdaClickListener
    }

    fun setActividad(actividad: Actividad, isSelectable:Boolean){
        binding.layoutAgenda.addView(Celda(context, actividad.actividad, CellType.SPINNER, isSelectable, celdaClickListener))
        binding.layoutAgenda.addView(Celda(context, actividad.tipo, CellType.SPINNER, isSelectable, celdaClickListener))
        binding.layoutAgenda.addView(Celda(context, actividad.comienzo, CellType.HORA, isSelectable, celdaClickListener))
        binding.layoutAgenda.addView(Celda(context, actividad.fin, CellType.HORA, isSelectable, celdaClickListener))
        binding.layoutAgenda.addView(Celda(context, actividad.importancia, CellType.SLIDER, isSelectable, celdaClickListener))
        binding.layoutAgenda.addView(Celda(context, actividad.comentarios, CellType.TEXTO, isSelectable, celdaClickListener))


    }

}