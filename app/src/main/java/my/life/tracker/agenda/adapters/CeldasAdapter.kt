package my.life.tracker.agenda.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.life.tracker.R
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.components.Celda


class CeldasAdapter(val context: Context, var data: ArrayList<String>,
                    val hintsActividades:String,
                    val hintsTipos:String) : RecyclerView.Adapter <CeldasAdapter.CeldasViewHolder>(), CeldaClickListener
{

    var celdaSelected:Celda? = null

    class CeldasViewHolder(val celda: Celda) : RecyclerView.ViewHolder(celda) {
    }
    private val colorSelectable = R.color.azul_notificacion

    override fun getItemCount(): Int {
        return data.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CeldasViewHolder {
        val v = Celda(context)
        val vh = CeldasViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: CeldasViewHolder, position: Int) {
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