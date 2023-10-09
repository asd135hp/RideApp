package com.example.mits5002_assignment3.data.model

import com.example.mits5002_assignment3.data.model.common.UserType

class Driver(
    userId: Int,
    userName: String,
    password: String,
    emailAddress: String
) : User(userId, userName, password, UserType.Driver, emailAddress) {
    var vehicle: Vehicle = Vehicle(userId)
        private set

    fun setVehicle(vehicle: Vehicle) {
        this.vehicle = vehicle
    }

    fun switchPassengerMode(): Passenger {
        return Passenger(userId, userName, password, emailAddress)
    }
}