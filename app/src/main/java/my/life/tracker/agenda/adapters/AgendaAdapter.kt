package my.life.tracker.agenda.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import my.life.tracker.R
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.Celda
import my.life.tracker.components.LineaAgenda


class AgendaAdapter(val context: Context, var data: ArrayList<Actividad>,
                    val hintsActividades:String,
                    val hintsTipos:String) : RecyclerView.Adapter <AgendaAdapter.AgendaViewHolder>(), CeldaClickListener
{

    var celdaSelected:Celda? = null
    var setupDone = false

    init {
        addLinea(Actividad())
    }
    class AgendaViewHolder(val lineaAgenda: LineaAgenda) : RecyclerView.ViewHolder(lineaAgenda)

    override fun getItemCount(): Int {
        return data.size
    }
    fun addLinea(actividad: Actividad) {
        data.add(data.size, actividad)
        clearCells()
        notifyItemChanged(data.size)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val v = LineaAgenda(context, this)
        val vh = AgendaViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        if(!setupDone) holder.lineaAgenda.setActividad(Actividad(
            "día",
            "actividad",
            "tipo",
            "comienzo",
            "fin" ,
            "importancia",
            "comentarios"), false)

        else holder.lineaAgenda.setActividad(data[position], true)
        setupDone= true
    }

    fun clearCells(){
        celdaSelected?.unselectCell()
    }

    override fun onCeldaClicked(celda: Celda) {
        celda.selectCell()
        if(celda != celdaSelected) celdaSelected?.unselectCell()
        celdaSelected = celda
    }
}