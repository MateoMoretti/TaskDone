package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import my.life.tracker.R
import my.life.tracker.databinding.WidgetBinding

class Widget : LinearLayout {

    private var isShowingDetails = false
    private val colorNotificacion = R.color.azul_notificacion
    private val colorDetalles = R.color.azul_detalles

    var binding: WidgetBinding = WidgetBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        binding.notificacion.setOnClickListener(){
            showDetails()
        }
        binding.notificacion.setCardBackgroundColor(ContextCompat.getColor(context, colorNotificacion))
        binding.detalles.setCardBackgroundColor(ContextCompat.getColor(context, colorDetalles))
    }

    private fun showDetails(){
        binding.detalles.visibility = if (binding.detalles.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        isShowingDetails = binding.detalles.visibility == View.VISIBLE
    }
}