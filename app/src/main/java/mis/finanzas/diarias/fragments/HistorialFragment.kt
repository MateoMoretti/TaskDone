package mis.finanzas.diarias.fragments

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.R
import com.example.taskdone.databinding.FragmentFinanzasHistorialBinding
import mis.finanzas.diarias.DatePickerFragment
import mis.finanzas.diarias.Preferences
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.activities.ActivityFinanzas
import mis.finanzas.diarias.model.Record
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HistorialFragment : Fragment() {
    private lateinit var binding: FragmentFinanzasHistorialBinding
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }

    var selected_date_desde: String? = null
    var selected_date_hasta: String? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasHistorialBinding.inflate(inflater, container, false)
        var selectedDateDesde =
            Preferences.getPreferenceString(requireContext(), "historial_desde")
        var selectedDateHasta =
            Preferences.getPreferenceString(requireContext(), "historial_hasta")
        if (selectedDateDesde == "") {
            selectedDateDesde = resources.getString(R.string.hace_un_mes)
        } else if (Utils.isHoy(selectedDateDesde)) {
            selectedDateDesde = resources.getString(R.string.hoy)
        }
        if (selectedDateHasta == "" || Utils.isHoy(selectedDateHasta)) {
            selectedDateHasta = resources.getString(R.string.hoy)
        }

        try {
            filtrar(selectedDateDesde, selectedDateHasta)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        binding.tituloDesde.setOnClickListener { v: View? ->
            showDatePickerDialog(
                true,
                binding.textDesde
            )
        }
        binding.tituloHasta.setOnClickListener { v: View? ->
            showDatePickerDialog(
                false,
                binding.textHasta
            )
        }
        binding.calendarDesde.setOnClickListener { v: View? ->
            showDatePickerDialog(
                true,
                binding.textDesde
            )
        }
        binding.calendarHasta.setOnClickListener { v: View? ->
            showDatePickerDialog(
                false,
                binding.textHasta
            )
        }
        binding.textDesde.setOnClickListener { v: View? ->
            showDatePickerDialog(
                true,
                binding.textDesde
            )
        }
        binding.textHasta.setOnClickListener { v: View? ->
            showDatePickerDialog(
                false,
                binding.textHasta
            )
        }
        //BORRAR ?
        //if (Preferences.getPreferenceString(requireContext(), "edicion_gasto_id") != "") {
        //    irAEditarGasto()
        //}
        /////
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun loadRecords(from: Date, to: Date) {
        cleanHistorial()

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
                        (item.findViewById<View>(R.id.signo_ingreso) as TextView).text = "-"
                        (item.findViewById<View>(R.id.signo_ingreso) as TextView).setTextColor(
                            requireContext().resources.getColor(R.color.rojo_egreso)
                        )
                        (item.findViewById<View>(R.id.txt_simbolo) as TextView).setTextColor(
                            requireContext().resources.getColor(R.color.rojo_egreso)
                        )
                        (item.findViewById<View>(R.id.txt_cantidad) as TextView).setTextColor(
                            requireContext().resources.getColor(R.color.rojo_egreso)
                        )
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
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.popup_edicion_gasto, binding.constraintEdicion)
        (view.findViewById<View>(R.id.edit_fecha) as TextView).text = record.date
        (view.findViewById<View>(R.id.edit_fecha) as TextView).setOnClickListener {
            showDatePickerDialog(
                view.findViewById<View>(R.id.edit_fecha) as TextView
            )
        }
        (view.findViewById<View>(R.id.edit_cantidad) as EditText).setText(Utils.formatAmount(record.amount))
        (view.findViewById<View>(R.id.edit_motivo) as EditText).setText(record.reason)

        (view.findViewById<View>(R.id.edit_cantidad) as EditText).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {//-
            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verificarCantidad(view.findViewById<View>(R.id.edit_cantidad) as EditText)
            }
            override fun afterTextChanged(editable: Editable) {//-
            }
        })

        if (record.isIncome) (view.findViewById<View>(R.id.check_ingreso) as CheckBox).isChecked = true

        val spinnerArrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            databaseViewModel.getAllCurrency().map { it.name }
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

        spinnerMoneda.setSelection(databaseViewModel.getAllCurrency().indexOf(currency))

        (view.findViewById<View>(R.id.agregar_moneda) as Button).setOnClickListener { irACrearMoneda() }
        (view.findViewById<View>(R.id.agregar_tag) as Button).setOnClickListener { irATags() }

        val tagsId = databaseViewModel.getAllTagRecordByRecordId(record.id).map { it.idTag }
        val tags = databaseViewModel.getTagsByIdList(tagsId).map { it.nombre }
        if(tags.isEmpty()) (view.findViewById<View>(R.id.txt_tag_seleccionados) as TextView).text = resources.getString(R.string.sin_seleccionados)
        else (view.findViewById<View>(R.id.txt_tag_seleccionados) as TextView).text = Utils.arrayListToString(tags)

        (view.findViewById<View>(R.id.cerrar) as ImageView).setOnClickListener { binding.constraintEdicion.removeViewAt(0) }
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
        databaseViewModel.addRecord(record)

        (activity as ActivityFinanzas).updateFragment(R.id.historialFragment)
        findNavController().navigate(R.id.historialFragment)
    }

    private fun popupDeleteRecord(record: Record) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.eliminar))
        builder.setMessage(resources.getString(R.string.estas_seguro))
        val c = DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
            databaseViewModel.deleteRecord(record)
            Toast.makeText(requireContext(),resources.getString(R.string.eliminado_exito), Toast.LENGTH_SHORT).show()
            (activity as ActivityFinanzas).updateFragment(R.id.historialFragment)
            findNavController().navigate(R.id.historialFragment)
        }
        builder.setPositiveButton(resources.getString(R.string.si), c)
        builder.setNegativeButton(resources.getString(R.string.no), null)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }


    private fun irACrearMoneda() {
        (activity as ActivityFinanzas).updateFragment(R.id.crearMonedaFragment)
        findNavController().navigate(R.id.crearMonedaFragment)
    }

    fun irATags() {
        Preferences.savePreferenceString(
            requireContext(),
            "" + R.id.historialFragment,
            "id_fragment_anterior"
        )
        (activity as ActivityFinanzas).updateFragment(R.id.tagsFragment)
        findNavController().navigate(R.id.tagsFragment)
    }


    private fun showDatePickerDialog(t: TextView) {
        val newFragment =
            DatePickerFragment.newInstance { datePicker: DatePicker?, year: Int, month: Int, day: Int ->
                val selectedDate =
                    year.toString() + "/" + Utils.twoDigits(month + 1) + "/" + Utils.twoDigits(day)
                val h = Calendar.getInstance()
                h[year, month] = day
                h.add(Calendar.DAY_OF_YEAR, 1)
                t.text = selectedDate
                Preferences.savePreferenceString(
                    requireContext(),
                    t.text.toString(),
                    "edicion_gasto_fecha"
                )
            }
        newFragment.show(
            Objects.requireNonNull(requireActivity()).supportFragmentManager,
            "datePicker"
        )
    }

    private fun verificarCantidad(e: EditText) {
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
    @Throws(ParseException::class)
    private fun filtrar(desde: String, hasta: String) {
        var text_desde = resources.getString(R.string.hace_un_mes)
        var text_hasta = resources.getString(R.string.hoy)
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy/MM/dd")
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, -30)
        var desde_date = c.time
        var hasta_date = Calendar.getInstance().time
        if (desde != text_desde) {
            if (desde == text_hasta) {
                text_desde = text_hasta
                desde_date = hasta_date
            } else {
                desde_date = sdf.parse(desde)
                text_desde = desde
            }
        }
        if (hasta != text_hasta) {
            hasta_date = sdf.parse(hasta)
            text_hasta = hasta
        }
        loadRecords(desde_date, hasta_date)
        binding.textDesde.text = text_desde
        binding.textHasta.text = text_hasta
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showDatePickerDialog(es_desde: Boolean, fecha_a_actualizar: TextView) {
        val newFragment =
            DatePickerFragment.newInstance { datePicker: DatePicker?, year: Int, month: Int, day: Int ->
                val selectedDate =
                    year.toString() + "/" + twoDigits(month + 1) + "/" + twoDigits(day)
                if (es_desde) {
                    fecha_a_actualizar.text = selectedDate
                    selected_date_desde = selectedDate
                    Preferences.savePreferenceString(
                        requireContext(),
                        selectedDate,
                        "historial_desde"
                    )
                    if (Utils.isHoy(selected_date_desde!!)) {
                        fecha_a_actualizar.text = resources.getString(R.string.hoy)
                    }
                } else {
                    fecha_a_actualizar.text = selectedDate
                    selected_date_hasta = selectedDate
                    Preferences.savePreferenceString(
                        requireContext(),
                        selectedDate,
                        "historial_hasta"
                    )
                    if (Utils.isHoy(selected_date_hasta!!)) {
                        fecha_a_actualizar.text = resources.getString(R.string.hoy)
                    }
                }
                try {
                    filtrar(
                        binding.textDesde.text.toString(),
                        binding.textHasta.text.toString()
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        newFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun twoDigits(n: Int): String {
        return if (n <= 9) "0$n" else n.toString()
    }

    /*@Throws(ParseException::class)
    fun editarGasto(view: View) {
        if (Utils.fechaMayorQueHoy((view.findViewById<View>(R.id.edit_fecha) as TextView).text.toString())) {
            Toast.makeText(requireContext(), R.string.error_fecha_futura, Toast.LENGTH_SHORT).show()
        } else {
            if ((view.findViewById<View>(R.id.edit_cantidad) as EditText).text.toString() == "" || (view.findViewById<View>(
                    R.id.edit_cantidad
                ) as EditText).text.toString() == "0"
            ) {
                Toast.makeText(requireContext(), R.string.error_cantidad_null, Toast.LENGTH_SHORT)
                    .show()
            } else {
                var ingreso = "0"
                if ((view.findViewById<View>(R.id.check_ingreso) as CheckBox).isChecked) {
                    ingreso = "1"
                }
                val cantidad_gasto =
                    (view.findViewById<View>(R.id.edit_cantidad) as EditText).text.toString()
                val editado_exito = dataBase!!.editGasto(
                    Preferences.getPreferenceString(
                        requireContext(),
                        "edicion_gasto_id"
                    ).toInt(),
                    (view.findViewById<View>(R.id.edit_fecha) as TextView).text.toString(),
                    (view.findViewById<View>(R.id.spinner_moneda) as Spinner).selectedItem.toString(),
                    Utils.stringToFloat(cantidad_gasto).toFloat(),
                    (view.findViewById<View>(R.id.edit_motivo) as EditText).text.toString(),
                    tags,
                    ingreso
                )
                if (editado_exito) {
                    Toast.makeText(requireContext(), R.string.editado_exito, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), R.string.error_editado, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }*/
}