package my.life.tracker.agenda.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.life.tracker.IndexValue
import my.life.tracker.agenda.AgendaPreferences
import my.life.tracker.agenda.AgendaPreferencesImpl
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.Celda
import my.life.tracker.components.LineaAgenda


class AgendaAdapter(val context: Context, var data: ArrayList<Actividad>,
                    val agendaPreferences: AgendaPreferences) : RecyclerView.Adapter <AgendaAdapter.AgendaViewHolder>(), CeldaClickListener
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

    override fun onValueSelected(actividad: Actividad) {
        return overdueCallback(actividad)
    }

    override fun onHintAdded(indexValue: IndexValue) {
        return addHintCallback(indexValue)
    }
}