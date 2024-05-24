package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.agenda.model.CellType
import my.life.tracker.databinding.LineaAgendaBinding

class LineaAgenda : LinearLayout{

    var binding: LineaAgendaBinding = LineaAgendaBinding.inflate(LayoutInflater.from(context), this, true)

    lateinit var celdaClickListener: CeldaClickListener

    lateinit var agendaPreferences: AgendaPreferences

    lateinit var actividad: Actividad


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, celdaClickListener: CeldaClickListener, agendaPreferences: AgendaPreferences) : super(context){
        this.celdaClickListener = celdaClickListener
        this.agendaPreferences = agendaPreferences
    }

    fun setTitle(){
        setActividad(Actividad(
            "actividad",
            "tipo",
            "comienzo",
            "fin" ,
            "importancia",
            "comentarios"),
        false)
    }

    fun setActividad(actividad: Actividad, isSelectable:Boolean) {
        var cellType = CellType.TEXTO
        this.actividad = actividad
        //Comienza en 1 para no agregar celda de día y termina menos 2 para no agregar celda de Día ni ID
        for (i in 0 until actividad.getAttributes().size - 2) {
            var hints: ArrayList<String> = arrayListOf()
            if (isSelectable) {
                hints = agendaPreferences.getCeldaHints(i)
                when (i) {
                    0, 1 -> cellType = CellType.SPINNER
                    2, 3 -> cellType = CellType.HORA
                    4 -> cellType = CellType.SLIDER
                    5 -> cellType = CellType.TEXTO
                }
            }

            binding.layoutAgenda.addView(
                Celda(
                    context,
                    actividad,
                    i,
                    cellType,
                    isSelectable,
                    celdaClickListener,
                    hints.toTypedArray()
                )
            )
        }
    }


}