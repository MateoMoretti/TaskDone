package mis.finanzas.diarias.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.taskdone.R
import android.view.Gravity
import android.text.TextWatcher
import android.text.Editable
import mis.finanzas.diarias.DatePickerFragment
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.activities.ActivityFinanzas
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.model.Record
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import mis.finanzas.diarias.viewmodels.RecordViewModel
import java.text.ParseException
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class PrincipalFragment : Fragment() {
    private lateinit var binding: FragmentFinanzasPrincipalBinding
    private val databaseViewModel: DatabaseViewModel by activityViewModels{DatabaseViewmodelFactory(requireContext())}
    private val recordViewModel: RecordViewModel by activityViewModels()

    var tags: ArrayList<String>? = null

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinanzasPrincipalBinding.inflate(inflater, container, false)
        databaseViewModel.getAllCurrency()

        setObservers()

        binding.spinnerMoneda.background =
            resources.getDrawable(R.drawable.fondo_blanco_redondeado)
        binding.spinnerMoneda.gravity = Gravity.CENTER

        binding.agregarTag.setOnClickListener {
            run {
            (activity as ActivityFinanzas).updateFragment(R.id.tagsFragment)
            findNavController().navigate(R.id.tagsFragment)
            }
        }

        binding.ok.setOnClickListener {
                addRecord()
        }
        tags = ArrayList()
        if (arguments != null && requireArguments().getString("scroll_tags") != null) {
            scrollToTag()
        }
        binding.editFecha.setOnClickListener { showDatePicker() }
        binding.agregarMoneda.setOnClickListener {
            (activity as ActivityFinanzas).updateFragment(R.id.crearMonedaFragment)
            findNavController().navigate(R.id.crearMonedaFragment)
        }
        binding.editCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verificarCantidad(charSequence.toString())
                recordViewModel.setAmount(
                    charSequence.toString().let {
                        if (it != "") it.toInt() else 0
                    })
            }
            override fun afterTextChanged(editable: Editable) {//
            }
        })
        binding.editMotivo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                recordViewModel.setReason(binding.editMotivo.text.toString())
            }

            override fun afterTextChanged(editable: Editable) {//
            }
        })
        //cargarGastoPendiente()
        binding.ayuda.setOnClickListener {
            Utils.popupAyuda(
                requireContext(), requireActivity(), ArrayList(
                    Arrays.asList(
                        resources.getString(R.string.ayuda_principal_1),
                        resources.getString(R.string.ayuda_principal_2)
                    )
                )
            )
        }
        reloadData()
        return binding.root
    }

    private fun setObservers(){
        databaseViewModel.currencies.observe(viewLifecycleOwner) { currencyList ->
            if (currencyList.map { it.name }.isEmpty()) {

                (activity as ActivityFinanzas).updateFragment(R.id.crearMonedaFragment)
                findNavController().navigate(R.id.crearMonedaFragment)
            }
            if(binding.spinnerMoneda.adapter == null || binding.spinnerMoneda.adapter?.count != currencyList.count()) {
                val spinnerArrayAdapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
                    requireActivity(),
                    android.R.layout.simple_spinner_dropdown_item,
                    currencyList.map { it.name } as List<String?>
                ) {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getView(position, convertView, parent)
                        (v as TextView).textSize = 20f
                        v.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        return v
                    }
                }
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
                binding.spinnerMoneda.adapter = spinnerArrayAdapter
                binding.spinnerMoneda.setSelection(currencyList.indexOf(recordViewModel.getCurrency()))

                binding.spinnerMoneda.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            adapterView: AdapterView<*>?,
                            view: View?,
                            i: Int,
                            l: Long
                        ) {
                            recordViewModel.setCurrency(currencyList[i])
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>?) {//
                        }
                    }
            }

            while (binding.currencyLayout.childCount != 0) {
                binding.currencyLayout.removeViewAt(0)
            }
            val inflater = requireActivity().layoutInflater
            for (x in currencyList.map { it.name }.indices) {
                @SuppressLint("InflateParams") val view =
                    inflater.inflate(R.layout.item_moneda_cantidad, null)
                @SuppressLint("CutPasteId") val moneda = view.findViewById<TextView>(R.id.name)
                @SuppressLint("CutPasteId") val cantidad = view.findViewById<TextView>(R.id.cantidad)
                moneda.text = currencyList.map { it.name }[x]
                cantidad.text =
                    currencyList.map { it.symbol }[x] + " " + Utils.formatoCantidad(currencyList.map { it.amount }[x])
                binding.currencyLayout.addView(view)
            }
        }
        binding.checkIngreso.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            recordViewModel.setIncome(!recordViewModel.getIncome())
        }
    }

    private fun reloadData(){
        databaseViewModel.refreshAllCurrency()
        recordViewModel.let {
            binding.editFecha.text = it.getDate()
            binding.editCantidad.setText(it.getAmount().toString())
            binding.editMotivo.setText( it.getReason())
            binding.txtTagSeleccionados.text = it.getTagsString(resources)
            binding.checkIngreso.isChecked = it.getIncome()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun cleanAndUpdate() {
        //binding.spinnerMoneda.setSelection(0)
        recordViewModel.setAmount(0)
        recordViewModel.setReason("")
        recordViewModel.setIncome(false)
        recordViewModel.setSelectedTags(arrayListOf())
        reloadData()
        binding.scrollview.fullScroll(ScrollView.FOCUS_UP)
    }


    private fun scrollToTag() {
        binding.scrollview.post { binding.scrollview.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun verificarCantidad(charSequence: String) {
        if (charSequence.length > 1 && charSequence.startsWith("0")) {
            binding.editCantidad.setText(charSequence.substring(1))
            binding.editCantidad.setSelection(binding.editCantidad.length())
        }
    }

    fun addRecord() {
        // FECHA DEL FUTURO NO SE PERMITE
        if (Utils.fechaMayorQueHoy(binding.editFecha.text.toString())) {
            Toast.makeText(requireContext(), R.string.error_fecha_futura, Toast.LENGTH_SHORT).show()
            return
        }
        if(recordViewModel.getAmount() == 0) {
            Toast.makeText(requireContext(), R.string.error_cantidad_null, Toast.LENGTH_SHORT).show()
            return
        }
        val record = Record(recordViewModel.getDate(),
                            recordViewModel.getAmount(),
                            recordViewModel.getReason(),
                            recordViewModel.getIncome(),
                            recordViewModel.getCurrency().id)
        databaseViewModel.addRecord(record)
        Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT)
                .show()

        cleanAndUpdate()
    }

    private fun showDatePicker() {
        DatePickerFragment.newInstance { _: DatePicker?, year: Int, month: Int, day: Int ->
            val selectedDate =
                year.toString() + "/" + Utils.twoDigits(month + 1) + "/" + Utils.twoDigits(day)
            val h = Calendar.getInstance()
            h[year, month] = day
            h.add(Calendar.DAY_OF_YEAR, 1)
            binding.editFecha.text = selectedDate
            recordViewModel.setDate(selectedDate)
        }.show(
            Objects.requireNonNull(requireActivity()).supportFragmentManager,
            "datePicker"
        )
    }


}
