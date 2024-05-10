package my.life.tracker.comidas.fragments

import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import my.life.tracker.R
import my.life.tracker.databinding.FragmentComidasBinding
import my.life.tracker.ActivityMain
import my.life.tracker.finanzas.viewmodels.DatabaseViewModel
import my.life.tracker.finanzas.viewmodels.DatabaseViewmodelFactory
import my.life.tracker.finanzas.viewmodels.AddRecordViewModel
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class ComidasFragment : Fragment() {
    private lateinit var binding: FragmentComidasBinding
    private val databaseViewModel: DatabaseViewModel by activityViewModels{DatabaseViewmodelFactory(requireContext())}
    private val recordViewModel: AddRecordViewModel by activityViewModels()

    var tags: ArrayList<String>? = null

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComidasBinding.inflate(inflater, container, false)
        (activity as ActivityMain).updateFragment(R.id.comidasFragment)

        return binding.root
    }

}
