package my.life.tracker.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.slider.Slider
import my.life.tracker.ActivityMain
import my.life.tracker.HourPickerFragment
import my.life.tracker.R
import my.life.tracker.Utils
import my.life.tracker.agenda.adapters.IconSpinnerAdapter
import my.life.tracker.agenda.interfaces.CeldaClickListener
import my.life.tracker.agenda.model.Actividad
import my.life.tracker.agenda.model.CellType
import my.life.tracker.agenda.model.IconText
import my.life.tracker.databinding.CeldaBinding


class Celda : LinearLayout {


    private val _valorCelda = MutableLiveData<String>().apply { value = "" }
    private val valorCelda: LiveData<String> get() = _valorCelda

    private var actividad : Actividad? = null
    private var indexDefaulValueActividad = 0
    private var isSelectable = true
    private var listOfHints : ArrayList<String> = arrayListOf()
    private var hasIgnoredFirstTriggerObserver = false
    private var hasIgnoredFirstTriggerSpinner = false
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
    constructor(context: Context?,
                actividad: Actividad,
                indexDefaulValueActividad: Int,
                cellType: CellType,
                isSelectable:Boolean,celdaClickListener: CeldaClickListener,
                listOfHints: Array<String> = arrayOf()
    ) : super(context)
    {
        this.celdaClickListener = celdaClickListener
        this.indexDefaulValueActividad = indexDefaulValueActividad
        this.actividad = actividad
        this._valorCelda.value = actividad.getAttributes()[indexDefaulValueActividad]
        this.cellType = cellType
        this.isSelectable = isSelectable
        this.listOfHints = listOfHints.toCollection(ArrayList())

        if(isSelectable) {
            valorCelda.observe(ActivityMain.instance){
                if(hasIgnoredFirstTriggerObserver) {
                    updateData()
                    saveData()

                }
                hasIgnoredFirstTriggerObserver = true
            }

            setup()
        }
        else{
            binding.celdaTv.background = ContextCompat.getDrawable(context!!, R.drawable.shape_rectangulo)
        }
        updateData()
    }
    private fun setup(){
        when(cellType){
            CellType.SLIDER -> {
                try {
                    binding.celdaSlider.value = valorCelda.value!!.toFloat()
                }catch (e:Exception){
                    e.printStackTrace()
                    binding.celdaSlider.value = 50f
                }


                binding.celdaSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                    override fun onStartTrackingTouch(slider: Slider) {
                        celdaClickListener?.onCeldaClicked(this@Celda)
                    }

                    override fun onStopTrackingTouch(slider: Slider) {
                        unselectCell()
                    }
                })

                binding.celdaSlider.addOnChangeListener { _, value, _ ->
                    _valorCelda.value = value.toString()
                    var color = R.color.slider_0
                    //binding.celdaSlider.setCustomThumbDrawable(ContextCompat.getDrawable(context!! , R.drawable.comidas)!!)
                    when(value){
                        in 0.0..10.0 -> { color = R.color.slider_0 }
                        in 10.0..20.0 -> { color = R.color.slider_10 }
                        in 20.0..30.0 -> { color = R.color.slider_20 }
                        in 30.0..40.0 -> { color = R.color.slider_30 }
                        in 40.0..60.0 -> { color = R.color.slider_40 }
                        in 60.0..70.0 -> { color = R.color.slider_60 }
                        in 70.0..80.0 -> { color = R.color.slider_70 }
                        in 80.0..90.0 -> { color = R.color.slider_80 }
                        in 90.0..100.0 -> { color = R.color.slider_90 }
                    }
                    binding.celdaSlider.haloTintList = ColorStateList.valueOf(resources.getColor(color, null ))
                    binding.celdaSlider.trackTintList = ColorStateList.valueOf(resources.getColor(color, null))
                }
            }
            CellType.TEXTO, CellType.SPINNER -> {
                binding.celdaTv.setOnLongClickListener {
                    celdaClickListener?.onCeldaLongClicked(this)
                    openHintsOnLongClick()
                    true
                }

                binding.celdaTv.setOnClickListener { celdaClickListener?.onCeldaClicked(this) }
                binding.celdaEd.setOnClickListener { celdaClickListener?.onCeldaClicked(this) }

                binding.celdaEd.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                        _valorCelda.value = binding.celdaEd.text.toString()
                        return@setOnEditorActionListener true
                    }
                    false
                }

                binding.celdaEd.setOnKeyListener(OnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                        _valorCelda.value = binding.celdaEd.text.toString()
                        return@OnKeyListener true
                    }
                    false
                })
            }
            CellType.HORA -> {
            }
        }

    }
    private fun updateData(){
        when(cellType){
            CellType.SLIDER -> {
                binding.celdaSlider.visibility = VISIBLE
                binding.celdaTv.visibility = INVISIBLE
                binding.celdaEd.visibility = INVISIBLE
                binding.celdaSpinner.visibility = INVISIBLE
                refreshHints()
            }
            else -> {
                closeKeyboard()
                if(binding.celdaEd.visibility == VISIBLE){
                    _valorCelda.value = binding.celdaEd.text.toString()
                }
                binding.celdaTv.text = valorCelda.value
                binding.celdaEd.setText(valorCelda.value)

                binding.celdaTv.visibility = VISIBLE
                binding.celdaEd.visibility = INVISIBLE
                binding.celdaSpinner.visibility = INVISIBLE
                actividad?.setAttributes(indexDefaulValueActividad, valorCelda.value!!)
            }
        }
    }
    private fun saveData(){
        celdaClickListener?.onValueSelected(actividad!!)
    }

    private fun openHintsOnLongClick() {
        when (cellType) {
            CellType.SPINNER -> {
                binding.celdaSpinner.performClick()
            }
            CellType.HORA -> {
                showHourPicker()
            }
            else -> {
                Unit
            }
        }
    }

    private fun closeKeyboard(){
        val inputMethodManager = getSystemService(context, InputMethodManager::class.java) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.celdaEd.windowToken, 0)
    }
    private fun showKeyboard(){
        val inputMethodManager = getSystemService(context, InputMethodManager::class.java) as InputMethodManager
        inputMethodManager.showSoftInput(binding.celdaEd, InputMethodManager.SHOW_IMPLICIT)
    }
    fun selectCell(){
        if(isSelected){
            when(cellType){
                CellType.TEXTO -> {
                    binding.celdaTv.visibility = INVISIBLE
                    binding.celdaEd.visibility = VISIBLE
                    binding.celdaEd.setSelection(binding.celdaEd.length())
                    binding.celdaEd.requestFocus()
                    showKeyboard()
                }
                CellType.HORA -> {
                    showHourPicker()
                }
                CellType.SPINNER -> {
                    binding.celdaTv.visibility = INVISIBLE
                    binding.celdaEd.visibility = VISIBLE
                    binding.celdaEd.setSelection(binding.celdaEd.length())
                    binding.celdaEd.requestFocus()
                    showKeyboard()
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
        updateData()
    }

    //Hints concatenadas con caracter "_"
    private fun refreshHints(){
        hasIgnoredFirstTriggerSpinner = false
        val hints = arrayListOf<IconText>()

        if(valorCelda.value !in listOfHints) hints.add(IconText(valorCelda.value!!))
        hints.addAll(listOfHints.map { IconText(it) })

        hints.add(IconText("", ContextCompat.getDrawable(context!! , android.R.drawable.ic_input_add)))
        val adapter = IconSpinnerAdapter(context, hints)

        binding.celdaSpinner.adapter = adapter
        binding.celdaSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                    if(hasIgnoredFirstTriggerSpinner) {
                        if(i == hints.size - 1){
                            addNewValueSpinner()
                        }
                        else {
                            _valorCelda.value = hints[i].text
                        }
                    }
                    hasIgnoredFirstTriggerSpinner = true
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {//
                }
            }
        binding.celdaSpinner.setSelection(hints.map { it.text }.indexOf(valorCelda.value))

    }

    private fun addNewValueSpinner(){
        binding.celdaSpinner.visibility = INVISIBLE
        binding.celdaEd.visibility = VISIBLE
        binding.celdaEd.requestFocus()
        binding.celdaEd.setSelection(binding.celdaEd.length())
    }
    private fun showHourPicker() {
        var defaultHour = 0
        var defaultMinutes = 0
        try {
            defaultHour = valorCelda.value!!.substring(0, 2).toInt()
            defaultMinutes = valorCelda.value!!.substring(3, 5).toInt()
        }catch (e:Exception){
            e.printStackTrace()
        }
        HourPickerFragment.newInstance({ _: TimePicker?, hour: Int, minutes ->
            val selectedHour = Utils.twoDigits(hour) + ":" + Utils.twoDigits(minutes)
            _valorCelda.value = selectedHour
        }, defaultHour, defaultMinutes)
            .show(
                ActivityMain.instance.supportFragmentManager,
                "hourPicker"
            )
    }
}