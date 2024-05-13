package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import my.life.tracker.databinding.LineaAgendaBinding

class LineaAgenda : LinearLayout {

    private var isSelectable = false

    var binding: LineaAgendaBinding = LineaAgendaBinding.inflate(LayoutInflater.from(context), this, true)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
    }
    constructor(context: Context?) : super(context){
        isSelectable = true
    }

}