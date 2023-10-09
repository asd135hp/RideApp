package com.example.mits5002_assignment3.data.model

/**
 * A class representing the whole ride with VIT Ride app
 */
class Ride(private val rideId: Int, private val userId: Int, private val driverId: Int) {
    var pickUpTime: Long = 0
    var pickUpLocation: String = ""
    var dropOffLocation: String = ""
    var paymentAmount: Int = 0
    var rideType: String = ""
    var status: Boolean = false
        private set

    /**
     * A method that starts the ride on the VIT Ride app
     * The method will initialize all required information for the whole ride
     * Then the app will change screen to riding screen for both driver and passenger (not yet implemented)
     */
    fun startRide(
        pickUpTime: Long,
        pickUpLocation: String,
        dropOffLocation: String,
        paymentAmount: Int,
        rideType: String
    ) {
        status = true
        this.pickUpTime = pickUpTime
        this.pickUpLocation = pickUpLocation
        this.dropOffLocation = dropOffLocation
        this.paymentAmount = paymentAmount
        this.rideType = rideType
    }

    /**
     * A method that stop the ride
     */
    fun stopRide() {
        status = false
    }

    fun statement() {

    }
}