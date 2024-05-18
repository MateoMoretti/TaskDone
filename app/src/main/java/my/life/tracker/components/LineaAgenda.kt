package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.databinding.LineaAgendaBinding

class LineaAgenda : LinearLayout {

    var binding: LineaAgendaBinding = LineaAgendaBinding.inflate(LayoutInflater.from(context), this, true)
    val celdas = arrayListOf(
                binding.actividad,
                binding.tipo,
                binding.comienzo,
                binding.fin,
                binding.importancia,
                binding.comentarios)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, celdaClickListener: CeldaClickListener) : super(context){
        for(x in celdas) x.addClickListener(celdaClickListener)
    }
    fun setActividad(actividad: Actividad){
        binding.actividad.setText(actividad.actividad)
        binding.tipo.setText(actividad.tipo)
        binding.comienzo.setText(actividad.comienzo)
        binding.fin.setText(actividad.fin)
        binding.importancia.setText(actividad.importancia)
        binding.comentarios.setText(actividad.comentarios)
    }

}