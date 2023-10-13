package com.example.mits5002_assignment3.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.mits5002_assignment3.data.model.dao.GenericDAO
import com.example.mits5002_assignment3.data.model.dao.UserDAO


@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("userId"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    )]
)
/**
 * A passenger class that has a relation to the user class
 *
 * Room (Android persistent database) is designed to have a high cohesive strategy
 * so a normal inheritance will be replaced with an id referenced to another table's id.
 * This makes the code a lot cleaner and easier to read
 *
 * In this case, the passenger object will have a userId, which references to
 * a user object retrievable from the database through DAOs. In a normal diagram, the passenger
 * class should inherit the user class.
 */
data class Passenger(@PrimaryKey var userId: Int) {
    @ColumnInfo var totalRides: Int = 0

    fun switchDriverMode(vehicle: Vehicle, userDao: UserDAO, dao: GenericDAO): Driver {
        val driver = Driver(userId).apply {
            vehicleId = vehicle.vehicleId
        }
        dao.add(vehicle)
        userDao.delete(this)
        userDao.addDriver(driver)
        return driver
    }

    fun rideRequest() {

    }

    fun trackRide() {

    }
}