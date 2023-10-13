package com.example.mits5002_assignment3

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.Parcel
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mits5002_assignment3.data.model.Driver
import com.example.mits5002_assignment3.data.model.GeoLocation
import com.example.mits5002_assignment3.data.model.User
import com.example.mits5002_assignment3.data.model.Vehicle
import com.example.mits5002_assignment3.data.model.dao.GenericDAO
import com.example.mits5002_assignment3.data.model.dao.UserDAO
import com.example.mits5002_assignment3.database.RideDatabase
import com.example.mits5002_assignment3.databinding.ActivityMainBinding
import com.example.mits5002_assignment3.ui.maps.MapsFragment
import com.example.mits5002_assignment3.ui.maps.MapsViewModel
import com.google.android.datatransport.BuildConfig
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.LocationRestriction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userDao: UserDAO
    private lateinit var genericDao: GenericDAO

    // for requesting permission
    private val permissionId = 40

    // maps view model
    private val mapsVM: MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val dbInstance = RideDatabase.getInstance(applicationContext)
        userDao = dbInstance.userDao
        genericDao = dbInstance.genericDao

        setContentView(binding.root)
        supportFragmentManager.apply {
            val fragment = findFragmentById(R.id.fragmentContainerView2)
            if(fragment != null)
                beginTransaction()
                    .hide(fragment)
                    .commit()
        }

        // get last location of the phone
        getLastLocation()

        // the drivers in the database is empty -> populate drivers since the code does not reach
        runBlocking(Dispatchers.IO) {
            if(userDao.getDrivers().isEmpty()) populateDrivers(GeoLocation(-37.840935, 144.946457))
        }


        // places API must be initialized beforehand or error will be thrown
        Places.initialize(applicationContext, com.example.mits5002_assignment3.BuildConfig.key)

        // from google android documentation
        // https://developers.google.com/maps/documentation/places/android-sdk/autocomplete#maps_places_autocomplete_support_fragment-kotlin
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.placesAutocomplete)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.run {
            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            setCountries("AU")   // only show results in australia, just for demonstration purpose only
        }

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("abc", place.latLng?.toString() ?: "um")
                place.latLng?.run {
                    // set destination which signal observers of the view model destination property
                    mapsVM.setDestination(GeoLocation(latitude, longitude))
                }
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })

    }


    /**
     * When user is prompted with permission request from this activity in requestPermissions() method,
     * this default even handler will be called and we will get the last location of the phone
     * during the app's lifecycle
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == permissionId)
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation()

    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            // get location on application resume state
            getLastLocation()
        }
    }

    /**
     * Get the last location of the phone while using the app.
     */
    @Suppress("MissingPermission")
    private fun getLastLocation(){
        fusedLocationClient.apply {
            // before getting user's location
            // we have to check for the permissions set in AndroidManifest
            if(!checkPermissions()){
                requestPermissions()
                return
            }

            // get maps fragment reference
            var fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as MapsFragment?
            lastLocation.addOnCompleteListener {
                val location = it.result

                // reapply fragment if it is null (not initialized somehow)
                if(fragment == null)
                    fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as MapsFragment?

                // if the last location is not there
                // then we will request a new one manually
                if(location == null && fragment != null) {
                    fusedLocationClient.requestLocationUpdates(
                        getLocationRequest(),
                        { innerLocation ->
                            // set fragment's location to set off its google maps functionalities
                            GeoLocation(
                                innerLocation.latitude,
                                innerLocation.longitude
                            ).let { l ->
                                mapsVM.setOrigin(l)
                                populateDrivers(l)  // start inserting drivers to the database
                            }
                        },
                        Looper.myLooper())

                    return@addOnCompleteListener
                }
            }
        }
    }

    /**
     * Get LocationRequest object from its builder class (from documentation)
     */
    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000)
            .apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL)
                setMaxUpdateAgeMillis(10000)
            }.build()
    }

    /**
     * From this activity, request access to location of the phone
     */
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, arrayOf<String>(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ), permissionId)
    }

    /**
     * Check for appropriate permissions before handling with location data
     */
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Populate drivers to the table who will surround the app's location
     * for demonstration purposes only
     */
    private fun populateDrivers(currentLocation: GeoLocation) {
        runBlocking(Dispatchers.IO) {
            // revert changes from previous runs
            userDao.deleteAllDrivers()
            userDao.deleteAllUsers()
            genericDao.deleteAllVehicles()

            // generate about 15 drivers
            (1..10).forEach { driverId ->
                // https://gis.stackexchange.com/questions/25877/generating-random-locations-nearby
                // generate random driver with random location within 100 meters
                val u = Random.nextDouble()
                val v = Random.nextDouble()
                val radius = 100
                val w = radius * sqrt(u)
                val t = 2 * PI * v
                val x = w * cos(t)  // generate random x0
                val y = w * sin(t)  // generate random y0

                // coroutine instead of normal execution
                userDao.addUser(User(
                    driverId,
                    getRandomString(10),
                    getRandomString(15),
                    getRandomString(8) + "@google.com"
                ).apply {
                    firstName = firstNames.random()
                    lastName = lastNames.random()
                })

                // add vehicle to this randomly generated driver
                genericDao.add(Vehicle(driverId).apply {
                    // only set up necessary info about the car
                    val carNames = cars.random().split(" ")
                    val plateNumber = getRandomString(6)
                    model = carNames[1]
                    brand = carNames[0]

                    // abc-xyz plate number in aus??
                    registrationNumber = plateNumber.slice(0..2) + '-' + plateNumber.slice(3..5)
                    registrationNumber = registrationNumber.uppercase()
                })

                userDao.addDriver(
                    Driver(driverId).apply {
                        // set necessary info for demonstration purposes
                        vehicleId = driverId
                        // rating from 4.5 to 5
                        rating = Random.nextDouble() * 0.5 + 4.5
                        rating = round(rating * 100) / 100
                        // location of the driver near the current location, within 100 meters
                        currentLocation.let {
                            latitude = it.lat + x
                            longitude = it.long + y

                            // calculate distance between user and driver
                            distance = Location("origin").apply {
                                latitude = it.lat
                                longitude = it.long
                            }.distanceTo(
                                Location("dest").apply {
                                    latitude = latitude
                                    longitude = longitude
                                }
                            ).toDouble()
                        }
                    }
                )

            }
        }
    }

    /**
     * Get a random string
     * https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin
     */
    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    companion object {
        private val firstNames = listOf("Robert", "Bernard", "Chris", "Floyd", "James", "Michael")
        private val lastNames = listOf("Morgan", "Zhang", "Mackay", "Wood", "Russell", "Morton", "Hughes")
        private val cars = listOf(
            "Toyota Corolla", "Toyota Camry", "Honda Civic",
            "Ford Focus", "Toyota RAV4", "Honda CR-V", "Subaru Legacy"
        )
    }
}