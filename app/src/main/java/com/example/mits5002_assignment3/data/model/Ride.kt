package com.example.mits5002_assignment3.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * A class representing the whole ride with VIT Ride app
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Driver::class,
            parentColumns = ["userId"],
            childColumns = ["driverId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Passenger::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class Ride(
    @PrimaryKey var rideId: Int,
    @ColumnInfo(index = true) var userId: Int,
    @ColumnInfo(index = true) var driverId: Int
) {
    @ColumnInfo var pickUpTime: Long = 0
    @ColumnInfo var pickUpLocation: String = ""
    @ColumnInfo var dropOffLocation: String = ""
    @ColumnInfo var paymentAmount: Int = 0
    @ColumnInfo var rideType: String = ""
    @ColumnInfo var status: Boolean = false

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
        rideType: String,
        context: Context
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