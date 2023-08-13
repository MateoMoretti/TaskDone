package mis.finanzas.diarias.finanzas.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.R
import com.example.taskdone.databinding.FragmentCreateCurrencyBinding
import com.example.taskdone.databinding.ItemMonedaCantidadEditableBinding
import com.example.taskdone.databinding.PopupEditCurrencyBinding
import mis.finanzas.diarias.finanzas.ActivityFinanzas
import mis.finanzas.diarias.finanzas.model.Currency
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewmodelFactory

class CreateCurrencyFragment : Fragment() {
    private lateinit var binding: FragmentCreateCurrencyBinding
    private val databaseViewModel: DatabaseViewModel by activityViewModels {
        DatabaseViewmodelFactory(
            requireContext()
        )
    }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateCurrencyBinding.inflate(inflater, container, false)
        (activity as ActivityFinanzas).updateFragment(R.id.crearMonedaFragment)

        databaseViewModel.getAllCurrency()
        setListeners()
        setObservers()
        return binding.root
    }


    private fun setObservers(){
        databaseViewModel.currencies.observe(viewLifecycleOwner){
            loadCurrencies(it)
        }
    }

    private fun setListeners(){
        binding.editCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                formatAmount(binding.editCantidad)
            }

            override fun afterTextChanged(editable: Editable) {
                //
            }
        })
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonCrear.setOnClickListener { createNewCurrency() }
    }

    @SuppressLint("SetTextI18n")
    private fun loadCurrencies(currencies: List<Currency>) {
        binding.currencyLayout.removeAllViews()
        if (currencies.isEmpty()){
            binding.yourCurrencies.visibility = View.GONE
            binding.back.visibility = View.GONE
        }
        else{
            binding.yourCurrencies.visibility =  View.VISIBLE
            binding.back.visibility =  View.VISIBLE

        }

        for (c in currencies) {
            val view = ItemMonedaCantidadEditableBinding.inflate(
                layoutInflater,
                binding.currencyLayout,
                true
            )
            view.name.text = c.name
            view.symbol.text = c.symbol
            view.edit.setOnClickListener {
                popupEditCurrency(c)
            }
        }
    }

    private fun formatAmount(e: EditText) {
        e.text.toString().let {
            if (it != "" && it != "0" && it.startsWith("0")) {
                e.setText(it.substring(1))
                e.setSelection(e.length())
            }
        }
    }


    private fun createNewCurrency() {
        if (canCreateCurrency(binding.editMoneda.text.toString())) {
            val c = Currency(
                binding.editMoneda.text.toString(),
                binding.editCantidad.text.toString().toFloat(),
                binding.editSymbol.text.toString()
            )
            databaseViewModel.addCurrency(c)
            Toast.makeText(context, R.string.guardado_exito, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun canCreateCurrency(name:String): Boolean{
        if (blankCurrency(name)) return false
        if(currencyAlreadyExists(name)) return false
        return true
    }

    private fun canEditCurrency(name:String, currency:Currency): Boolean{
        if (blankCurrency(name)) return false
        if(currencyAlreadyExists(name, currency )) return false
        return true
    }
    private fun blankCurrency(name:String): Boolean{
        (name == "").let {
            if(it) Toast.makeText(context, R.string.moneda_nombre_vacio, Toast.LENGTH_SHORT).show()
            return it
        }
    }

    private fun currencyAlreadyExists(name:String, currency: Currency?=null): Boolean {
        var otherCurrencies = databaseViewModel.getAllCurrency()
        currency?.let { otherCurrencies = otherCurrencies.filter { c -> c.id != it.id } }
        if (name in otherCurrencies.map { c -> c.name }) {
            Toast.makeText(context, R.string.error_moneda_existente, Toast.LENGTH_SHORT).show()
            return true
        }
        return false

    }


    @SuppressLint("RtlHardcoded")
    private fun popupEditCurrency(currency: Currency) {
        val builder = AlertDialog.Builder(context)

        builder.setNegativeButton(R.string.cancelar, null)
        builder.setCancelable(true)

        val view = PopupEditCurrencyBinding.inflate(layoutInflater, null, false)

        view.editName.setText(currency.name)
        view.editSymbol.setText(currency.symbol)
        builder.setTitle(R.string.editar_moneda)
        builder.setView(view.root)
            .setPositiveButton(
                resources.getString(R.string.aceptar)
            ) { _: DialogInterface?, _: Int ->
                editCurrency(
                    currency,
                    view.editName.text.toString(),
                    view.editSymbol.text.toString()
                )
            }
        val dialog: Dialog = builder.create()
        //Envio el dialog para cerrarlo si borra la moneda
        view.delete.setOnClickListener { popupDeleteCurrency(currency, dialog) }

        val window = dialog.window
        window?.setGravity(Gravity.CENTER or Gravity.RIGHT)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        dialog.show()
    }

    private fun editCurrency(currency: Currency, newName: String, symbol: String) {
        if (canEditCurrency(newName, currency)) {
            currency.name = newName
            currency.symbol = symbol

            databaseViewModel.addCurrency(currency)
            Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT).show()
        }
    }


    private fun popupDeleteCurrency(currency: Currency, editDialog: Dialog) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.eliminar)
        builder.setMessage(R.string.estas_seguro_monedas)
        val c = DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
            databaseViewModel.deleteCurrency(currency)
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.eliminado_exito),
                Toast.LENGTH_SHORT
            ).show()
            editDialog.dismiss()
        }
        builder.setPositiveButton(resources.getString(R.string.si), c)
        builder.setNegativeButton(resources.getString(R.string.no), null)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }
}
