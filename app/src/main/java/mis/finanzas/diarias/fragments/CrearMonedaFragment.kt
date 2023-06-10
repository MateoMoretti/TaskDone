package mis.finanzas.diarias.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.R
import com.example.taskdone.databinding.FragmentCrearMonedaBinding
import mis.finanzas.diarias.model.Currency
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import java.util.*

class CrearMonedaFragment : Fragment() {
    private var binding: FragmentCrearMonedaBinding? = null
    private val databaseViewModel: DatabaseViewModel by viewModels{DatabaseViewmodelFactory(requireContext())}

    var monedas = ArrayList<String>()
    var cantidades = ArrayList<Float>()
    var simbolos = ArrayList<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrearMonedaBinding.inflate(inflater, container, false)

        binding!!.editCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verificarCantidad(binding!!.editCantidad)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding!!.volver.setOnClickListener { findNavController().navigate(R.id.action_crearMonedaFragment_to_principalFragment) }
        binding!!.buttonCrear.setOnClickListener { createNewCurrency() }
        //cargarMonedas()
        return binding!!.root
    }

    private fun createNewCurrency(){
        val c = Currency(binding!!.editMoneda.text.toString(),
            binding!!.editCantidad.text.toString().toFloat(),
            binding!!.editSimbolo.text.toString())
        databaseViewModel.addCurrency(c)
        findNavController().navigate(R.id.action_crearMonedaFragment_to_principalFragment)
    }

    private fun verificarCantidad(e: EditText) {
        if (e.text.toString() != "" && e.text.toString() != "0") {
            if (e.text.toString().startsWith("0")) {
                e.setText(e.text.toString().substring(1))
                e.setSelection(e.length())
            }
        }
    }

    /*fun crearMoneda() {
        val nombre_moneda_persistir = binding!!.editMoneda.text.toString()
        val cantidad_elegida = binding!!.editCantidad.text.toString()
        var simbolo_persistir = binding!!.editSimbolo.text.toString()
        if (nombre_moneda_persistir == "") {
            Toast.makeText(requireContext(), R.string.moneda_nombre_vacio, Toast.LENGTH_SHORT)
                .show()
        } else {
            val data = database!!.getMonedaByNombre(nombre_moneda_persistir)
            var existe = false
            while (data.moveToNext()) {
                existe = true
            }
            if (existe) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.error_moneda_existente),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var cantidad_persistir = 0
                if (cantidad_elegida != "") {
                    cantidad_persistir = cantidad_elegida.toInt()
                }
                if (simbolo_persistir == "") {
                    simbolo_persistir = resources.getString(R.string.example_simbolo)
                }
                val insertData = database!!.addMonedaCantidad(
                    nombre_moneda_persistir,
                    cantidad_persistir.toFloat(),
                    simbolo_persistir
                )
                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT)
                        .show()
                }
                if (monedas.size == 0) {
                    navController!!.navigate(R.id.principalFragment)
                } else {
                    binding!!.editMoneda.setText("")
                    binding!!.editSimbolo.setText("")
                    binding!!.editCantidad.setText("0")
                    cleanYCargar()
                }
            }
        }
    }

    fun cargarMonedas() {
        val data = database!!.getMonedasByUserId(userViewModel.getId())
        while (data.moveToNext()) {
            monedas.add(data.getString(1))
            cantidades.add(data.getFloat(2))
            simbolos.add(data.getString(3))
        }
        llenarLayoutMonedas()
    }*/
/*
    @SuppressLint("SetTextI18n")
    private fun llenarLayoutMonedas() {
        while (binding!!.layoutMonedas.childCount != 0) {
            binding!!.layoutMonedas.removeViewAt(0)
        }
        val inflater = requireActivity().layoutInflater
        if (monedas.isEmpty()) {
            binding!!.volver.visibility = View.INVISIBLE
            binding!!.subtitulo.visibility = View.GONE
            binding!!.layoutMonedas.visibility = View.GONE
        } else {
            binding!!.volver.visibility = View.VISIBLE
            binding!!.subtitulo.visibility = View.VISIBLE
            binding!!.layoutMonedas.visibility = View.VISIBLE
            Preferences.savePreferenceString(
                requireContext(),
                fragment_anterior,
                "id_fragment_anterior"
            )
            for (x in monedas.indices) {
                @SuppressLint("InflateParams") val view =
                    inflater.inflate(R.layout.item_moneda_cantidad_editable, null)
                @SuppressLint("CutPasteId") val editar = view.findViewById<Button>(R.id.editar)
                @SuppressLint("CutPasteId") val moneda = view.findViewById<TextView>(R.id.moneda)
                @SuppressLint("CutPasteId") val simbolo = view.findViewById<TextView>(R.id.simbolo)
                editar.setOnClickListener { v: View? ->
                    popupEditarMoneda(
                        monedas[x],
                        simbolo.text.toString()
                    )
                }
                moneda.text = monedas[x]
                simbolo.text = simbolos[x]
                binding!!.layoutMonedas.addView(view)
            }
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun popupEditarMoneda(nombre: String, simbolo: String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val vista =
            inflater.inflate(R.layout.popup_editar_moneda, null)
        val edit_nombre = vista.findViewById<EditText>(R.id.edit_nombre)
        val edit_simbolo = vista.findViewById<EditText>(R.id.edit_simbolo)
        edit_nombre.setText(nombre)
        edit_simbolo.setText(simbolo)
        builder.setTitle(resources.getString(R.string.editar_moneda))
        builder.setView(vista)
            .setPositiveButton(
                resources.getString(R.string.aceptar)
            ) { dialog: DialogInterface?, which: Int ->
                editarMoneda(
                    nombre,
                    edit_nombre.text.toString(),
                    edit_simbolo.text.toString()
                )
            }
        builder.setNegativeButton(resources.getString(R.string.cancelar), null)
        builder.setCancelable(true)
        val dialog: Dialog = builder.create()
        val window = dialog.window
        window?.setGravity(Gravity.CENTER or Gravity.RIGHT)

        //Envio el dialog para cerrarlo si borra la moneda
        val borrar = vista.findViewById<ImageView>(R.id.borrar)
        borrar.setOnClickListener { v: View? -> popupBorrarMoneda(nombre, dialog) }
        dialog.show()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }*/

    /*private fun editarMoneda(nombre_viejo: String, nombre: String, simbolo: String) {
        if (nombre == "") {
            Toast.makeText(requireContext(), R.string.moneda_nombre_vacio, Toast.LENGTH_SHORT)
                .show()
        } else {
            if (nombre_viejo != nombre) {
                val data = database!!.getMonedaByNombre(nombre)
                var existe = false
                while (data.moveToNext()) {
                    existe = true
                }
                if (existe) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.error_moneda_existente),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val insertData = database!!.updateMoneda(nombre_viejo, nombre, simbolo)
                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT)
                        .show()
                }
                cleanYCargar()
            }
        }
    }*/

    /*private fun popupBorrarMoneda(nombre: String, dialog_viejo: Dialog) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.eliminar))
        builder.setMessage(resources.getString(R.string.estas_seguro_monedas))
        val c = DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int ->
            borrarMoneda(
                nombre,
                dialog_viejo
            )
        }
        builder.setPositiveButton(resources.getString(R.string.si), c)
        builder.setNegativeButton(resources.getString(R.string.no), null)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }*/

    /*private fun borrarMoneda(nombre: String, dialog_viejo: Dialog) {
        val result = database!!.deleteMonedaByNombre(nombre)
        if (result) {
            dialog_viejo.dismiss()
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.eliminado_exito),
                Toast.LENGTH_SHORT
            ).show()
            cleanYCargar()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.error_eliminado),
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/

    private fun cleanYCargar() {
        monedas.clear()
        simbolos.clear()
        cantidades.clear()
        //cargarMonedas()
    }
}