package mis.finanzas.diarias.principal.fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.taskdone.R
import com.example.taskdone.databinding.FragmentPrincipalBinding
import mis.finanzas.diarias.ActivityMain
import java.util.*

class PrincipalFragment : Fragment() {
    private lateinit var binding: FragmentPrincipalBinding

    var tags: ArrayList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        (activity as ActivityMain).updateFragment(R.id.principalFragment)

        return binding.root
    }

}
