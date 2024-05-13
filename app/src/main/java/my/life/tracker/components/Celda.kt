package my.life.tracker.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import my.life.tracker.R
import my.life.tracker.agenda.model.CellType
import my.life.tracker.databinding.CeldaBinding


class Celda : LinearLayout {

    var hasIgnoredFirstTrigger = false
    var isAlreadySelected = false
    private var isSelectable = false
    private lateinit var cellType : CellType
    private var listOfHints = arrayListOf<String>()
    private val colorSelectable = R.color.borders

    private val blockCharacterSet = "/n"

     var valorCelda = ""

    var binding: CeldaBinding = CeldaBinding.inflate(LayoutInflater.from(context), this, true)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.Celda);
        if (attributes != null) {
            isSelectable = attributes.getBoolean(R.styleable.Celda_isSelectable, true)
            cellType = CellType.entries.toTypedArray()[attributes.getInt(R.styleable.Celda_cellType, 0)]
            attributes.recycle()
        }

        if(cellType == CellType.SPINNER) binding.celdaTv.visibility = INVISIBLE
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
                valorCelda = binding.celdaEd.text.toString()
                saveData()
                return@OnKeyListener true
            }
            false
        })
    }

    fun saveData(){
        binding.celdaTv.text = valorCelda
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

    //Hints concatenadas con caracter "_"
    fun setHints(hints:String){
        listOfHints.add(0, valorCelda)
        listOfHints.addAll(hints.split("_"))
        listOfHints.add("+")
        val spinnerArrayAdapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            listOfHints as List<String?>
        ) {}
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
        binding.celdaSpinner.adapter = spinnerArrayAdapter
        binding.celdaSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                    if(hasIgnoredFirstTrigger) {
                        valorCelda = listOfHints[i]
                        saveData()
                    }
                    hasIgnoredFirstTrigger = true
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {//
                }
            }
    }

}