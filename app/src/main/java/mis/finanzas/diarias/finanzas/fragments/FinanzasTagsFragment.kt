package mis.finanzas.diarias.finanzas.fragments

import android.os.Bundle
import com.example.taskdone.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskdone.databinding.FragmentTagsBinding
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.ActivityMain
import mis.finanzas.diarias.finanzas.model.Tag
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.finanzas.viewmodels.DatabaseViewmodelFactory
import mis.finanzas.diarias.finanzas.viewmodels.AddRecordViewModel
import mis.finanzas.diarias.finanzas.viewmodels.BaseRecordViewModel
import mis.finanzas.diarias.finanzas.viewmodels.EditRecordViewModel
import java.util.*

class FinanzasTagsFragment : Fragment() {
    private lateinit var binding: FragmentTagsBinding
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }
    private val addRecordViewModel: AddRecordViewModel by activityViewModels()
    private val editRecordViewModel: EditRecordViewModel by activityViewModels()

    private lateinit var currentTags: List<Tag>
    private var adapterTags: mis.finanzas.diarias.finanzas.components.ListaTagsAdapter? = null
    private lateinit var viewModel: BaseRecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTagsBinding.inflate(inflater, container, false)
        when((activity as ActivityMain).getFragmentId()){
            R.id.finanzasPrincipalFragment -> viewModel = addRecordViewModel
            R.id.finanzasHistorialFragment -> viewModel = editRecordViewModel
        }
        (activity as ActivityMain).updateFragment(R.id.finanzasTagsFragment)

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonAceptar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonEliminar.setOnClickListener { popupDeleteTags() }
        binding.buttonCrearTag.setOnClickListener { popupCrearTag() }
        binding.ayuda.setOnClickListener {
            Utils.popupAyuda(requireContext(), requireActivity(), ArrayList(
                    listOf(
                            resources.getString(R.string.ayuda_tags_1),
                            resources.getString(R.string.ayuda_tags_2)
                        )
                )
            )
        }

        reloadData()
        return binding.root
    }

    private fun reloadData() {
        currentTags = databaseViewModel.getAllTags()
        adapterTags =
            mis.finanzas.diarias.finanzas.components.ListaTagsAdapter(viewModel, currentTags, this)

        if (currentTags.isEmpty()) {
            binding.txtSinTags.visibility = View.VISIBLE
        } else {
            binding.txtSinTags.visibility = View.INVISIBLE
        }
        binding.recyclerTags.layoutManager = LinearLayoutManager(context)
        binding.recyclerTags.adapter = adapterTags
    }


    private fun popupCrearTag() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val popupView = inflater.inflate(R.layout.popup_crear_tag, null)
        val edtTag = popupView.findViewById<EditText>(R.id.edit_crear_tag)
        builder.setTitle(resources.getString(R.string.crear_nuevo_tag))
        builder.setView(popupView)
            .setPositiveButton(
                resources.getString(R.string.aceptar)
            ) { _: DialogInterface?, _: Int -> createTag(edtTag.text.toString()) }
        val dialog: Dialog = builder.create()
        val window = dialog.window
        window?.setGravity(Gravity.CENTER or Gravity.RIGHT)
        dialog.show()
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun createTag(tag: String) {
        if (tag == "") {
            Toast.makeText(requireContext(), R.string.tag_vacio, Toast.LENGTH_SHORT).show()
            return
        }
        // TAG EXISTENTE
        if (currentTags.map { it.nombre }.contains(tag)) {
            Toast.makeText(context, R.string.error_tag_existente, Toast.LENGTH_SHORT).show()
            return
        }
        // ÉXITO
        databaseViewModel.addTag(Tag(tag))
        reloadData()
    }



    fun popupEditTag(tag: Tag) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val vista = inflater.inflate(R.layout.popup_crear_tag, null)
        val editarTag = vista.findViewById<EditText>(R.id.edit_crear_tag)
        editarTag.setText(tag.nombre)
        builder.setTitle(resources.getString(R.string.editar_tag))
        builder.setView(vista)
            .setPositiveButton(
                resources.getString(R.string.aceptar)
            ) { _: DialogInterface?, _: Int ->
                editTag(
                    tag,
                    editarTag.text.toString()
                )
            }
        builder.setNegativeButton(resources.getString(R.string.cancelar), null)
        builder.setCancelable(true)
        val dialog: Dialog = builder.create()
        val window = dialog.window
        window?.setGravity(Gravity.CENTER or Gravity.RIGHT)
        dialog.show()
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun editTag(tag: Tag, newTag: String) {

        // TAG VACIO
        if (newTag == "") {
            Toast.makeText(context, R.string.tag_vacio, Toast.LENGTH_SHORT).show()
            return
        }
        // TAG EXISTENTE
        if (currentTags.map { it.nombre }.contains(newTag)) {
            Toast.makeText(context, R.string.error_tag_existente, Toast.LENGTH_SHORT).show()
            return
        }
        // ÉXITO
        tag.nombre = newTag
        databaseViewModel.addTag(tag)
        Toast.makeText(context, R.string.guardado_exito, Toast.LENGTH_SHORT).show()
    }


    private fun popupDeleteTags() {

        // NO HAY TAGS SELECCIONADOS
        if (viewModel.getSelectedTags().isEmpty()) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.sin_tags_seleccionados),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(resources.getString(R.string.eliminar))
            builder.setMessage(resources.getString(R.string.estas_seguro_tags))
            val c = DialogInterface.OnClickListener { _: DialogInterface?, _: Int -> deleteTags() }
            builder.setPositiveButton(resources.getString(R.string.si), c)
            builder.setNegativeButton(resources.getString(R.string.no), null)
            builder.setCancelable(true)
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun deleteTags() {
        for (tag in viewModel.getSelectedTags()) {
            databaseViewModel.deleteTag(tag)
        }
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.eliminado_exito),
            Toast.LENGTH_SHORT
        ).show()
        reloadData()
    }

}