package mis.finanzas.diarias.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HistorialFragment : Fragment() {
    private var binding: FragmentFinanzasHistorialBinding? = null
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }

    var selected_date_desde: String? = null
    var selected_date_hasta: String? = null
    var tags = ArrayList<String>()
    var monedas = ArrayList<String>()
    var cantidades = ArrayList<Float>()
    var simbolos = ArrayList<String>()

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasHistorialBinding.inflate(inflater, container, false)
        var selected_date_desde =
            Preferences.getPreferenceString(requireContext(), "historial_desde")
        var selected_date_hasta =
            Preferences.getPreferenceString(requireContext(), "historial_hasta")
        if (selected_date_desde == "") {
            selected_date_desde = resources.getString(R.string.hace_un_mes)
        } else if (Utils.isHoy(selected_date_desde)) {
            selected_date_desde = resources.getString(R.string.hoy)
        }
        if (selected_date_hasta == "" || Utils.isHoy(selected_date_hasta)) {
            selected_date_hasta = resources.getString(R.string.hoy)
        }
        //dataBase = DataBase(userViewModel, requireContext())
        //val data = dataBase!!.getMonedasByUserId(userViewModel.getId())
        /*while (data.moveToNext()) {
            monedas.add(data.getString(1))
            cantidades.add(data.getFloat(2))
            simbolos.add(data.getString(3))
        }*/
        try {
            filtrar(selected_date_desde, selected_date_hasta)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        binding!!.tituloDesde.setOnClickListener { v: View? ->
            showDatePickerDialog(
                true,
                binding!!.textDesde
            )
        }
        binding!!.tituloHasta.setOnClickListener { v: View? ->
            showDatePickerDialog(
                false,
                binding!!.textHasta
            )
        }
        binding!!.calendarDesde.setOnClickListener { v: View? ->
            showDatePickerDialog(
                true,
                binding!!.textDesde
            )
        }
        binding!!.calendarHasta.setOnClickListener { v: View? ->
            showDatePickerDialog(
                false,
                binding!!.textHasta
            )
        }
        binding!!.textDesde.setOnClickListener { v: View? ->
            showDatePickerDialog(
                true,
                binding!!.textDesde
            )
        }
        binding!!.textHasta.setOnClickListener { v: View? ->
            showDatePickerDialog(
                false,
                binding!!.textHasta
            )
        }
        //BORRAR ?
        //if (Preferences.getPreferenceString(requireContext(), "edicion_gasto_id") != "") {
        //    irAEditarGasto()
        //}
        /////
        return binding!!.root
    }

    /*@SuppressLint("SetTextI18n")
    private fun cargarHistorial(desde_date: Date, hasta_date: Date) {
        cleanHistorial()
        *//*val data = dataBase!!.getGastosBySessionUser(
            Utils.dateToString(desde_date),
            Utils.dateToString(hasta_date)
        )*//*
        val data = recordViewModel.getRecords(desde_date, hasta_date)
        val data_items = ArrayList<CompleteRecord>()
        val fechas = ArrayList<String>()
        for (item in data) {
            if (!fechas.contains(item.fecha)) {
                fechas.add(item.fecha)
            }

            val record = CompleteRecord(item.id,
                item.fecha,
                item.idMon,
                item.totalGasto,
                item.motivo,
                item.ingreso,
                if (item.ingreso == "0") "-" else "+",
                null,
                null,
                null,
                formattedAmount = Utils.formatoCantidad(item.totalGasto))
            data_items.add(record)

            //Agregar al modelo de Record
            //i.nombre_moneda = data.getString(5)
            //i.simbolo = data.getString(6)
        }

        //LLENO LOS TITULOS DE FECHAS
        if (fechas.size == 0) {
            binding!!.sinDatos.visibility = View.VISIBLE
        } else {
            binding!!.sinDatos.visibility = View.INVISIBLE
            val inflater = requireActivity().layoutInflater
            for (x in fechas.indices) {
                @SuppressLint("InflateParams") val view =
                    inflater.inflate(R.layout.elementos_historial, null)
                if (x == 0) {
                    view.findViewById<View>(R.id.linea).visibility = View.INVISIBLE
                }
                val layout_items = view.findViewById<LinearLayout>(R.id.layout_item_historial)
                binding!!.layoutHistorial.addView(view)
                val fecha = view.findViewById<TextView>(R.id.fecha)
                fecha.text = Utils.getDia(fechas[x])
                for (i in data_items) {
                    if (i.fecha == fechas[x]) {
                        @SuppressLint("InflateParams") val item =
                            inflater.inflate(R.layout.item_historial, null)
                        if (i.currencySymbol == "-") {
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
                        (item.findViewById<View>(R.id.signo_ingreso) as TextView).text = i.signo
                        (item.findViewById<View>(R.id.txt_simbolo) as TextView).text = i.currencySymbol
                        (item.findViewById<View>(R.id.txt_cantidad) as TextView).text = i.formattedAmount
                        (item.findViewById<View>(R.id.txt_motivo) as TextView).text = i.motivo
                        item.findViewById<View>(R.id.editar)
                            .setOnClickListener { v: View? -> irAEditarGasto(i) }
                        layout_items.addView(item)
                    }
                }
            }
        }
    }*/

    /*@SuppressLint("UseCompatLoadingForDrawables")
    private fun irAEditarGasto(item: CompleteRecord) {
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val view =
            inflater.inflate(R.layout.popup_edicion_gasto, binding!!.constraintEdicion)
        if (Preferences.getPreferenceString(requireContext(), "edicion_gasto_id") == "") {
            Preferences.savePreferenceString(
                requireContext(),
                Integer.toString(item.ID),
                "edicion_gasto_id"
            )
        }
        if (Preferences.getPreferenceString(requireContext(), "edicion_gasto_fecha") != "") {
            (view.findViewById<View>(R.id.edit_fecha) as TextView).text =
                Preferences.getPreferenceString(requireContext(), "edicion_gasto_fecha")
        } else {
            Preferences.savePreferenceString(requireContext(), item.fecha, "edicion_gasto_fecha")
            (view.findViewById<View>(R.id.edit_fecha) as TextView).text = item.fecha
        }
        (view.findViewById<View>(R.id.edit_fecha) as TextView).setOnClickListener { v: View? ->
            showDatePickerDialog(
                view.findViewById<View>(R.id.edit_fecha) as TextView
            )
        }
        if (Preferences.getPreferenceString(requireContext(), "edicion_gasto_motivo") != "") {
            (view.findViewById<View>(R.id.edit_motivo) as EditText).setText(
                Preferences.getPreferenceString(
                    requireContext(),
                    "edicion_gasto_motivo"
                )
            )
        } else {
            Preferences.savePreferenceString(requireContext(), item.motivo, "edicion_gasto_motivo")
            (view.findViewById<View>(R.id.edit_motivo) as EditText).setText(item.motivo)
        }
        (view.findViewById<View>(R.id.edit_motivo) as EditText).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Preferences.savePreferenceString(
                    requireContext(),
                    (view.findViewById<View>(R.id.edit_motivo) as EditText).text.toString(),
                    "edicion_gasto_motivo"
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        if (Preferences.getPreferenceString(requireContext(), "edicion_gasto_cantidad") != "") {
            (view.findViewById<View>(R.id.edit_cantidad) as EditText).setText(
                Preferences.getPreferenceString(
                    requireContext(),
                    "edicion_gasto_cantidad"
                )
            )
        } else {
            Preferences.savePreferenceString(
                requireContext(),
                item.formattedAmount,
                "edicion_gasto_cantidad"
            )
            (view.findViewById<View>(R.id.edit_cantidad) as EditText).setText(item.formattedAmount)
        }
        (view.findViewById<View>(R.id.edit_cantidad) as EditText).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verificarCantidad(view.findViewById<View>(R.id.edit_cantidad) as EditText)
                Preferences.savePreferenceString(
                    requireContext(),
                    (view.findViewById<View>(R.id.edit_cantidad) as EditText).text.toString(),
                    "edicion_gasto_cantidad"
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        val ingreso = Preferences.getPreferenceString(requireContext(), "edicion_gasto_ingreso")
        if (ingreso != "") {
            if (ingreso == "1") {
                (view.findViewById<View>(R.id.check_ingreso) as CheckBox).isChecked = true
            }
        } else {
            if (item.signo == "+") {
                (view.findViewById<View>(R.id.check_ingreso) as CheckBox).isChecked = true
                Preferences.savePreferenceString(requireContext(), "1", "edicion_gasto_ingreso")
            } else {
                Preferences.savePreferenceString(requireContext(), "0", "edicion_gasto_ingreso")
            }
        }
        (view.findViewById<View>(R.id.check_ingreso) as CheckBox).setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            guardarIngresoPendiente(
                b
            )
        }
        val spinnerArrayAdapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            monedas as List<String?>
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
        spinnerMoneda.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                Preferences.savePreferenceString(
                    requireContext(),
                    Integer.toString(spinnerMoneda.selectedItemPosition),
                    "edicion_gasto_moneda_index"
                )
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val moneda_index =
            Preferences.getPreferenceString(requireContext(), "edicion_gasto_moneda_index")
        if (moneda_index != "") {
            spinnerMoneda.setSelection(moneda_index.toInt())
        } else {
            for (x in monedas.indices) {
                if (monedas[x] == item.currencyName) {
                    spinnerMoneda.setSelection(x)
                }
            }
        }
        (view.findViewById<View>(R.id.agregar_moneda) as Button).setOnClickListener { v: View? -> irACrearMoneda() }
        (view.findViewById<View>(R.id.agregar_tag) as Button).setOnClickListener { v: View? -> irATags() }
        if (Preferences.getPreferenceString(requireContext(), "edicion_gasto_tags") == "") {
            val database = DataBase(userViewModel, requireContext())
            val tags = database.getTagsByGastoId(item.ID)
            var hay_tags = false
            val tags_seleccionados = ArrayList<String>()
            while (tags.moveToNext()) {
                hay_tags = true
                tags_seleccionados.add(tags.getString(1))
            }
            (view.findViewById<View>(R.id.txt_tag_seleccionados) as TextView).text =
                Utils.arrayListToString(tags_seleccionados)
            Preferences.savePreferenceString(
                requireContext(),
                Utils.arrayListToString(tags_seleccionados),
                "edicion_gasto_tags"
            )
            if (!hay_tags) {
                (view.findViewById<View>(R.id.txt_tag_seleccionados) as TextView).text =
                    resources.getString(R.string.sin_seleccionados)
                Preferences.savePreferenceString(requireContext(), "", "edicion_gasto_tags")
            }
        } else {
            val tags_seleccionados = StringBuilder()
            tags.addAll(
                Arrays.asList(
                    *Preferences.getPreferenceString(
                        requireContext(),
                        "edicion_gasto_tags"
                    ).split(", ").toTypedArray()
                )
            )
            tags_seleccionados.append(Utils.arrayListToString(tags))
            (view.findViewById<View>(R.id.txt_tag_seleccionados) as TextView).text =
                tags_seleccionados.toString()
        }
        (view.findViewById<View>(R.id.cerrar) as ImageView).setOnClickListener { v: View? -> cerrarEdicion() }
        (view.findViewById<View>(R.id.borrar) as ImageView).setOnClickListener { v: View? ->
            popupBorrarGasto(
                view
            )
        }
        (view.findViewById<View>(R.id.ok) as Button).setOnClickListener { v: View? ->
            try {
                guardarYCerrarEdicion(view)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }*/

    /*private fun popupBorrarGasto(view: View) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.eliminar))
        builder.setMessage(resources.getString(R.string.estas_seguro))
        val c = DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
            borrarGasto(view)
        }
        builder.setPositiveButton(resources.getString(R.string.si), c)
        builder.setNegativeButton(resources.getString(R.string.no), null)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }*/

    /*private fun borrarGasto(view: View) {
        val result = dataBase!!.deleteGastoById(
            Preferences.getPreferenceString(
                requireContext(),
                "edicion_gasto_id"
            ).toInt(),
            Preferences.getPreferenceString(requireContext(), "edicion_gasto_cantidad").toFloat(),
            Preferences.getPreferenceString(requireContext(), "edicion_gasto_ingreso"),
            (view.findViewById<View>(R.id.spinner_moneda) as Spinner).selectedItem.toString()
        )
        if (result) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.eliminado_exito),
                Toast.LENGTH_SHORT
            ).show()
            tags.clear()
            Preferences.deletePreferencesEdicionGasto(requireContext())
            findNavController().navigate(R.id.historialFragment)
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.error_eliminado),
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/

    @Throws(ParseException::class)
    private fun guardarYCerrarEdicion(view: View) {
       // editarGasto(view)
        tags.clear()
        Preferences.deletePreferencesEdicionGasto(requireContext())
        findNavController().navigate(R.id.historialFragment)
    }

    private fun cerrarEdicion() {
        tags.clear()
        Preferences.deletePreferencesEdicionGasto(requireContext())
        binding!!.constraintEdicion.removeViewAt(0)
    }

    private fun irACrearMoneda() {
        Preferences.savePreferenceString(
            requireContext(),
            "" + R.id.historialFragment,
            "id_fragment_anterior"
        )
        findNavController().navigate(R.id.crearMonedaFragment)
    }

    fun irATags() {
        Preferences.savePreferenceString(
            requireContext(),
            "" + R.id.historialFragment,
            "id_fragment_anterior"
        )
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
        binding!!.textDesde.text = resources.getString(R.string.hace_un_mes)
        binding!!.textHasta.text = resources.getString(R.string.hoy)
        while (binding!!.layoutHistorial.childCount != 0) {
            binding!!.layoutHistorial.removeViewAt(0)
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
        //cargarHistorial(desde_date, hasta_date)
        binding!!.textDesde.text = text_desde
        binding!!.textHasta.text = text_hasta
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
                        binding!!.textDesde.text.toString(),
                        binding!!.textHasta.text.toString()
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