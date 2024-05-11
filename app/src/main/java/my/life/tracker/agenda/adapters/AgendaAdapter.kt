package my.life.tracker.agenda.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.components.LineaAgenda


class AgendaAdapter(val context: Context, var data: ArrayList<Actividad>) : RecyclerView.Adapter <AgendaAdapter.AgendaViewHolder>(){
    class AgendaViewHolder(val lineaAgenda: LineaAgenda) : RecyclerView.ViewHolder(lineaAgenda) {
    }

    override fun getItemCount(): Int {
        return data.size
    }
    fun addLinea(actividad: Actividad) {
        data.add(actividad)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val v = LineaAgenda(context)
        val vh = AgendaViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        holder.lineaAgenda.binding.actividad.text = data[position].actividad
        holder.lineaAgenda.binding.tipo.text = data[position].tipo
        holder.lineaAgenda.binding.comienza.text = data[position].comienza
        holder.lineaAgenda.binding.fin.text = data[position].fin
        holder.lineaAgenda.binding.importancia.text = data[position].importancia
        holder.lineaAgenda.binding.comentarios.text = data[position].comentarios
    }

}