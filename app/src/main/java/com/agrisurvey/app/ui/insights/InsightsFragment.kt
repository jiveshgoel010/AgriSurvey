package com.agrisurvey.app.ui.insights

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.agrisurvey.app.MainActivity
import com.agrisurvey.app.R
import com.agrisurvey.app.data.model.Survey
import com.agrisurvey.app.databinding.FragmentInsightsBinding
import com.agrisurvey.app.viewmodel.InsightsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    private val insightsViewModel: InsightsViewModel by viewModels()
    private var selectedState: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.heatMapView.onCreate(savedInstanceState)
        binding.heatMapView.onResume() // Needed to trigger map rendering
        binding.heatMapView.touchListener = object : MapTouchListener {
            override fun onTouch() {
                binding.insightsScrollView.requestDisallowInterceptTouchEvent(true)
            }
        }

        selectedState = arguments?.getString("state") ?: "Unknown"

        observeViewModel()

        selectedState?.let {
            insightsViewModel.fetchSurveysForState(it)
        }
    }

    private fun observeViewModel() {
        insightsViewModel.surveys.observe(viewLifecycleOwner, Observer { surveys ->
            if (surveys.isNotEmpty()) {
                binding.layoutChartsAndMap.isVisible = true
                binding.tvNoData.isVisible = false
                showSurveyInsights(surveys)
            } else {
                binding.layoutChartsAndMap.isVisible = false
                binding.tvNoData.isVisible = true
            }
        })

        insightsViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.isVisible = isLoading
        })

        insightsViewModel.error.observe(viewLifecycleOwner, Observer { errorMsg ->
            if (errorMsg != null) {
                binding.tvError.text = errorMsg
                binding.tvError.isVisible = true
            } else {
                binding.tvError.isVisible = false
            }
        })
    }

    private fun showSurveyInsights(surveys: List<Survey>) {
        InsightsChartHelper.setupCropTypeChart(binding.pieChartCropType, surveys)
        InsightsChartHelper.setupIrrigationTypeChart(binding.pieChartIrrigationType, surveys)
        InsightsChartHelper.setupLandSizeChart(binding.barChartLandSize, surveys)
        InsightsChartHelper.setupCroppingPatternChart(binding.barChartCroppingPattern, surveys)

        binding.heatMapView.setSurveyPoints(surveys)
    }

    override fun onResume() {
        super.onResume()
//        (requireActivity() as MainActivity).setAppbar("$selectedState", showBackButton = true)
        val keys = resources.getStringArray(R.array.state_keys)
        val displayNames = resources.getStringArray(R.array.indian_states)
        val index = keys.indexOf(selectedState)
        val displayName = if (index != -1) displayNames[index] else selectedState ?: "Insights"
        (requireActivity() as MainActivity).setAppbar(displayName, showBackButton = true)

        binding.heatMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.heatMapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.heatMapView.onDestroy()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        binding.heatMapView.onLowMemory()
    }

}