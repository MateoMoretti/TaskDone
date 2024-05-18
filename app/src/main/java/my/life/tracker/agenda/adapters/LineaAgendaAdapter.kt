package my.life.tracker.agenda.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.life.tracker.R
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.Celda
import my.life.tracker.components.LineaAgenda


class LineaAgendaAdapter(val context: Context, var data: ArrayList<Actividad>,
                         val hintsActividades:String,
                         val hintsTipos:String) : RecyclerView.Adapter <LineaAgendaAdapter.AgendaViewHolder>(), CeldaClickListener
{

    var celdaSelected:Celda? = null

    init {
        addLinea(Actividad())
    }
    class AgendaViewHolder(val lineaAgenda: LineaAgenda) : RecyclerView.ViewHolder(lineaAgenda) {
    }
    private val colorSelectable = R.color.azul_notificacion

    override fun getItemCount(): Int {
        return data.size
    }
    fun addLinea(actividad: Actividad) {
        data.add(data.size, actividad)
        clearCells()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val v = LineaAgenda(context, this)
        val vh = AgendaViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        holder.lineaAgenda.setActividad(data[position])

        val celdaActividad = holder.lineaAgenda.binding.actividad
        val celdaTipo = holder.lineaAgenda.binding.tipo
        val celdaComienzo = holder.lineaAgenda.binding.comienzo
        val celdaFin = holder.lineaAgenda.binding.fin
        val celdaImportancia = holder.lineaAgenda.binding.importancia
        val celdaComentarios = holder.lineaAgenda.binding.comentarios

        /*celdaActividad.let {
            it.binding.celdaTv.setOnClickListener { selectCell(celdaActividad)}
            it.setHints(hintsActividades)
        }
        celdaTipo.let {
            it.binding.celdaTv.setOnClickListener { selectCell(celdaTipo)}
            it.setHints(hintsTipos)
        }
        celdaComienzo.binding.celdaTv.setOnClickListener { selectCell(celdaComienzo)}
        celdaFin.binding.celdaTv.setOnClickListener { selectCell(celdaFin)}
        celdaImportancia.binding.celdaTv.setOnClickListener { selectCell(celdaImportancia)}
        celdaImportancia.binding.celdaTv.setOnClickListener { selectCell(celdaImportancia)}
        celdaComentarios.binding.celdaTv.setOnClickListener { selectCell(celdaComentarios)}*/

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