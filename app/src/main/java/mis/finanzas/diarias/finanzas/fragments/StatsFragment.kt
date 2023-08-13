package mis.finanzas.diarias.finanzas.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import com.example.taskdone.R
import android.annotation.SuppressLint
import android.widget.TextView
import android.graphics.Typeface
import android.widget.LinearLayout
import android.app.Dialog
import android.content.DialogInterface
import android.view.*
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.databinding.FragmentFinanzasStatsBinding
import mis.finanzas.diarias.*
import mis.finanzas.diarias.finanzas.ActivityFinanzas
import mis.finanzas.diarias.finanzas.model.Record
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewmodelFactory
import mis.finanzas.diarias.finanzas.viewmodels.StatsViewModel
import java.text.DecimalFormat
import java.util.*
import java.util.stream.Collectors

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentFinanzasStatsBinding
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }
    private val statsViewModel: StatsViewModel by activityViewModels()

    private lateinit var inflater: LayoutInflater
    private var dayDifference = 0L
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinanzasStatsBinding.inflate(inflater, container, false)
        (activity as ActivityFinanzas).updateFragment(R.id.statsFragment)
        this.inflater = inflater

        loadStats(statsViewModel.getDesdeDate(), statsViewModel.getHastaDate())

        binding.tituloDesde.setOnClickListener { showDatePickerDialog(binding.textDesde) }
        binding.tituloHasta.setOnClickListener { showDatePickerDialog(binding.textHasta) }
        binding.calendarDesde.setOnClickListener { showDatePickerDialog(binding.textDesde) }
        binding.calendarHasta.setOnClickListener { showDatePickerDialog(binding.textHasta) }
        binding.textDesde.setOnClickListener { showDatePickerDialog(binding.textDesde) }
        binding.textHasta.setOnClickListener { showDatePickerDialog(binding.textHasta) }

        binding.avanzado.setOnClickListener { _: View? ->
            popupAvanzado(ArrayList(listOf(resources.getString(R.string.mostrar_publicidad))))
        }
        return binding.root
    }
    @SuppressLint("InflateParams")
    private fun loadBalance(listRecord: List<Record>,
                            layoutRecord:LinearLayout,
                            layoutType: LinearLayout,
                            layoutTitle: LinearLayout,
                            emptyStringResource: Int){
        if (listRecord.isEmpty()) {
            val view = inflater.inflate(R.layout.item_texto_simple, null)
            (view.findViewById<View>(R.id.texto) as TextView).let {
                it.text = resources.getString(emptyStringResource)
                it.typeface = Typeface.DEFAULT_BOLD
            }
            layoutTitle.addView(view)
        }else {
            val isIncomeView = inflater.inflate(R.layout.item_texto_simple, null)
            layoutType.addView(isIncomeView)
            val view = inflater.inflate(R.layout.item_stats, null)
            (view.findViewById<View>(R.id.total) as TextView).typeface = Typeface.DEFAULT_BOLD
            (view.findViewById<View>(R.id.promedio) as TextView).typeface = Typeface.DEFAULT_BOLD
            layoutRecord.addView(view)
        }

        for(listGroupByCurrency in listRecord.groupBy { it.idCurrency }){
            val totalAmount = listGroupByCurrency.value.stream().map { it.amount }.collect(Collectors.toList()).sum()

            val currency = databaseViewModel.getCurrencyById(listGroupByCurrency.value[0].idCurrency)
            currency?.let {
                addRecordUI(
                    totalAmount,
                    it.name,
                    it.symbol,
                    layoutRecord,
                    layoutType
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun loadTags(mainRecordList: List<Record>,
                         isIncomeStringResource: Int,
                         colorResource: Int,
                         ){

        val isIncomeView = inflater.inflate(R.layout.item_texto_simple, null)
        (isIncomeView.findViewById<View>(R.id.texto) as TextView).let {
            it.text = resources.getString(isIncomeStringResource)
            it.setTextColor(ContextCompat.getColor(requireContext(), colorResource))
            it.typeface = Typeface.DEFAULT_BOLD
        }
        isIncomeView.setPadding(0, 20, 0, 0)
        binding.layoutTipoTags.addView(isIncomeView)

        val header = inflater.inflate(R.layout.item_stats, null)
        val headerTotal = (header.findViewById<View>(R.id.total) as TextView)
        val headerAverage = (header.findViewById<View>(R.id.promedio) as TextView)
        headerTotal.typeface = Typeface.DEFAULT_BOLD
        headerAverage.typeface = Typeface.DEFAULT_BOLD
        binding.layoutTags.addView(header)
        header.setPadding(0, 20, 0, 0)

        //Lista con ID tag e ID record
        val listTagRecord = databaseViewModel.getAllTagRecordByRecordIdList(mainRecordList.map { it.id })

        //Agrupo por tags y recorro
        for(listTag in listTagRecord.groupBy { it.idTag }){
            //Obtengo records
            val records = mainRecordList.filter { record -> record.id in listTag.value.stream().map { it.idRecord }.collect(Collectors.toList()) }

            val tag = databaseViewModel.getTagById(listTag.value[0].idTag)
            //Los agrupo por monedas
            for(recordsGroupByCurrency in records.groupBy { it.idCurrency }){

                val currency = databaseViewModel.getCurrencyById(recordsGroupByCurrency.value[0].idCurrency)
                val finalAmount = recordsGroupByCurrency.value.sumOf { it.amount }

                val lineView = inflater.inflate(R.layout.item_texto_simple, null)
                (lineView.findViewById<View>(R.id.texto) as TextView).let {
                    it.text = "${tag?.nombre}: "
                    it.typeface = Typeface.DEFAULT_BOLD
                }
                binding.layoutTipoTags.addView(lineView)
                val line = inflater.inflate(R.layout.item_stats, null)
                val totalAmount = (line.findViewById<View>(R.id.total) as TextView)
                val average = (line.findViewById<View>(R.id.promedio) as TextView)
                totalAmount.text = currency?.symbol + " " + Utils.formatAmount(finalAmount)
                average.text = currency?.symbol + " " + Utils.formatAmount(finalAmount / dayDifference)
                binding.layoutTags.addView(line)

            }

        }




    }

    @SuppressLint("SetTextI18n", "InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun loadStats(from: Date, to: Date) {
        cleanLayouts()
        dayDifference = Utils.diferenciaDeDias(from, to)
        binding.textDesde.text = statsViewModel.getDesdeString(resources)
        binding.textHasta.text = statsViewModel.getHastaString(resources)
        val records = databaseViewModel.getRecords(from, to)

        val gastos = records.stream().filter{!it.isIncome}.collect(Collectors.toList())
        val ingresos = records.stream().filter{it.isIncome}.collect(Collectors.toList())
        loadBalance(gastos, binding.layoutGastos, binding.layoutTipoGastos, binding.layoutTituloGasto, R.string.sin_gastos)
        loadBalance(ingresos, binding.layoutIngresos, binding.layoutTipoIngresos, binding.layoutTituloIngresos,R.string.sin_ingresos)

        loadTags(gastos, R.string.gastos, R.color.rojo_egreso)
        loadTags(ingresos, R.string.ingresos, R.color.verde_ingreso)
        if(records.isEmpty()) {
            val view = inflater.inflate(R.layout.item_texto_simple, null)
            (view.findViewById<View>(R.id.texto) as TextView).let {
                it.typeface = Typeface.DEFAULT_BOLD
                it.text = resources.getString(R.string.sin_info_sobre_tags)
            }
            view.setPadding(0, 0, 0, 10)
            binding.layoutTituloTags.addView(view)
        }
    }
    @SuppressLint("SetTextI18n", "InflateParams")
    private fun addRecordUI(
        amount: Int,
        currency: String,
        symbol: String,
        layout: LinearLayout,
        layoutType: LinearLayout
    ) {
        val df = DecimalFormat()
        df.maximumFractionDigits = 2
        val viewType = inflater.inflate(R.layout.item_texto_simple, null)
        (viewType.findViewById<View>(R.id.texto) as TextView).let {
            it.text = "$currency: "
            it.typeface = Typeface.DEFAULT_BOLD
        }
        layoutType.addView(viewType)
        val view = inflater.inflate(R.layout.item_stats, null)
        (view.findViewById<View>(R.id.total) as TextView).text = symbol + " " + Utils.formatAmount(amount)
        (view.findViewById<View>(R.id.promedio) as TextView).text = symbol + " " + Utils.formatAmount(amount / dayDifference)
        layout.addView(view)
    }
    private fun cleanLayouts() {
        binding.textDesde.text = resources.getString(R.string.hace_un_mes)
        binding.textHasta.text = resources.getString(R.string.hoy)
        while (binding.layoutTituloGasto.childCount != 1) {
            binding.layoutTituloGasto.removeViewAt(1)
        }
        while (binding.layoutTituloIngresos.childCount != 1) {
            binding.layoutTituloIngresos.removeViewAt(1)
        }
        while (binding.layoutTituloTags.childCount != 1) {
            binding.layoutTituloTags.removeViewAt(1)
        }
        while (binding.layoutGastos.childCount != 0) {
            binding.layoutGastos.removeViewAt(0)
            binding.layoutTipoGastos.removeViewAt(0)
        }
        while (binding.layoutIngresos.childCount != 0) {
            binding.layoutIngresos.removeViewAt(0)
            binding.layoutTipoIngresos.removeViewAt(0)
        }
        while (binding.layoutTags.childCount != 0) {
            binding.layoutTags.removeViewAt(0)
            binding.layoutTipoTags.removeViewAt(0)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showDatePickerDialog(updateDate: TextView) {
        val newFragment =
            DatePickerFragment.newInstance { datePicker: DatePicker?, year: Int, month: Int, day: Int ->
                val selectedDate = year.toString() + "/" + twoDigits(month + 1) + "/" + twoDigits(day)
                updateDate.text = selectedDate

                val from = Utils.stringToDate(binding.textDesde.text.toString())
                val to = Utils.stringToDate(binding.textHasta.text.toString())
                statsViewModel.setDesdeDate(from)
                statsViewModel.setHastaDate(to)
                loadStats(from, to)
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
            val vista = inflater.inflate(R.layout.popup_informacion, null)
            val text1 = vista.findViewById<TextView>(R.id.texto_1)
            val texto_2 = vista.findViewById<TextView>(R.id.texto_2)
            val texto_3 = vista.findViewById<TextView>(R.id.texto_3)
            val texto_4 = vista.findViewById<TextView>(R.id.texto_4)
            val texto_5 = vista.findViewById<TextView>(R.id.texto_5)
            val textos = ArrayList(Arrays.asList(text1, texto_2, texto_3, texto_4, texto_5))
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

        findNavController().navigate(R.id.statsAvanzadosFragment)
    }
}