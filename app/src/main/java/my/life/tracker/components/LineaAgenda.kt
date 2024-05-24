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

    fun setActividad(actividad: Actividad, isSelectable:Boolean, listOfHints: ArrayList<String> = arrayListOf()) {
        var cellType = CellType.TEXTO

        //Comienza en 1 para no agregar celda de día y termina menos 1 para no agregar celda de ID
        for (i in 1 until actividad.getAttributes().size - 1) {
            var hints: ArrayList<String> = arrayListOf()

            if (isSelectable) {
                if (i < listOfHints.size) hints = listOfHints[i].split("_") as ArrayList<String>
                when (i) {
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