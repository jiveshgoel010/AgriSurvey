package com.agrisurvey.app.ui.insights

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider

@SuppressLint("ClickableViewAccessibility")
class HeatMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MapView(context, attrs), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var points: List<LatLng> = emptyList()

    var touchListener: MapTouchListener? = null

    init {
        // Detect user touch
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                touchListener?.onTouch()
            }
            false
        }
    }

    fun setSurveyPoints(surveys: List<com.agrisurvey.app.data.model.Survey>) {
        points = surveys.mapNotNull {
            it.latitude?.let { lat ->
                it.longitude?.let { lng ->
                    LatLng(
                        lat,
                        lng
                    )
                }
            }
        }
        getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true // Enable zoom controls
        map.uiSettings.isScrollGesturesEnabled = true // Enable touch scroll
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true

        if (points.isNotEmpty()) {
            val provider = HeatmapTileProvider.Builder().data(points).build()
            map.addTileOverlay(TileOverlayOptions().tileProvider(provider))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(points[0], 6f))
        }
    }
}