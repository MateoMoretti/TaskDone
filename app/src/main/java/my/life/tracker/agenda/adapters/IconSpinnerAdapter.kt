package my.life.tracker.agenda.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.compose.ui.res.dimensionResource
import androidx.core.content.ContextCompat
import my.life.tracker.R
import my.life.tracker.agenda.model.IconText
import my.life.tracker.databinding.SpinnerDropdownAgendaBinding


class IconSpinnerAdapter(
    context: Context?,
    val iconTextList: ArrayList<IconText>?
) :
    ArrayAdapter<IconText>(context!!, 0, iconTextList!!) {

    lateinit var binding: SpinnerDropdownAgendaBinding
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {

        var convertedView = convertView
        if(convertView == null){
            convertedView = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_agenda, null, false)
            binding = SpinnerDropdownAgendaBinding.bind(convertedView)
        }
        binding.root.layoutParams = ViewGroup.LayoutParams(
            context.resources.getDimension(R.dimen.celda_width).toInt(),
            context.resources.getDimension(R.dimen.celda_height).toInt())

        iconTextList!![position].icon?.let{
            binding.icon.setImageDrawable(it)
            binding.icon.visibility = View.VISIBLE
        }
        binding.text.text = iconTextList[position].text


        return convertedView!!
    }
}
