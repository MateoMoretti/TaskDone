package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View.OnKeyListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import my.life.tracker.R
import my.life.tracker.databinding.CeldaBinding


class Celda : LinearLayout {

    var isAlreadySelected = false
    private var isSelectable = false
    private var hint = ""
    private val colorSelectable = R.color.borders

    private val blockCharacterSet = "/n"

     var valorCelda = ""

    var binding: CeldaBinding = CeldaBinding.inflate(LayoutInflater.from(context), this, true)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.Celda);
        if (attributes != null) {
            isSelectable = attributes.getBoolean(R.styleable.Celda_isSelectable, true)
            attributes.recycle()
        }
    }
    constructor(context: Context?) : super(context){
        isSelectable = true
    }

    init {
        binding.celdaEd.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                saveData()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.celdaEd.setOnKeyListener(OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                saveData()
                return@OnKeyListener true
            }
            false
        })
    }

    fun saveData(){
        binding.celdaTv.text = binding.celdaEd.text
        binding.celdaTv.visibility = VISIBLE
        binding.celdaEd.visibility = INVISIBLE
    }
    fun selectCell(){
        if(isAlreadySelected){
            binding.celdaTv.visibility = INVISIBLE
            binding.celdaEd.visibility = VISIBLE
            binding.celdaEd.requestFocus()
            binding.celdaEd.setSelection(binding.celdaEd.length())
        } else {
            isAlreadySelected = true
            binding.celdaTv.setBackgroundColor(ContextCompat.getColor(context, colorSelectable))
        }
    }

    fun unselectCell(){
        isAlreadySelected=false
        binding.celdaTv.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        saveData()
    }

}