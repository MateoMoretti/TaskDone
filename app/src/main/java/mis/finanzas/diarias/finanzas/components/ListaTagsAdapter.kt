package mis.finanzas.diarias.finanzas.components

import androidx.recyclerview.widget.RecyclerView
import mis.finanzas.diarias.finanzas.components.ListaTagsAdapter.ViewHolderAdapter
import com.example.taskdone.R
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import mis.finanzas.diarias.finanzas.fragments.TagsFragment
import mis.finanzas.diarias.finanzas.model.Tag
import mis.finanzas.diarias.finanzas.viewmodels.BaseRecordViewModel

class ListaTagsAdapter(
    private val viewModel: BaseRecordViewModel,
    private var items: List<Tag>,
    private val fragment: TagsFragment
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