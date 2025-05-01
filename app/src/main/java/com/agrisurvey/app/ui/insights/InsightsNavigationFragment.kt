package com.agrisurvey.app.ui.insights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrisurvey.app.MainActivity
import com.agrisurvey.app.R
import com.agrisurvey.app.databinding.FragmentInsightsNavigationBinding

class InsightsNavigationFragment : Fragment() {

    private var _binding: FragmentInsightsNavigationBinding? = null
    private val binding get() = _binding!!
    private lateinit var stateDisplayNames: Array<String>
    private lateinit var stateKeys: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsNavigationBinding.inflate(inflater, container, false)

        stateDisplayNames = resources.getStringArray(R.array.indian_states)
        stateKeys = resources.getStringArray(R.array.state_keys)

        val adapter = StateCardAdapter(stateDisplayNames) { selectedDisplayName ->
            // Find the index of the selected display name to get the corresponding state key
            val index = stateDisplayNames.indexOf(selectedDisplayName)
            if (index != -1) {
                val selectedStateKey = stateKeys[index]
                navigateToInsightsFragment(selectedStateKey)
            }
        }

        binding.recyclerViewStates.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewStates.adapter = adapter

        return binding.root
    }

    private fun navigateToInsightsFragment(state: String) {
        val bundle = Bundle().apply {
            putString("state", state)
        }
        val insightsFragment = InsightsFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, insightsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.setAppbar(
            getString(R.string.title_insights),
            showBackButton = false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
