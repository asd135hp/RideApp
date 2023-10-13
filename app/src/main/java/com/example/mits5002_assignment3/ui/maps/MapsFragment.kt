package com.example.mits5002_assignment3.ui.maps

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mits5002_assignment3.R
import com.example.mits5002_assignment3.data.model.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    // modifiable Latitude/Longitude
    var loc = GeoLocation(0.0, 0.0)
        set(value) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
            field = value
        }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Default comment for MapsFragment:
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val geoLocation = LatLng(loc.lat, loc.long)
        googleMap.apply {
            addMarker(MarkerOptions().position(geoLocation).title("Your location"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(geoLocation, 15.0f))
            setMinZoomPreference(10.0f)
            setMaxZoomPreference(18.0f)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set to sydney
        loc = GeoLocation(-34.0, 151.0)
    }
}