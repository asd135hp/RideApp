package com.example.mits5002_assignment3

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.math.PI
import kotlin.math.cos
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

        getLastLocation()
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
                            val l = GeoLocation(
                                innerLocation.latitude,
                                innerLocation.longitude
                            )

                            fragment?.loc = l
                            populateDrivers(l)  // start inserting drivers to the database
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
    private fun populateDrivers(currentLocation: GeoLocation){
        if(userDao.getDrivers().isNotEmpty()) return

        // generate about 15 drivers
        (1..15).forEach { driverId ->
            // https://gis.stackexchange.com/questions/25877/generating-random-locations-nearby
            // generate random driver with random location within 100 meters
            val u = Random.nextDouble()
            val v = Random.nextDouble()
            val radius = 100
            val w = radius * sqrt(u)
            val t = 2 * PI * v
            val x = w * cos(t)  // generate random x0
            val y = w * sin(t)  // generate random y0

            userDao.addUser(User(
                driverId,
                getRandomString(10),
                getRandomString(15),
                getRandomString(8) + "@google.com"
            ).apply {
                firstName = firstNames.random()
                lastName = lastNames.random()
            })

            userDao.addDriver(
                Driver(driverId).apply {
                    // set necessary info for demonstration purposes
                    vehicleId = driverId
                    // location of the driver near the current location, within 100 meters
                    latitude = currentLocation.lat + x
                    longitude = currentLocation.long + y
                }
            )

            // add vehicle to this randomly generated driver
            genericDao.add(Vehicle(driverId).apply {
                // only set up necessary info about the car
                val carNames = cars.random().split(" ")
                val plateNumber = getRandomString(6)
                model = carNames[1]
                brand = carNames[0]

                // abc-xyz plate number in aus??
                registrationNumber = plateNumber.slice(0..2) + '-' + plateNumber.slice(3..5)
            })
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