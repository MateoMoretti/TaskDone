package my.life.tracker.principal.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import my.life.tracker.ActivityMain
import my.life.tracker.R
import my.life.tracker.databinding.FragmentPrincipalBinding
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
