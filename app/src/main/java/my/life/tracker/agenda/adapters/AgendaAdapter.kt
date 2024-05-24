package my.life.tracker.agenda.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.life.tracker.IndexValue
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.Celda
import my.life.tracker.components.LineaAgenda
import java.util.Collections


class AgendaAdapter(val context: Context, var data: ArrayList<Actividad>,
                    val agendaPreferences: AgendaPreferences)
    : RecyclerView.Adapter <AgendaAdapter.AgendaViewHolder>(), CeldaClickListener, ItemTouchHelperAdapter
{

    var celdaSelected:Celda? = null
    var setupDone = false

    private lateinit var overdueCallback : (actividad: Actividad) -> Unit
    private lateinit var addHintCallback : (indexValue: IndexValue) -> Unit

    fun overdueListener(callback: (actividad: Actividad) -> Unit){
        overdueCallback = callback
    }
    fun addHintListener(callback: (indexValue: IndexValue) -> Unit){
        addHintCallback = callback
    }
    init {
        data.add(0, Actividad())
    }
    class AgendaViewHolder(val lineaAgenda: LineaAgenda) : RecyclerView.ViewHolder(lineaAgenda)

    override fun getItemCount(): Int {
        return data.size
    }
    fun addLinea(actividad: Actividad) {
        clearCells()
        data.add(data.size, actividad)
        notifyItemChanged(data.size)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val v = LineaAgenda(context, this, agendaPreferences)
        val vh = AgendaViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        if(!setupDone) holder.lineaAgenda.setTitle()
        else {
            holder.lineaAgenda.setActividad(data[position], true)
        }
        setupDone= true
    }

    override fun getItemViewType(position: Int): Int {
        if(!setupDone) return 0
        return 0
    }

    fun clearCells(){
        celdaSelected?.unselectCell()
    }
    override fun onCeldaClicked(celda: Celda) {
        celda.selectCell()
        if(celda != celdaSelected) celdaSelected?.unselectCell()
        celdaSelected = celda
    }

    override fun onValueSelected(actividad: Actividad) {
        return overdueCallback(actividad)
    }

    override fun onHintAdded(indexValue: IndexValue) {
        return addHintCallback(indexValue)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}