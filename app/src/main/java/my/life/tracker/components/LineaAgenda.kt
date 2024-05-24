package my.life.tracker.components

import android.content.Context
import android.icu.util.Calendar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import my.life.tracker.Utils
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
            Utils.dateToString(Calendar.getInstance().time),
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

        //Termina menos 1 para no agregar celda de ID
        //El 0 es 'date' pero no se utiliza su valor, solo se utiliza para agregar ícono
        for (i in 0 until actividad.getAttributes().size - 1) {
            var hints: ArrayList<String> = arrayListOf()
            if (isSelectable) {
                hints = agendaPreferences.getCeldaHints(i)
                when (i) {
                    0 -> cellType = CellType.DRAG_IMAGE
                    1, 2 -> cellType = CellType.SPINNER
                    3, 4 -> cellType = CellType.HORA
                    5 -> cellType = CellType.SLIDER
                    6 -> cellType = CellType.TEXTO
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