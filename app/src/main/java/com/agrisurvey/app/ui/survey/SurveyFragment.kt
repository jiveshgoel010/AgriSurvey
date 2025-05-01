package com.agrisurvey.app.ui.survey

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.agrisurvey.app.BuildConfig
import com.agrisurvey.app.MainActivity
import com.agrisurvey.app.R
import com.agrisurvey.app.data.model.Survey
import com.agrisurvey.app.databinding.FragmentSurveyBinding
import com.agrisurvey.app.utils.KeyboardUtil
import com.agrisurvey.app.utils.LocationUtil
import com.agrisurvey.app.viewmodel.SurveyViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import java.util.Locale


@AndroidEntryPoint
class SurveyFragment : Fragment() {

    private var _binding: FragmentSurveyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SurveyViewModel by viewModels()
    private lateinit var mapView: MapView
    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String? = null
    private var googleMap: GoogleMap? = null
    private val googleMapsApiKey = BuildConfig.GOOGLE_MAPS_API_KEY

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value == true }
        if (granted) {
            fetchCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location Permission is required", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val autocompleteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(result.data!!)
                selectedLatLng = place.latLng
                selectedAddress = place.address
                binding.editLocation.setText(place.address)

                // Update map marker
                if (place.latLng != null) {
                    mapView.visibility = View.VISIBLE
                    googleMap?.clear()
                    googleMap?.addMarker(
                        MarkerOptions().position(place.latLng!!).title("Selected Location")
                    )
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 15f))

                    getAddressFromLatLng(requireContext(), place.latLng!!) { address, state ->
                        selectedAddress = address
                        binding.editLocation.setText(address)
                        state?.let {
                            binding.dropdownState.setText(it, false)
                        }
                    }
                }

            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // User canceled the operation → Do nothing
            } else {
                // Some actual error occurred
                val status = Autocomplete.getStatusFromIntent(result.data!!)
                Toast.makeText(
                    requireContext(),
                    "Error: ${status.statusMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Optionally hide keyboard when user clicks anywhere outside
        KeyboardUtil.setupKeyboardHideOnTouch(binding.root, requireContext())

        val irrigationTypes = resources.getStringArray(R.array.irrigation_types)
        val seasons = resources.getStringArray(R.array.seasons)
        val cropTypes = resources.getStringArray(R.array.crop_types)
        val croppingPatterns = resources.getStringArray(R.array.cropping_patterns)
        val states = resources.getStringArray(R.array.indian_states)

        Log.d("GoogleMaps", "API Key: $googleMapsApiKey")


        binding.dropdownIrrigation.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                irrigationTypes
            )
        )
        binding.dropdownSeason.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                seasons
            )
        )
        binding.dropdownCropType.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                cropTypes
            )
        )
        binding.dropdownCroppingPattern.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                croppingPatterns
            )
        )
        binding.dropdownState.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                states
            )
        )

        LocationUtil.initialize(requireContext())
        Places.initialize(requireContext(), googleMapsApiKey)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap?.uiSettings?.isZoomControlsEnabled = true

            val indiaLatLng = LatLng(20.5937, 78.9629) // Center of India
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaLatLng, 4f))
            mapView.visibility = View.VISIBLE

            Log.d("MapRender", "Map created")

            // Add error handling/logging
            googleMap?.setOnMapLoadedCallback {
                Log.d("MapRender", "Map loaded successfully")
            }

            googleMap?.setOnMapClickListener {
                Log.d("MapRender", "Map clicked at: $it")
            }
        }


        binding.editLocation.apply {
            inputType = InputType.TYPE_NULL
            isFocusable = false
            setOnClickListener {
                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setCountries(listOf("IN")) // optional: restrict to India
                    .build(requireContext())
                autocompleteLauncher.launch(intent)
            }
        }

        binding.btnCurrentLocation.setOnClickListener {
            if (hasLocationPermission()) {
                fetchCurrentLocation()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }


        binding.btnSubmit.setOnClickListener {
            val cropsGrownText = binding.editCropsGrown.text.toString().trim()
            val cropsList = cropsGrownText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val name = binding.editName.text.toString().trim()
            val phoneNumber = binding.editPhone.text.toString().trim()
            val landSize = binding.editLandSize.text.toString().trim()
            val irrigationType = binding.dropdownIrrigation.text.toString().trim()
            val season = binding.dropdownSeason.text.toString().trim()
            val cropType = binding.dropdownCropType.text.toString().trim()
            val croppingPattern = binding.dropdownCroppingPattern.text.toString().trim()
            val location = binding.editLocation.text.toString().trim()
            val state = binding.dropdownState.text.toString().trim()

            // Validation checks
            if (name.isEmpty() || phoneNumber.isEmpty() || landSize.isEmpty() || state.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill all required fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid phone number",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!isValidLandSize(landSize)) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid land size",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (cropsList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter at least one crop",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

//            val survey = Survey(
//                userId = "", // will be overridden in repo
//                name = name,
//                phoneNumber = phoneNumber,
//                landSize = landSize,
//                irrigationType = irrigationType,
//                season = season,
//                cropType = cropType,
//                croppingPattern = croppingPattern,
//                location = state,
//                cropsGrown = cropsList // Pass the valid crops list
//            )

            Log.d("SurveySubmission", "State: ${binding.dropdownState.text}")

            val survey = Survey(
                userId = "", // will be overridden in repo
                name = name,
                phoneNumber = phoneNumber,
                landSize = landSize,
                irrigationType = irrigationType,
                season = season,
                cropType = cropType,
                croppingPattern = croppingPattern,
                location = location,
                state = state,
                cropsGrown = cropsList,
                latitude = selectedLatLng?.latitude,   // Add latitude
                longitude = selectedLatLng?.longitude // Add longitude
            )


            viewModel.submitSurvey(survey)
        }

        viewModel.submissionSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Survey submitted successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                clearForm()
            } else {
                Toast.makeText(requireContext(), "Failed to submit survey.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = "^[+]?[0-9]{10,13}$"
        return phone.matches(Regex(phonePattern))
    }

    private fun isValidLandSize(landSize: String): Boolean {
        return try {
            val landSizeValue = landSize.toDouble()
            landSizeValue > 0
        } catch (e: NumberFormatException) {
            false  // Invalid number format
        }
    }

    private fun hasLocationPermission(): Boolean {
        val context = requireContext()
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun fetchCurrentLocation() {
        LocationUtil.getCurrentLocation(
            context = requireContext(),
            activity = requireActivity(),
            onLocationFetched = { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                selectedLatLng = latLng
                getAddressFromLatLng(requireContext(), latLng) { address, state ->
                    selectedAddress = address
                    binding.editLocation.setText(address)
                    mapView.visibility = View.VISIBLE
                    googleMap?.clear()
                    googleMap?.addMarker(
                        MarkerOptions().position(latLng).title("Your Location")
                    )
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    state?.let {
                        binding.dropdownState.setText(it, false)
                    }
                }
            },
            onError = { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun getAddressFromLatLng(
        context: Context,
        latLng: LatLng,
        callback: (String, String?) -> Unit
    ) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val fullAddress = addresses?.firstOrNull()
            val addressLine = fullAddress?.getAddressLine(0) ?: "Unknown location"
            val stateName = fullAddress?.adminArea // Extracts the state
            callback(addressLine, stateName)
        } catch (e: Exception) {
            callback("Geocoder error: ${e.localizedMessage}", null)
        }
    }

    private fun getLatLngFromAddress(address: String, callback: (LatLng) -> Unit) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 1)
            val latLng = if (addresses?.isNotEmpty() == true) {
                LatLng(addresses[0].latitude, addresses[0].longitude)
            } else {
                LatLng(0.0, 0.0) // Fallback to some default location
            }
            callback(latLng)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error fetching LatLng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMapLocation(latLng: LatLng) {
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun clearForm() {
        binding.editName.text?.clear()
        binding.editPhone.text?.clear()
        binding.editLandSize.text?.clear()
        binding.dropdownIrrigation.setText("")
        binding.dropdownSeason.setText("")
        binding.dropdownCropType.setText("")
        binding.editCropsGrown.text?.clear()
        binding.dropdownCroppingPattern.setText("")
        binding.editLocation.text?.clear()
        binding.dropdownState.setText("")
        mapView.visibility = View.GONE
        googleMap?.clear()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}