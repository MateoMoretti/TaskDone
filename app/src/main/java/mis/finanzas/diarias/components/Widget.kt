package mis.finanzas.diarias.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.taskdone.R
import com.example.taskdone.databinding.WidgetBinding

class Widget : LinearLayout {

    private val isShowingDetails = false
    val colorNotificacion = R.color.azul_notificacion
    val colorDetalles = R.color.azul_detalles

    var binding: WidgetBinding = WidgetBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        binding.notificacion.setOnClickListener(){
            showDetails()
        }
        binding.notificacion.setCardBackgroundColor(ContextCompat.getColor(context, colorNotificacion))
        binding.detalles.setCardBackgroundColor(ContextCompat.getColor(context, colorDetalles))
    }

    private fun showDetails(){
        binding.detalles.visibility = if (binding.detalles.visibility == View.VISIBLE) View.GONE else View.VISIBLE

    }
}