package mis.finanzas.diarias.finanzas.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.R
import com.example.taskdone.databinding.FragmentFinanzasHistorialBinding
import mis.finanzas.diarias.DatePickerFragment
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.ActivityMain
import mis.finanzas.diarias.finanzas.model.Record
import mis.finanzas.diarias.finanzas.model.Currency
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewmodelFactory
import mis.finanzas.diarias.finanzas.viewmodels.EditRecordViewModel
import mis.finanzas.diarias.finanzas.viewmodels.HistorialFilterViewModel
import java.util.Calendar
import java.util.Objects
import java.util.Date

class HistorialFragment : Fragment() {
    private lateinit var binding: FragmentFinanzasHistorialBinding
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }
    private val editRecordViewModel: EditRecordViewModel by activityViewModels()
    private val historialFilterViewModel: HistorialFilterViewModel by activityViewModels()

    private lateinit var currencyList: List<Currency>

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFinanzasHistorialBinding.inflate(inflater, container, false)
        (activity as ActivityMain).updateFragment(R.id.finanzasHistorialFragment)

        currencyList = databaseViewModel.refreshAllCurrency()
        loadRecords(historialFilterViewModel.getDesdeDate(), historialFilterViewModel.getHastaDate())

        binding.tituloDesde.setOnClickListener { showDatePickerDialog(binding.textDesde) }
        binding.tituloHasta.setOnClickListener { showDatePickerDialog(binding.textHasta) }
        binding.calendarDesde.setOnClickListener { showDatePickerDialog(binding.textDesde) }
        binding.calendarHasta.setOnClickListener { showDatePickerDialog(binding.textHasta) }
        binding.textDesde.setOnClickListener { showDatePickerDialog(binding.textDesde) }
        binding.textHasta.setOnClickListener { showDatePickerDialog(binding.textHasta) }

        editRecordViewModel.getRecord()?.let { record ->
            databaseViewModel.getCurrencyById(record.idCurrency)?.let { currency ->
                editRecord(record, currency)
            }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun loadRecords(from: Date, to: Date) {
        cleanHistorial()

        binding.textDesde.text = historialFilterViewModel.getDesdeString(resources)
        binding.textHasta.text = historialFilterViewModel.getHastaString(resources)

        val data = databaseViewModel.getRecords(from, to)

        val allDates = data.map { it.date }.distinct()

        //LLENO LOS TITULOS DE FECHAS
        if (allDates.isEmpty()) binding.noData.visibility = View.VISIBLE
        
        else {
            binding.noData.visibility = View.INVISIBLE
            val inflater = requireActivity().layoutInflater
            for (x in allDates.indices) {
                @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.elementos_historial, null)
                if (x == 0) view.findViewById<View>(R.id.linea).visibility = View.INVISIBLE
                
                val layoutItems = view.findViewById<LinearLayout>(R.id.layout_item_historial)
                binding.layoutHistorial.addView(view)
                val fecha = view.findViewById<TextView>(R.id.fecha)
                fecha.text = Utils.getDia(allDates[x])

                val recordsInDate = data.filter { it.date == allDates[x] }

                for (record in recordsInDate) {
                    @SuppressLint("InflateParams") val item = inflater.inflate(R.layout.item_historial, null)
                    (item.findViewById<View>(R.id.signo_ingreso) as TextView).text = "+"
                    if (!record.isIncome) {
                        val rojoEgreso = ContextCompat.getColor(requireContext(), R.color.rojo_egreso)
                        (item.findViewById<View>(R.id.signo_ingreso) as TextView).text = "-"
                        (item.findViewById<View>(R.id.signo_ingreso) as TextView).setTextColor(rojoEgreso)
                        (item.findViewById<View>(R.id.txt_simbolo) as TextView).setTextColor(rojoEgreso)
                        (item.findViewById<View>(R.id.txt_cantidad) as TextView).setTextColor(rojoEgreso)
                    }
                    val currency = databaseViewModel.getCurrencyById(record.idCurrency)
                    (item.findViewById<View>(R.id.txt_simbolo) as TextView).text = currency!!.symbol
                    (item.findViewById<View>(R.id.txt_cantidad) as TextView).text = Utils.formatAmount(record.amount)
                    (item.findViewById<View>(R.id.txt_motivo) as TextView).text = record.reason
                    item.findViewById<View>(R.id.edit).setOnClickListener { editRecord(record, currency) }
                    layoutItems.addView(item)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun editRecord(record: Record, currency: Currency) {
        editRecordViewModel.setRecord(record)
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.popup_edicion_gasto, binding.constraintEdicion)
        (view.findViewById<View>(R.id.edit_fecha) as TextView).let { date ->
            date.text = record.date
            date.setOnClickListener {
                showDatePickerDialogEdit(date, record)
            }
        }

        (view.findViewById<View>(R.id.edit_cantidad) as EditText).let { amount ->
            amount.setText(Utils.formatAmount(record.amount))
            amount.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {//-
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    val editText = view.findViewById<View>(R.id.edit_cantidad) as EditText
                    verifyAmount(editText)
                    try {
                        record.amount = editText.text.toString().toInt()
                        editRecordViewModel.setRecord(record)
                    } catch (_: Exception){//-
                    }
                }

                override fun afterTextChanged(editable: Editable) {//-
                }
            })
        }

        (view.findViewById<View>(R.id.edit_motivo) as EditText).let { reason ->
            reason.setText(record.reason)
            reason.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {//-
                }
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    val editText = view.findViewById<View>(R.id.edit_motivo) as EditText
                    try {
                        record.reason = editText.text.toString()
                        editRecordViewModel.setRecord(record)
                    } catch (_:Exception){//-
                    }
                }
                override fun afterTextChanged(editable: Editable) {//-
                }
            })
        }

        (view.findViewById<View>(R.id.check_ingreso) as CheckBox).let {income ->
            if (record.isIncome) income.isChecked = true
            income.setOnClickListener {
                record.isIncome = income.isChecked
                editRecordViewModel.setRecord(record)
            }
        }

        val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            currencyList.map { it.name }
        ) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).textSize = 20f
                v.textAlignment = View.TEXT_ALIGNMENT_CENTER
                return v
            }
        }
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
        val spinnerMoneda = view.findViewById<View>(R.id.spinner_moneda) as Spinner
        spinnerMoneda.adapter = spinnerArrayAdapter
        spinnerMoneda.background = resources.getDrawable(R.drawable.fondo_blanco_redondeado)
        spinnerMoneda.gravity = Gravity.CENTER

        spinnerMoneda.setSelection(currencyList.indexOf(currency))

        spinnerMoneda.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                    currencyList.let {
                        try {
                            record.idCurrency = it[i].id
                        }catch (_:Exception){
                            record.idCurrency = it[0].id
                        }
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {//-
                }
            }

        (view.findViewById<View>(R.id.agregar_moneda) as Button).setOnClickListener {
            findNavController().navigate(R.id.finanzasCrearMonedaFragment)
        }
        (view.findViewById<View>(R.id.agregar_tag) as Button).setOnClickListener {
            findNavController().navigate(R.id.finanzasTagsFragment)
        }


        var tagsId = databaseViewModel.getAllTagRecordByRecordId(record.id).map { it.idTag }
        editRecordViewModel.getRecord()?.let {

            tagsId = editRecordViewModel.getSelectedTags().map { it.id }
        }
        val tags = databaseViewModel.getTagsByIdList(tagsId).map { it.nombre }

        if(tags.isEmpty()) (view.findViewById<View>(R.id.txt_tag_seleccionados) as TextView).text = resources.getString(R.string.sin_seleccionados)
        else (view.findViewById<View>(R.id.txt_tag_seleccionados) as TextView).text = Utils.arrayListToString(tags)

        (view.findViewById<View>(R.id.cerrar) as ImageView).setOnClickListener {
            editRecordViewModel.endEdit()
            binding.constraintEdicion.removeViewAt(0) }
        (view.findViewById<View>(R.id.delete) as ImageView).setOnClickListener { popupDeleteRecord(record) }
        (view.findViewById<View>(R.id.ok) as Button).setOnClickListener { updateRecord(view, record) }
    }

    private fun updateRecord(view: View, record: Record) {
        val date = (view.findViewById<View>(R.id.edit_fecha) as TextView).text.toString()
        if (Utils.fechaMayorQueHoy(date)){
            Toast.makeText(requireContext(), R.string.error_fecha_futura, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            record.amount = (view.findViewById<View>(R.id.edit_cantidad) as EditText).text.toString().toInt()
        }catch (e:java.lang.Exception){
            Toast.makeText(requireContext(), R.string.error_cantidad_null, Toast.LENGTH_SHORT).show()
            return
        }

        record.date = date
        record.idCurrency = databaseViewModel.getCurrencyByName(
            (view.findViewById<View>(R.id.spinner_moneda) as Spinner).selectedItem.toString())!!.id

        record.reason = (view.findViewById<View>(R.id.edit_motivo) as EditText).text.toString()
        record.isIncome = (view.findViewById<View>(R.id.check_ingreso) as CheckBox).isChecked
        databaseViewModel.updateRecord(oldRecord = databaseViewModel.getRecordById(record.id), record)
        editRecordViewModel.endEdit()

        findNavController().navigate(R.id.finanzasHistorialFragment)
    }

    private fun popupDeleteRecord(record: Record) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.eliminar))
        builder.setMessage(resources.getString(R.string.estas_seguro))
        val c = DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
            databaseViewModel.deleteRecord(record)
            editRecordViewModel.endEdit()
            Toast.makeText(requireContext(),resources.getString(R.string.eliminado_exito), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.finanzasHistorialFragment)
        }
        builder.setPositiveButton(resources.getString(R.string.si), c)
        builder.setNegativeButton(resources.getString(R.string.no), null)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }

    private fun showDatePickerDialogEdit(t: TextView, record: Record) {
        val newFragment =
            DatePickerFragment.newInstance { _: DatePicker?, year: Int, month: Int, day: Int ->
                val selectedDate = year.toString() + "/" + Utils.twoDigits(month + 1) + "/" + Utils.twoDigits(day)
                val h = Calendar.getInstance()
                h[year, month] = day
                h.add(Calendar.DAY_OF_YEAR, 1)
                t.text = selectedDate
                record.date = selectedDate
                editRecordViewModel.setRecord(record)
            }
        newFragment.show(
            Objects.requireNonNull(requireActivity()).supportFragmentManager,
            "datePicker"
        )
    }

    private fun verifyAmount(e: EditText) {
        if (e.text.toString() != "" && e.text.toString() != "0") {
            if (e.text.toString().startsWith("0")) {
                e.setText(e.text.toString().substring(1))
                e.setSelection(e.length())
            }
        }
    }

    private fun cleanHistorial() {
        binding.textDesde.text = resources.getString(R.string.hace_un_mes)
        binding.textHasta.text = resources.getString(R.string.hoy)
        while (binding.layoutHistorial.childCount != 0) {
            binding.layoutHistorial.removeViewAt(0)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showDatePickerDialog(updateDate: TextView) {
        val newFragment =
            DatePickerFragment.newInstance { _: DatePicker?, year: Int, month: Int, day: Int ->
                val selectedDate = year.toString() + "/" + twoDigits(month + 1) + "/" + twoDigits(day)
                updateDate.text = selectedDate

                val from = Utils.stringToDate(binding.textDesde.text.toString())
                val to = Utils.stringToDate(binding.textHasta.text.toString())
                historialFilterViewModel.setDesdeDate(from)
                historialFilterViewModel.setHastaDate(to)
                loadRecords(from, to)

            }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun twoDigits(n: Int): String {
        return if (n <= 9) "0$n" else n.toString()
    }
}