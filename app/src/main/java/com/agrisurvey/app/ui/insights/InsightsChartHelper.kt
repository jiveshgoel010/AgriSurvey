package com.agrisurvey.app.ui.insights

import android.graphics.Color
import com.agrisurvey.app.data.model.Survey
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


object InsightsChartHelper {

    // Crop Type Pie Chart
    fun setupCropTypeChart(chart: PieChart, surveys: List<Survey>) {
        val cropTypeCount = surveys.groupingBy { it.cropType }.eachCount()

        val entries = cropTypeCount.map { PieEntry(it.value.toFloat(), it.key) }

        val dataSet = PieDataSet(entries, "Crop Types")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#FF5722")
        )

        val pieData = PieData(dataSet)
        chart.data = pieData
        chart.description.isEnabled = false
        chart.legend.orientation = Legend.LegendOrientation.VERTICAL
        chart.legend.isWordWrapEnabled = true
        chart.invalidate()
    }

    // Irrigation Type Pie Chart
    fun setupIrrigationTypeChart(chart: PieChart, surveys: List<Survey>) {
        val irrigationCount = surveys.groupingBy { it.irrigationType }.eachCount()
        val entries = irrigationCount.map { PieEntry(it.value.toFloat(), it.key ?: "Unknown") }

        val dataSet = PieDataSet(entries, "Irrigation Types")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData = PieData(dataSet)

        chart.data = pieData
        chart.description.isEnabled = false
        chart.legend.orientation = Legend.LegendOrientation.VERTICAL
        chart.legend.isWordWrapEnabled = true
        chart.invalidate()
    }

    // Land Size Distribution Bar Chart
    fun setupLandSizeChart(chart: BarChart, surveys: List<Survey>) {
        val sizeBuckets = mutableMapOf(
            "0-1 acre" to 0,
            "1-2 acres" to 0,
            "2-5 acres" to 0,
            "5+ acres" to 0
        )

        surveys.forEach { survey ->
//            val size = survey.landSize ?: 0.0
            val size = survey.landSize.toDouble() ?: 0.0

            when {
                size <= 1.0 -> sizeBuckets["0-1 acre"] = sizeBuckets["0-1 acre"]!! + 1
                size <= 2.0 -> sizeBuckets["1-2 acres"] = sizeBuckets["1-2 acres"]!! + 1
                size <= 5.0 -> sizeBuckets["2-5 acres"] = sizeBuckets["2-5 acres"]!! + 1
                else -> sizeBuckets["5+ acres"] = sizeBuckets["5+ acres"]!! + 1
            }
        }

        val entries = sizeBuckets.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Land Size Distribution")
        dataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        chart.data = barData
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(sizeBuckets.keys.toList())
        chart.xAxis.granularity = 1f
        chart.xAxis.setDrawLabels(true)
        chart.setFitBars(true)
        chart.description.isEnabled = false
        chart.invalidate()
    }

    // Cropping Pattern Chart (Horizontal Bar)
    fun setupCroppingPatternChart(chart: BarChart, surveys: List<Survey>) {
        val patternCount = surveys.groupingBy { it.croppingPattern }.eachCount()
        val entries = patternCount.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Cropping Patterns")
        dataSet.colors = ColorTemplate.PASTEL_COLORS.toList()
        val barData = BarData(dataSet)

        chart.data = barData
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(patternCount.keys.toList())
        chart.xAxis.granularity = 1f
        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.invalidate()
    }

}