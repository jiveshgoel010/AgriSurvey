package com.agrisurvey.app.ui.home

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.agrisurvey.app.MainActivity
import com.agrisurvey.app.R
import com.agrisurvey.app.databinding.FragmentHomeBinding
import com.agrisurvey.app.ui.insights.InsightsNavigationFragment
import com.agrisurvey.app.ui.survey.SurveyFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var recyclerViewNews: RecyclerView
    private lateinit var textWelcomeDescription: TextView
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        recyclerViewNews = binding.recyclerViewNews
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = requireActivity() as MainActivity

        binding.btnCta.setOnClickListener {
            mainActivity.loadFragment(SurveyFragment())
            mainActivity.setAppbar("Survey", showBackButton = false)
            mainActivity.setBottomNavItem(R.id.menuSurvey)
        }

        val shortcuts = listOf(
            ActionShortcut("New Survey", R.drawable.baseline_insert_drive_file_24) {
                mainActivity.loadFragment(SurveyFragment())
                mainActivity.setAppbar("Survey", showBackButton = false)
                mainActivity.setBottomNavItem(R.id.menuSurvey)
            },
            ActionShortcut("View Insights", R.drawable.analysis) {
                mainActivity.loadFragment(InsightsNavigationFragment())
                mainActivity.setAppbar("Insights", showBackButton = false)
                mainActivity.setBottomNavItem(R.id.menuInsights)
            },
            ActionShortcut("Ask Gemini", R.drawable.round_home_24) {
                Toast.makeText(requireContext(), "Gemini Coming Soon!", Toast.LENGTH_SHORT).show()
            },
            ActionShortcut("View Map", R.drawable.baseline_insert_drive_file_24) {
                Toast.makeText(requireContext(), "Map Feature Coming Soon!", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        val gridLayout = binding.shortcutsGrid
        shortcuts.forEach { shortcut ->
            val card = layoutInflater.inflate(R.layout.item_action_shortcut_tile, gridLayout, false)
            val icon = card.findViewById<ImageView>(R.id.iconShortcut)
            val title = card.findViewById<TextView>(R.id.titleShortcut)

            icon.setImageResource(shortcut.iconResId)
            title.text = shortcut.title

            card.setOnClickListener { shortcut.action() }

            gridLayout.addView(card)
        }

        val newsList = listOf(
            "PM-KISAN 14th Installment released!",
            "Rain forecast this week in Punjab",
            "Soil Health Card campaign launched",
            "Crop insurance deadline extended",
            "Minimum Support Prices hiked for Rabi crops"
        )

        val newsAdapter = NewsCarouselAdapter(newsList)
        recyclerViewNews.adapter = newsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class ActionShortcut(
        val title: String,
        val iconResId: Int,
        val action: () -> Unit
    )
}