package com.example.mits5002_assignment3.data.model

data class Vehicle(val driverId: Int) {
    var vehicleId: Int = 0
    var model: String = ""
    var brand: String = ""
    var variant: String = ""
    var registrationNumber: String = ""
    var mileage: Int = 0
    var status: String = ""
}