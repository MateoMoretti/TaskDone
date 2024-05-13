package my.life.tracker.agenda.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import my.life.tracker.R
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.Celda
import my.life.tracker.components.LineaAgenda


class AgendaAdapter(val context: Context, var data: ArrayList<Actividad>,
                    val hintsActividades:String,
                    val hintsTipos:String) : RecyclerView.Adapter <AgendaAdapter.AgendaViewHolder>(){

    var celdaSelected:Celda? = null

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
        val v = LineaAgenda(context)
        val vh = AgendaViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        val celdaActividad = holder.lineaAgenda.binding.actividad
        val celdaTipo = holder.lineaAgenda.binding.tipo
        val celdaComienzo = holder.lineaAgenda.binding.comienzo
        val celdaFin = holder.lineaAgenda.binding.fin
        val celdaImportancia = holder.lineaAgenda.binding.importancia
        val celdaComentarios = holder.lineaAgenda.binding.comentarios

        celdaActividad.let {
            it.binding.celdaTv.text = data[position].actividad
            it.binding.celdaEd.setText(data[position].actividad)
            it.binding.celdaTv.setOnClickListener { selectCell(celdaActividad)}
            it.setHints(hintsActividades)
        }
        celdaTipo.let {
            it.binding.celdaTv.text = data[position].tipo
            it.binding.celdaEd.setText(data[position].tipo)
            it.binding.celdaTv.setOnClickListener { selectCell(celdaTipo)}
            it.setHints(hintsTipos)
        }
        celdaComienzo.let {
            it.binding.celdaTv.text = data[position].comienzo
            it.binding.celdaEd.setText(data[position].comienzo)
            it.binding.celdaTv.setOnClickListener { selectCell(celdaComienzo)}
        }
        celdaFin.let {
            it.binding.celdaTv.text = data[position].fin
            it.binding.celdaEd.setText(data[position].fin)
            it.binding.celdaTv.setOnClickListener { selectCell(celdaFin)}
        }
        celdaImportancia.let {
            it.binding.celdaTv.text = data[position].importancia
            it.binding.celdaEd.setText(data[position].importancia)
            it.binding.celdaTv.setOnClickListener { selectCell(celdaImportancia)}
        }
        celdaComentarios.let {
            it.binding.celdaTv.text = data[position].comentarios
            it.binding.celdaEd.setText(data[position].comentarios)
            it.binding.celdaTv.setOnClickListener { selectCell(celdaComentarios)}
        }
    }

    fun selectCell(celda: Celda){
        if(!celda.isAlreadySelected) clearCells()
        celda.selectCell()
        celdaSelected = celda
    }

    fun clearCells(){
        celdaSelected?.unselectCell()
    }
}