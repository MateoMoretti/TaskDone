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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.databinding.FragmentFinanzasPrincipalBinding
import mis.finanzas.diarias.Preferences
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import java.lang.StringBuilder
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(api = Build.VERSION_CODES.O)
class PrincipalFragment : Fragment() {
    private lateinit var binding: FragmentFinanzasPrincipalBinding
    private val databaseViewModel: DatabaseViewModel by viewModels{DatabaseViewmodelFactory(requireContext())}

    var tags: ArrayList<String>? = null
    lateinit var monedas: List<String>
    lateinit var cantidades: List<Float>
    lateinit var simbolos: List<String>

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinanzasPrincipalBinding.inflate(inflater, container, false)

        //Obtengo las monedas del usuario con sus cantidades y simbolos
       val data = databaseViewModel.getAllCurrency()

        monedas = data.map { it.nombre }
        cantidades = data.map { it.cantidad.toFloat() }
        simbolos = data.map { it.simbolo }

        llenarLayoutMonedas()

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
        binding!!.spinnerMoneda.adapter = spinnerArrayAdapter
        binding!!.spinnerMoneda.background =
            resources.getDrawable(R.drawable.fondo_blanco_redondeado)
        binding!!.spinnerMoneda.gravity = Gravity.CENTER
        binding!!.agregarTag.setOnClickListener { v: View? -> findNavController().navigate(R.id.action_principalFragment_to_tagsFragment) }
        //dataBase = DataBase(userViewModel, requireContext())
        binding!!.ok.setOnClickListener { v: View? ->
            try {
                //addData()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        tags = ArrayList()
        if (arguments != null) {
            if (requireArguments().getString("scroll_tags") != null) {
                scrollToTag()
            }
        }
        binding!!.editFecha.setOnClickListener { v: View? -> showDatePickerDialog() }
        binding!!.agregarMoneda.setOnClickListener { v: View? -> findNavController().navigate(R.id.action_principalFragment_to_crearMonedaFragment) }
        binding!!.editCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verificarCantidad()
                Preferences.savePreferenceString(
                    requireContext(),
                    binding!!.editCantidad.text.toString(),
                    "gasto_cantidad"
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.editMotivo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Preferences.savePreferenceString(
                    requireContext(),
                    binding!!.editMotivo.text.toString(),
                    "gasto_motivo"
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.checkIngreso.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            guardarIngresoPendiente(
                b
            )
        }
        cargarGastoPendiente()
        binding!!.ayuda.setOnClickListener { v: View? ->
            Utils.popupAyuda(
                requireContext(), requireActivity(), ArrayList(
                    Arrays.asList(
                        resources.getString(R.string.ayuda_principal_1),
                        resources.getString(R.string.ayuda_principal_2)
                    )
                )
            )
        }
        return binding!!.root
    }

    fun guardarIngresoPendiente(b: Boolean) {
        Preferences.savePreferenceString(requireContext(), "0", "gasto_ingreso")
        if (b) {
            Preferences.savePreferenceString(requireContext(), "1", "gasto_ingreso")
        }
    }


    fun cargarGastoPendiente() {
        val fecha = Preferences.getPreferenceString(requireContext(), "gasto_fecha")
        val moneda_index = Preferences.getPreferenceString(requireContext(), "gasto_moneda_index")
        val cantidad = Preferences.getPreferenceString(requireContext(), "gasto_cantidad")
        val motivo = Preferences.getPreferenceString(requireContext(), "gasto_motivo")
        val ingreso = Preferences.getPreferenceString(requireContext(), "gasto_ingreso")
        val tags_concatenados = Preferences.getPreferenceString(requireContext(), "gasto_tags")
        binding!!.editFecha.text =
            Utils.calendarToString(Calendar.getInstance())
        if (fecha != "") {
            binding!!.editFecha.text = fecha
        }
        if (moneda_index != "") {
            binding!!.spinnerMoneda.setSelection(moneda_index.toInt())
        }
        if (cantidad != "") {
            binding!!.editCantidad.setText(cantidad)
        }
        if (motivo != "") {
            binding!!.editMotivo.setText(motivo)
        }
        if (ingreso == "1") {
            binding!!.checkIngreso.isChecked = true
        }
        val tags_seleccionados = StringBuilder()
        if (tags_concatenados != "") {
            tags!!.addAll(Arrays.asList(*tags_concatenados.split(", ").toTypedArray()))
            tags_seleccionados.append(Utils.arrayListToString(tags!!))
        } else {
            tags_seleccionados.append(resources.getString(R.string.sin_seleccionados))
        }
        binding!!.txtTagSeleccionados.text = tags_seleccionados
    }

    @SuppressLint("SetTextI18n")
    private fun llenarLayoutMonedas() {
        while (binding!!.layoutMonedas.childCount != 0) {
            binding!!.layoutMonedas.removeViewAt(0)
        }
        val inflater = requireActivity().layoutInflater
        for (x in monedas.indices) {
            @SuppressLint("InflateParams") val view =
                inflater.inflate(R.layout.item_moneda_cantidad, null)
            @SuppressLint("CutPasteId") val moneda = view.findViewById<TextView>(R.id.moneda)
            @SuppressLint("CutPasteId") val cantidad = view.findViewById<TextView>(R.id.cantidad)
            moneda.text = monedas[x]
            cantidad.text =
                simbolos[x] + " " + Utils.formatoCantidad(cantidades[x])
            binding!!.layoutMonedas.addView(view)
        }
    }

    private fun scrollToTag() {
        binding!!.scrollview.post { binding!!.scrollview.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun verificarCantidad() {
        if (binding!!.editCantidad.text.toString() != "" && binding!!.editCantidad.text.toString() != "0") {
            if (binding!!.editCantidad.text.toString().startsWith("0")) {
                binding!!.editCantidad.setText(binding!!.editCantidad.text.toString().substring(1))
                binding!!.editCantidad.setSelection(binding!!.editCantidad.length())
            }
        }
    }

    private fun showDatePickerDialog() {
        val newFragment =
            DatePickerFragment.newInstance { datePicker: DatePicker?, year: Int, month: Int, day: Int ->
                val selectedDate =
                    year.toString() + "/" + Utils.twoDigits(month + 1) + "/" + Utils.twoDigits(day)
                val h = Calendar.getInstance()
                h[year, month] = day
                h.add(Calendar.DAY_OF_YEAR, 1)
                binding!!.editFecha.text = selectedDate
                Preferences.savePreferenceString(
                    requireContext(),
                    binding!!.editFecha.text.toString(),
                    "gasto_fecha"
                )
            }
        newFragment.show(
            Objects.requireNonNull(requireActivity()).supportFragmentManager,
            "datePicker"
        )
    }

    /*@Throws(ParseException::class)
    fun addData() {
        if (Utils.fechaMayorQueHoy(binding!!.editFecha.text.toString())) {
            Toast.makeText(requireContext(), R.string.error_fecha_futura, Toast.LENGTH_SHORT).show()
        } else {
            if (binding!!.editCantidad.text.toString() == "" || binding!!.editCantidad.text.toString() == "0") {
                Toast.makeText(requireContext(), R.string.error_cantidad_null, Toast.LENGTH_SHORT)
                    .show()
            } else {
                var ingreso = "0"
                if (binding!!.checkIngreso.isChecked) {
                    ingreso = "1"
                }
                val insertData = dataBase!!.addGastos(
                    binding!!.editFecha.text.toString(),
                    binding!!.spinnerMoneda.selectedItem.toString(),
                    binding!!.editCantidad.text.toString().toFloat(),
                    binding!!.editMotivo.text.toString(),
                    tags!!,
                    ingreso
                )
                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT)
                        .show()
                }
                cleanAndUpdate()
            }
        }
    }*/

    @SuppressLint("SetTextI18n")
    private fun cleanAndUpdate() {
        Preferences.deletePreferencesGastoPendiente(requireContext())
        binding!!.spinnerMoneda.setSelection(0)
        binding!!.editCantidad.setText("0")
        binding!!.editMotivo.setText("")
        binding!!.checkIngreso.isChecked = false
        binding!!.txtTagSeleccionados.text = resources.getString(R.string.sin_seleccionados)
        tags!!.clear()
        monedas = listOf()
        cantidades = listOf()
        //val data = dataBase!!.getMonedasByUserId(userViewModel.getId())
        /*while (data.moveToNext()) {
            monedas.add(data.getString(1))
            cantidades.add(data.getFloat(2))
            simbolos.add(data.getString(3))
        }*/
        llenarLayoutMonedas()
        binding!!.scrollview.fullScroll(ScrollView.FOCUS_UP)
    }
}
