package com.example.mits5002_assignment3.ui.maps

import android.graphics.Color
import android.nfc.TagLostException
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mits5002_assignment3.R
import com.example.mits5002_assignment3.data.model.GeoLocation
import com.example.mits5002_assignment3.ui.drivers.DriverFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.apache.commons.text.StringEscapeUtils
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.math.log
import kotlin.math.round

class MapsFragment : Fragment() {
    private val viewModel: MapsViewModel by activityViewModels()
    private var previousMarker: Marker? = null
    private var previousPolyline: Polyline? = null

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

        val loc = viewModel.origin.value ?: GeoLocation(0.0, 0.0)
        val geoLocation = LatLng(loc.lat, loc.long)
        googleMap.apply {
            addMarker(MarkerOptions().position(geoLocation).title("Your location"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(geoLocation, 15.0f))
            setMinZoomPreference(1.0f)
            setMaxZoomPreference(18.0f)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // when the origin changes, apply changes to the map
        viewModel.origin.observe(viewLifecycleOwner) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }

        Log.i("abc", "xyz")

        viewModel.destination.observe(viewLifecycleOwner) { location ->
            Log.i("abc", "$location\n${viewModel.origin.value}")
            Volley.newRequestQueue(context).run {
                val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=${viewModel.origin.value}&" +
                        "destination=${location}&" +
                        "mode=driving&" +
                        "key=${com.example.mits5002_assignment3.BuildConfig.key}"
                val stringReq = StringRequest(
                    Request.Method.GET,
                    url,
                    { response ->
                        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

                        // parse json to JsonElement (kotlin json lib)
                        val json = Json.decodeFromString<JsonElement>(response)
                        val result = parseDirectionsJson(json).apply {
                            Log.i("abc", "$address, $distance, $time, $encodedPoints")
                        }

                        // due to stringindexoutofbounds exception, we will try to fix
                        // the issue
                        val decodedPoints = try {
                            PolyUtil.decode(result.encodedPoints)
                        } catch(e: Exception) {
                            Log.i("abc", e.message ?: "")
                            PolyUtil.decode("${result.encodedPoints}@")
                        }

                        val options = PolylineOptions().apply {
                            // draw on map with all decoded waypoints
                            // on thickness of 3 and color blue lines
                            width(5.0f)
                            color(Color.BLACK)
                            addAll(decodedPoints)
                            Log.i("abc", decodedPoints.toString())
                        }

                        // add all polylines from our created options
                        mapFragment?.getMapAsync { map ->
                            map.apply {
                                // remove previous marker and polyline
                                previousMarker?.remove()
                                previousPolyline?.remove()

                                // add another marker with a title of destination name and
                                // distance and time
                                previousMarker = addMarker(
                                    MarkerOptions()
                                        .position(location.toLatLng())
                                        .title("Destination")
                                        .snippet("${result.distance} in ${result.time}")
                                )

                                // add all polylines to draw to the map
                                previousPolyline = addPolyline(options)

                                // move camera to the route we just draw
                                moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    viewModel.origin.value?.toLatLng() ?: LatLng(0.0, 0.0),
                                    result.zoomLevel))

                                // now show available drivers to the user
                                showDriversList()
                            }
                        }
                    },
                    {
                        Toast.makeText(context, "Please try again later!", Toast.LENGTH_SHORT).show()
                    })

                add(stringReq)
            }
        }
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set to melbourne
        viewModel.setOrigin(-37.840935, 144.946457)
    }

    /**
     * Show all available drivers near the origin (user's location) within 100 meters
     * (populated by a function in the MainActivity).
     */
    private fun showDriversList(){
        parentFragmentManager.apply {
            val driverFragment = findFragmentById(R.id.fragmentContainerView2) as DriverFragment?
            if(driverFragment != null)
                beginTransaction().show(driverFragment).commit()
        }
    }

    /**
     * Parse result got from directions API. What we need to get from here are distance, time
     * and encoded points for display purposes
     */
    private fun parseDirectionsJson(json: JsonElement): DirectionResults {
        var distance = ""
        var time = ""
        var encodedPoints = ""
        var address = ""
        json.jsonObject["routes"]
            ?.jsonArray?.get(0)
            ?.jsonObject?.run {
                get("legs")
                    ?.jsonArray?.get(0)
                    ?.jsonObject?.run {
                        distance = get("distance")?.jsonObject?.get("text")?.toString() ?: ""
                        time = get("duration")?.jsonObject?.get("text")?.toString() ?: ""
                        address = get("end_address")?.toString() ?: ""
                    }

                // just json things where double escape becomes quadruple escape (for transferring data)
                // and getting string from the jsonObject adds a double quote at both ends
                // which are not needed
                encodedPoints = (get("overview_polyline")?.jsonObject?.get("points")?.toString() ?: "")

            }

        return object : DirectionResults {
            override val distance: String
                get() = distance.replace("\"", "")
            override val time: String
                get() = time.replace("\"", "")
            override val encodedPoints: String
                get() = encodedPoints
                    .replace("\\\\", "\\")
                    .replace("\"", "")
                    .also {
                        // found bugs from this line
                        Log.i("abc", StringEscapeUtils.escapeJson(encodedPoints))
                    }
            override val address: String
                get() = address.replace("\"", "")
            override val zoomLevel: Float
                get() {
                    // get numeric distance that is inversely proportional to the level of zoom
                    // in google maps (base ratio: level 13 zoom for 4.0km distance
                    val adjustedLevel = log(
                        distance.replace("\"", "").split(" ")[0].toDouble(),
                        4.0
                    ).toFloat() - 1

                    Log.i("abc", adjustedLevel.toString())
                    return 13.0f - round(adjustedLevel)
                }
        }
    }

    interface DirectionResults {
        val address: String
        val distance: String
        val zoomLevel: Float
        val time: String
        val encodedPoints: String
    }
}