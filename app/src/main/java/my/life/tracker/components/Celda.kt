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
import android.widget.TimePicker
import my.life.tracker.ActivityMain
import my.life.tracker.HourPickerFragment
import my.life.tracker.R
import my.life.tracker.Utils
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.CellType
import my.life.tracker.databinding.CeldaBinding


class Celda : LinearLayout {


    private var valorCelda = ""
    private var isSelectable = true
    private var listOfHints = arrayListOf<String>()
    private var hasIgnoredFirstTrigger = false
    private var celdaClickListener: CeldaClickListener? = null
    private var cellType = CellType.TEXTO

    var binding: CeldaBinding = CeldaBinding.inflate(LayoutInflater.from(context), this, true)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.Celda)
        if (attributes != null) {
            isSelectable = attributes.getBoolean(R.styleable.Celda_isSelectable, true)
            cellType = CellType.entries.toTypedArray()[attributes.getInt(R.styleable.Celda_cellType, 0)]
            attributes.recycle()
        }
        setup()
    }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, valorCelda:String, cellType: CellType, isSelectable:Boolean,celdaClickListener: CeldaClickListener, hints: ArrayList<String> = arrayListOf()) : super(context){
        this.celdaClickListener = celdaClickListener
        this.valorCelda = valorCelda
        this.cellType = cellType
        this.isSelectable = isSelectable
        listOfHints = hints

        if(isSelectable) {
            if (valorCelda in listOfHints) {
                listOfHints.remove(valorCelda)
            }
            listOfHints.add(0, valorCelda)
            listOfHints.add(listOfHints.size, "add_new_value")

            setup()
        }
        saveData()
    }
    fun setup(){
        saveData()
        when(cellType){
            CellType.SPINNER -> {
                addHints()
            }
            else -> {

            }
        }
        binding.celdaTv.setOnClickListener { celdaClickListener?.onCeldaClicked(this) }
        binding.celdaEd.setOnClickListener { celdaClickListener?.onCeldaClicked(this) }

        binding.celdaEd.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                saveData()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.celdaEd.setOnKeyListener(OnKeyListener { _, keyCode, event ->
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
        binding.celdaEd.setText(valorCelda)

        binding.celdaTv.visibility = VISIBLE
        binding.celdaEd.visibility = INVISIBLE
        binding.celdaSpinner.visibility = INVISIBLE
    }
    fun selectCell(){
        if(isSelected){
            when(cellType){
                CellType.TEXTO -> {
                    binding.celdaTv.visibility = INVISIBLE
                    binding.celdaEd.visibility = VISIBLE
                    binding.celdaEd.requestFocus()
                    binding.celdaEd.setSelection(binding.celdaEd.length())
                }
                CellType.HORA -> {
                    showHourPicker()
                }
                CellType.SPINNER -> {
                    binding.celdaTv.visibility = INVISIBLE
                    binding.celdaSpinner.visibility = VISIBLE
                    binding.celdaSpinner.performClick()
                }
                CellType.SLIDER -> {
                }
            }
        } else {
            isSelected = true
        }
    }

    fun unselectCell(){
        isSelected=false
        saveData()
    }

    //Hints concatenadas con caracter "_"
    fun addHints(){
        val spinnerArrayAdapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            context,
            R.layout.spinner_dropdown_agenda,
            listOfHints as List<String?>
        ) {}
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_agenda)
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
    private fun showHourPicker() {
        var defaultHour = 0
        var defaultMinutes = 0
        try {
            defaultHour = valorCelda.substring(0, 2).toInt()
            defaultMinutes = valorCelda.substring(3, 5).toInt()
        }catch (e:Exception){
            e.printStackTrace()
        }
        HourPickerFragment.newInstance({ _: TimePicker?, hour: Int, minutes ->
            val selectedHour = Utils.twoDigits(hour) + ":" + Utils.twoDigits(minutes)
            valorCelda = selectedHour
            saveData()
        }, defaultHour, defaultMinutes)
            .show(
                ActivityMain.instance.supportFragmentManager,
                "hourPicker"
            )
    }
    fun setText(text:String){
        valorCelda = text
        saveData()
    }
    fun hideAll(){
        binding.celdaTv.visibility = INVISIBLE
        binding.celdaEd.visibility = INVISIBLE
        binding.celdaSpinner.visibility = INVISIBLE
    }


}