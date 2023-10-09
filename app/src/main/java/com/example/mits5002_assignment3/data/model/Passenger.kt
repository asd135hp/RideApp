package com.example.mits5002_assignment3.data.model

import com.example.mits5002_assignment3.data.model.common.UserType

class Passenger(
    userId: Int,
    userName: String,
    password: String,
    emailAddress: String
) : User(userId, userName, password, UserType.Passenger, emailAddress) {
    private val totalRides: Int = 0

    fun switchDriverMode(): Driver {
        return Driver(userId, userName, password, emailAddress)
    }

    fun rideRequest() {

    }

    fun trackRide() {

    }
}