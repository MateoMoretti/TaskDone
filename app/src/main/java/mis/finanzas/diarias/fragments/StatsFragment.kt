package mis.finanzas.diarias.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import com.example.taskdone.R
import android.annotation.SuppressLint
import android.widget.TextView
import android.graphics.Typeface
import android.widget.LinearLayout
import kotlin.Throws
import android.app.Dialog
import android.content.DialogInterface
import android.view.*
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.databinding.FragmentFinanzasStatsBinding
import mis.finanzas.diarias.*
import mis.finanzas.diarias.activities.ActivityFinanzas
import mis.finanzas.diarias.model.Record
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StatsFragment : Fragment() {
    private var binding: FragmentFinanzasStatsBinding? = null
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }
    var all_gastos = ArrayList<Record>()
    var monedas = ArrayList<String>()
    var simbolos = ArrayList<String>()
    var gastos = ArrayList<ArrayList<Record>>()
    var ingresos = ArrayList<ArrayList<Record>>()
    var selected_date_desde: String? = null
    var selected_date_hasta: String? = null
    var cantidad_dias = 0L
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFinanzasStatsBinding.inflate(inflater, container, false)
        var selected_date_desde = Preferences.getPreferenceString(requireContext(), "stats_desde")
        var selected_date_hasta = Preferences.getPreferenceString(requireContext(), "stats_hasta")
        if (selected_date_desde == "") {
            selected_date_desde = resources.getString(R.string.hace_un_mes)
        } else if (Utils.isHoy(selected_date_desde)) {
            selected_date_desde = resources.getString(R.string.hoy)
        }
        if (selected_date_hasta == "" || Utils.isHoy(selected_date_hasta)) {
            selected_date_hasta = resources.getString(R.string.hoy)
        }
        try {
            //filtrar(selected_date_desde, selected_date_hasta)
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
        binding!!.avanzado.setOnClickListener { v: View? ->
            popupAvanzado(
                ArrayList(
                    listOf(
                        resources.getString(R.string.mostrar_publicidad)
                    )
                )
            )
        }
        return binding!!.root
    }

    /*@SuppressLint("SetTextI18n", "InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun cargarStats(desde: Calendar, hasta: Calendar) {
        cleanLayouts()
        cantidad_dias = Utils.diferenciaDeDias(desde, hasta)
        val data = database!!.getGastosBySessionUser(
            Utils.calendarToString(desde),
            Utils.calendarToString(hasta)
        )
        while (data.moveToNext()) {
            val id = data.getInt(0);
            val fecha = data.getString(1)
            val total_gasto = data.getFloat(2)
            val motivo = data.getString(3)
            val ingreso = data.getString(4)
            val nombre_moneda = data.getString(5)
            val simbolo_moneda = data.getString(6)
            val g = Record(fecha, nombre_moneda, total_gasto, motivo, ingreso,id)
            all_gastos.add(g)
            if (!monedas.contains(g.idMon)) {
                monedas.add(nombre_moneda)
                simbolos.add(simbolo_moneda)
            }
        }

        //AGREGO LINEAS DE GASTOS e INGRESOS
        val total_gastos = database!!.getTotalGastosBetweenFechasGroupByMonedas(
            Utils.calendarToString(desde),
            Utils.calendarToString(hasta),
            "0"
        )
        val total_ingresos = database!!.getTotalGastosBetweenFechasGroupByMonedas(
            Utils.calendarToString(desde),
            Utils.calendarToString(hasta),
            "1"
        )
        var hay_gastos = false
        var hay_ingresos = false
        val inflater = requireActivity().layoutInflater
        while (total_gastos.moveToNext()) {
            if (!hay_gastos) {
                @SuppressLint("InflateParams") val view_tipo =
                    inflater.inflate(R.layout.item_texto_simple, null)
                binding!!.layoutTipoGastos.addView(view_tipo)
                @SuppressLint("InflateParams") val view =
                    inflater.inflate(R.layout.item_stats, null)
                (view.findViewById<View>(R.id.total) as TextView).typeface = Typeface.DEFAULT_BOLD
                (view.findViewById<View>(R.id.promedio) as TextView).typeface =
                    Typeface.DEFAULT_BOLD
                binding!!.layoutGastos.addView(view)
                hay_gastos = true
            }
            agregarGastoIngreso(
                total_gastos.getString(2),
                total_gastos.getFloat(1),
                total_gastos.getString(3),
                binding!!.layoutGastos,
                binding!!.layoutTipoGastos
            )
        }
        total_gastos.moveToFirst()
        if (!hay_gastos) {
            @SuppressLint("InflateParams") val view =
                inflater.inflate(R.layout.item_texto_simple, null)
            (view.findViewById<View>(R.id.texto) as TextView).text =
                resources.getString(R.string.sin_gastos)
            (view.findViewById<View>(R.id.texto) as TextView).typeface = Typeface.DEFAULT_BOLD
            binding!!.layoutTituloGasto.addView(view)
        }
        while (total_ingresos.moveToNext()) {
            if (!hay_ingresos) {
                hay_ingresos = true
                @SuppressLint("InflateParams") val view_tipo =
                    inflater.inflate(R.layout.item_texto_simple, null)
                binding!!.layoutTipoIngresos.addView(view_tipo)
                val view = inflater.inflate(R.layout.item_stats, null)
                (view.findViewById<View>(R.id.total) as TextView).typeface = Typeface.DEFAULT_BOLD
                (view.findViewById<View>(R.id.promedio) as TextView).typeface =
                    Typeface.DEFAULT_BOLD
                binding!!.layoutIngresos.addView(view)
            }
            agregarGastoIngreso(
                total_ingresos.getString(2),
                total_ingresos.getFloat(1),
                total_ingresos.getString(3),
                binding!!.layoutIngresos,
                binding!!.layoutTipoIngresos
            )
        }
        total_ingresos.moveToFirst()
        if (!hay_ingresos) {
            @SuppressLint("InflateParams") val view =
                inflater.inflate(R.layout.item_texto_simple, null)
            (view.findViewById<View>(R.id.texto) as TextView).text =
                resources.getString(R.string.sin_ingresos)
            (view.findViewById<View>(R.id.texto) as TextView).typeface = Typeface.DEFAULT_BOLD
            binding!!.layoutTituloIngresos.addView(view)
        }

        //STATS AVANZADOS
        val total_by_tags = database!!.getTotalGastosGroupByTagIngresoMoneda(
            Utils.calendarToString(desde),
            Utils.calendarToString(hasta)
        )
        var hay_tags = false
        var hay_gasto_tag = false
        var hay_ingreso_tag = false

        //No hay informacion de los tags
        @SuppressLint("InflateParams") var view = inflater.inflate(R.layout.item_texto_simple, null)
        (view.findViewById<View>(R.id.texto) as TextView).typeface = Typeface.DEFAULT_BOLD
        (view.findViewById<View>(R.id.texto) as TextView).text =
            resources.getString(R.string.sin_info_sobre_tags)
        view.setPadding(0, 0, 0, 10)
        binding!!.layoutTituloTags.addView(view)
        while (total_by_tags.moveToNext()) {
            if (!hay_tags) {
                binding!!.layoutTituloTags.removeViewAt(1)
                hay_tags = true
            }
            val total = total_by_tags.getFloat(1)
            //String nombre_moneda = total_by_tags.getString(2);
            val simbolo = total_by_tags.getString(3)
            val nombre_tag = total_by_tags.getString(4)
            val ingreso = total_by_tags.getString(5)
            if (ingreso == "0") {
                if (!hay_gasto_tag) {
                    @SuppressLint("InflateParams") val view_tipo =
                        inflater.inflate(R.layout.item_texto_simple, null)
                    (view_tipo.findViewById<View>(R.id.texto) as TextView).text =
                        resources.getString(R.string.gastos)
                    (view_tipo.findViewById<View>(R.id.texto) as TextView).setTextColor(
                        resources.getColor(R.color.rojo_egreso)
                    )
                    (view_tipo.findViewById<View>(R.id.texto) as TextView).typeface =
                        Typeface.DEFAULT_BOLD
                    binding!!.layoutTipoTags.addView(view_tipo)

                    val viewStats = inflater.inflate(R.layout.item_stats, null)
                    (viewStats.findViewById<View>(R.id.total) as TextView).typeface =
                        Typeface.DEFAULT_BOLD
                    (viewStats.findViewById<View>(R.id.promedio) as TextView).typeface =
                        Typeface.DEFAULT_BOLD
                    binding!!.layoutTags.addView(view)
                    hay_gasto_tag = true
                }
            } else {
                if (!hay_ingreso_tag) {
                    @SuppressLint("InflateParams") val view_tipo =
                        inflater.inflate(R.layout.item_texto_simple, null)
                    (view_tipo.findViewById<View>(R.id.texto) as TextView).text =
                        resources.getString(R.string.ingresos)
                    (view_tipo.findViewById<View>(R.id.texto) as TextView).setTextColor(
                        resources.getColor(R.color.verde_ingreso)
                    )
                    (view_tipo.findViewById<View>(R.id.texto) as TextView).typeface =
                        Typeface.DEFAULT_BOLD
                    view_tipo.setPadding(0, 20, 0, 0)
                    binding!!.layoutTipoTags.addView(view_tipo)
                    val viewStats = inflater.inflate(R.layout.item_stats, null)
                    (viewStats.findViewById<View>(R.id.total) as TextView).typeface =
                        Typeface.DEFAULT_BOLD
                    (viewStats.findViewById<View>(R.id.promedio) as TextView).typeface =
                        Typeface.DEFAULT_BOLD
                    viewStats.setPadding(0, 20, 0, 0)
                    binding!!.layoutTags.addView(view)
                    hay_ingreso_tag = true
                }
            }
            @SuppressLint("InflateParams") val view_tipo =
                inflater.inflate(R.layout.item_texto_simple, null)
            (view_tipo.findViewById<View>(R.id.texto) as TextView).text = "$nombre_tag: "
            (view_tipo.findViewById<View>(R.id.texto) as TextView).typeface = Typeface.DEFAULT_BOLD
            binding!!.layoutTipoTags.addView(view_tipo)
            val viewStats = inflater.inflate(R.layout.item_stats, null)
            (viewStats.findViewById<View>(R.id.total) as TextView).text =
                simbolo + " " + Utils.formatoCantidad(total)
            (viewStats.findViewById<View>(R.id.promedio) as TextView).text =
                simbolo + " " + Utils.formatoCantidad(total / cantidad_dias)
            binding!!.layoutTags.addView(view)
        }
    }*/

    @SuppressLint("SetTextI18n")
    private fun agregarGastoIngreso(
        moneda: String,
        total: Float,
        simbolo: String,
        layout: LinearLayout,
        layout_tipo: LinearLayout
    ) {
        val inflater = requireActivity().layoutInflater
        val df = DecimalFormat()
        df.maximumFractionDigits = 2
        @SuppressLint("InflateParams") val view_tipo =
            inflater.inflate(R.layout.item_texto_simple, null)
        (view_tipo.findViewById<View>(R.id.texto) as TextView).text = "$moneda: "
        (view_tipo.findViewById<View>(R.id.texto) as TextView).typeface =
            Typeface.DEFAULT_BOLD
        layout_tipo.addView(view_tipo)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.item_stats, null)
        (view.findViewById<View>(R.id.total) as TextView).text =
            simbolo + " " + Utils.formatoCantidad(total)
        (view.findViewById<View>(R.id.promedio) as TextView).text =
            simbolo + " " + Utils.formatoCantidad(total / cantidad_dias)
        layout.addView(view)
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
        //cargarStats(Utils.dateToCalendar(desde_date), Utils.dateToCalendar(hasta_date))
        binding!!.textDesde.text = text_desde
        binding!!.textHasta.text = text_hasta
    }

    private fun cleanLayouts() {
        cantidad_dias = 0L
        gastos.clear()
        ingresos.clear()
        binding!!.textDesde.text = resources.getString(R.string.hace_un_mes)
        binding!!.textHasta.text = resources.getString(R.string.hoy)
        while (binding!!.layoutTituloGasto.childCount != 1) {
            binding!!.layoutTituloGasto.removeViewAt(1)
        }
        while (binding!!.layoutTituloIngresos.childCount != 1) {
            binding!!.layoutTituloIngresos.removeViewAt(1)
        }
        while (binding!!.layoutTituloTags.childCount != 1) {
            binding!!.layoutTituloTags.removeViewAt(1)
        }
        while (binding!!.layoutGastos.childCount != 0) {
            binding!!.layoutGastos.removeViewAt(0)
            binding!!.layoutTipoGastos.removeViewAt(0)
        }
        while (binding!!.layoutIngresos.childCount != 0) {
            binding!!.layoutIngresos.removeViewAt(0)
            binding!!.layoutTipoIngresos.removeViewAt(0)
        }
        while (binding!!.layoutTags.childCount != 0) {
            binding!!.layoutTags.removeViewAt(0)
            binding!!.layoutTipoTags.removeViewAt(0)
        }
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
                    Preferences.savePreferenceString(requireContext(), selectedDate, "stats_desde")
                    if (Utils.isHoy(selected_date_desde!!)) {
                        fecha_a_actualizar.text = resources.getString(R.string.hoy)
                    }
                } else {
                    fecha_a_actualizar.text = selectedDate
                    selected_date_hasta = selectedDate
                    Preferences.savePreferenceString(requireContext(), selectedDate, "stats_hasta")
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

    //Mostrar solo la primera vez, luego acceder de una
    @SuppressLint("RtlHardcoded")
    fun popupAvanzado(strings: ArrayList<String>) {
        if (Preferences.getPreferenceString(requireContext(), "acepto_publicidad") == "1") {
            aceptarPublicidadEIrAvanzados()
        } else {
            val inflater = requireActivity().layoutInflater
            @SuppressLint("InflateParams") val vista =
                inflater.inflate(R.layout.popup_informacion, null)
            val texto_1 = vista.findViewById<TextView>(R.id.texto_1)
            val texto_2 = vista.findViewById<TextView>(R.id.texto_2)
            val texto_3 = vista.findViewById<TextView>(R.id.texto_3)
            val texto_4 = vista.findViewById<TextView>(R.id.texto_4)
            val texto_5 = vista.findViewById<TextView>(R.id.texto_5)
            val textos = ArrayList(Arrays.asList(texto_1, texto_2, texto_3, texto_4, texto_5))
            for (x in strings.indices) {
                textos[x].text = strings[x]
            }
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(requireContext().resources.getString(R.string.informacion))
            builder.setView(vista)
                .setPositiveButton(
                    requireContext().resources.getString(R.string.aceptar)
                ) { dialog: DialogInterface?, which: Int -> aceptarPublicidadEIrAvanzados() }
            builder.setCancelable(true)
            builder.setNegativeButton(resources.getString(R.string.cancelar), null)
            val dialog: Dialog = builder.create()
            val window = dialog.window
            window?.setGravity(Gravity.CENTER or Gravity.RIGHT)
            dialog.show()
            dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }

    private fun aceptarPublicidadEIrAvanzados() {
        Preferences.savePreferenceString(requireContext(), "1", "acepto_publicidad")
        Ads.getInstance().show(activity)

        (activity as ActivityFinanzas).updateFragment(R.id.statsAvanzadosFragment)
        findNavController().navigate(R.id.statsAvanzadosFragment)
    }
}