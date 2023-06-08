package mis.finanzas.diarias.fragments

import android.os.Bundle
import com.example.taskdone.R
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskdone.databinding.FragmentTagsBinding
import mis.finanzas.diarias.components.ListaTagsAdapter
import mis.finanzas.diarias.Preferences
import mis.finanzas.diarias.Utils
import mis.finanzas.diarias.model.Tag
import mis.finanzas.diarias.viewmodels.DatabaseViewModel
import mis.finanzas.diarias.viewmodels.DatabaseViewmodelFactory
import java.util.*

class TagsFragment : Fragment() {
    private lateinit var binding: FragmentTagsBinding
    private val databaseViewModel: DatabaseViewModel by viewModels{ DatabaseViewmodelFactory(requireContext()) }

    var tags_actual = ArrayList<String>()
    var adapter_tags: ListaTagsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTagsBinding.inflate(inflater, container, false)
        /*if (Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")
                .toInt() == R.id.principalFragment
        ) {
            val tags_concatenados = Preferences.getPreferenceString(requireContext(), "gasto_tags")
            if (tags_concatenados != "") {
                tags_actual.addAll(Arrays.asList(*tags_concatenados.split(", ").toTypedArray()))
            }
        } else if (Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")
                .toInt() == R.id.historialFragment
        ) {
            val tagsConcatenados =
                Preferences.getPreferenceString(requireContext(), "edicion_gasto_tags")
            if (tagsConcatenados != "") {
                tags_actual.addAll(Arrays.asList(*tagsConcatenados.split(", ").toTypedArray()))
            }
        }*/

        cargarTags()
        binding!!.volver.setOnClickListener { v: View? -> volverAPrincipal(false) }

        //Cambiar
        //binding!!.buttonEliminar.setOnClickListener { v: View? -> popupBorrarTags() }
        binding!!.buttonCrearTag.setOnClickListener { v: View? -> popupCrearTag() }
        binding!!.buttonAceptar.setOnClickListener { v: View? -> volverAPrincipal(true) }
        binding!!.ayuda.setOnClickListener { v: View? ->
            Utils.popupAyuda(
                requireContext(), requireActivity(), ArrayList(
                    Arrays
                        .asList(
                            resources.getString(R.string.ayuda_tags_1),
                            resources.getString(R.string.ayuda_tags_2)
                        )
                )
            )
        }
        return binding!!.root
    }

    /*private fun popupBorrarTags() {
        if (tags_actual.isEmpty()) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.sin_tags_seleccionados),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(resources.getString(R.string.eliminar))
            builder.setMessage(resources.getString(R.string.estas_seguro_tags))
            val c =
                DialogInterface.OnClickListener { dialogInterface: DialogInterface?, i: Int -> deleteTag() }
            builder.setPositiveButton(resources.getString(R.string.si), c)
            builder.setNegativeButton(resources.getString(R.string.no), null)
            builder.setCancelable(true)
            val dialog = builder.create()
            dialog.show()
        }
    }*/

    /*private fun borrarTags() {
        var resultado = true
        for (s in tags_actual) {
            val result = database!!.deleteTagsByNombre(s)
            if (!result) {
                resultado = false
            }
        }
        if (resultado) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.eliminado_exito),
                Toast.LENGTH_SHORT
            ).show()
            if (Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")
                    .toInt() == R.id.principalFragment
            ) {
                Preferences.savePreferenceString(requireContext(), "", "gasto_tags")
            } else if (Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")
                    .toInt() == R.id.historialFragment
            ) {
                Preferences.savePreferenceString(requireContext(), "", "edicion_gasto_tags")
            }
            findNavController().navigate(R.id.tagsFragment)
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.error_eliminado),
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/

    private fun cargarTags() {
        val listTags = databaseViewModel.getAllTags()
        for (tag in listTags) {

        }
        /*val tags = database!!.getTagsByUserId(
            userViewModel.getId()
        ) //Reemplazar por getTagsByUsuarioId si permito más cuentas en un futuro
        val tags_nombre: MutableList<ItemTag> = ArrayList()
        while (tags.moveToNext()) {
            val i = ItemTag()
            i.nombre_tag = tags.getString(1)
            tags_nombre.add(i)
        }
        adapter_tags = ListaTagsAdapter(tags_nombre, tags_actual, requireContext(), this)
        if (tags_nombre.isEmpty()) {
            binding!!.txtSinTags.visibility = View.VISIBLE
        } else {
            binding!!.txtSinTags.visibility = View.INVISIBLE
        }
        binding!!.recyclerTags.layoutManager = LinearLayoutManager(context)
        binding!!.recyclerTags.adapter = adapter_tags*/
    }

    private fun volverAPrincipal(aceptar: Boolean) {
        val bundle = Bundle()
        if (aceptar) {
            if (Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")
                    .toInt() == R.id.principalFragment
            ) {
                Preferences.savePreferenceString(
                    requireContext(),
                    Utils.arrayListToString(tags_actual),
                    "gasto_tags"
                )
            } else if (Preferences.getPreferenceString(requireContext(), "id_fragment_anterior")
                    .toInt() == R.id.historialFragment
            ) {
                Preferences.savePreferenceString(
                    requireContext(),
                    Utils.arrayListToString(tags_actual),
                    "edicion_gasto_tags"
                )
            }
        }
        bundle.putBoolean("scroll_tags", true)
        findNavController().navigate(
            Preferences.getPreferenceString(
                requireContext(),
                "id_fragment_anterior"
            ).toInt(), bundle
        )
    }

    @SuppressLint("RtlHardcoded")
    fun popupEditarTag(tag_viejo: String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val vista = inflater.inflate(R.layout.popup_crear_tag, null)
        val editar_tag = vista.findViewById<EditText>(R.id.edit_crear_tag)
        editar_tag.setText(tag_viejo)
        builder.setTitle(resources.getString(R.string.editar_tag))
        builder.setView(vista)
            .setPositiveButton(
                resources.getString(R.string.aceptar)
            ) { dialog: DialogInterface?, which: Int ->
                editTag(
                    tag_viejo,
                    editar_tag.text.toString()
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

    private fun editTag(oldTag: String, newTag: String){
        val tag = 2
    }

    /*private fun editarTag(tag_viejo: String, tag_nuevo: String) {
        if (tag_nuevo == "") {
            Toast.makeText(requireContext(), R.string.tag_vacio, Toast.LENGTH_SHORT).show()
        } else {
            val tag_existente = database!!.getTagByNombre(tag_nuevo)
            var tag_existe = false
            while (tag_existente.moveToNext()) {
                tag_existe = true
            }
            if (tag_existe) {
                Toast.makeText(requireContext(), R.string.error_tag_existente, Toast.LENGTH_SHORT)
                    .show()
            } else {
                val insertData = database!!.updateTag(tag_viejo, tag_nuevo)
                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT)
                        .show()
                }
                cargarTags()
            }
        }
    }*/

    @SuppressLint("RtlHardcoded")
    private fun popupCrearTag() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        @SuppressLint("InflateParams") val vista = inflater.inflate(R.layout.popup_crear_tag, null)
        val crear_tag = vista.findViewById<EditText>(R.id.edit_crear_tag)
        builder.setTitle(resources.getString(R.string.crear_nuevo_tag))
        builder.setView(vista)
            .setPositiveButton(
                resources.getString(R.string.aceptar)
            ) { dialog: DialogInterface?, which: Int -> crearTag(crear_tag.text.toString()) }
        val dialog: Dialog = builder.create()
        val window = dialog.window
        window?.setGravity(Gravity.CENTER or Gravity.RIGHT)
        dialog.show()
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun crearTag(tag: String) {
        val tag = Tag(tag)
        databaseViewModel.addTag(tag)
        cargarTags()


        /*if (tag == "") {
            Toast.makeText(requireContext(), R.string.tag_vacio, Toast.LENGTH_SHORT).show()
        } else {
            val tag_existente = database!!.getTagByNombre(tag)
            var tag_existe = false
            while (tag_existente.moveToNext()) {
                tag_existe = true
            }
            if (tag_existe) {
                Toast.makeText(requireContext(), R.string.error_tag_existente, Toast.LENGTH_SHORT)
                    .show()
            } else {
                val insertData = database!!.addTag(tag)
                if (insertData) {
                    Toast.makeText(requireContext(), R.string.guardado_exito, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), R.string.error_guardado, Toast.LENGTH_SHORT)
                        .show()
                }
                cargarTags()
            }
        }
    }*/
    }
}