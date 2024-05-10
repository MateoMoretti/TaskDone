package my.life.tracker.finanzas.components

import androidx.recyclerview.widget.RecyclerView
import my.life.tracker.finanzas.components.ListaTagsAdapter.ViewHolderAdapter
import my.life.tracker.R
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import my.life.tracker.finanzas.fragments.FinanzasTagsFragment
import my.life.tracker.finanzas.model.Tag
import my.life.tracker.finanzas.viewmodels.BaseRecordViewModel

class ListaTagsAdapter(
    private val viewModel: BaseRecordViewModel,
    private var items: List<Tag>,
    private val fragment: FinanzasTagsFragment
) : RecyclerView.Adapter<ViewHolderAdapter>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAdapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)


        return ViewHolderAdapter(view)
    }

    override fun onBindViewHolder(holder: ViewHolderAdapter, position: Int) {
        holder.tag.text = items[position].nombre
        if (viewModel.getSelectedTags().contains(items[position])) {
            holder.tag.isChecked = true
        }
        holder.tag.setOnClickListener { viewModel.onClickTag(items[position]) }

        holder.edit.setOnClickListener { fragment.popupEditTag(items[position]) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolderAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var edit: Button
        var tag: CheckBox

        init {
            tag = itemView.findViewById(R.id.check_tag)
            edit = itemView.findViewById(R.id.edit)
        }
    }
}