package com.example.mits5002_assignment3.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.mits5002_assignment3.data.model.dao.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("userId"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["vehicleId"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )]
)
/**
 * A driver class that has a relation to the user class
 *
 * Room (Android persistent database) is designed to have a high cohesive strategy
 * so a normal inheritance will be replaced with an id referenced to another table's id.
 * This makes the code a lot cleaner and easier to read
 *
 * In this case, the driver object will have a userId, which references to
 * a user object retrievable from the database through DAOs. In a normal diagram, the driver
 * class should inherit the user class.
 */
data class Driver(@PrimaryKey var userId: Int) {
    @ColumnInfo(index = true) var vehicleId = 0
    @ColumnInfo var latitude = 0.0
    @ColumnInfo var longitude = 0.0
    @ColumnInfo var rating = 5.0
    @ColumnInfo var distance = 0.0

    fun switchPassengerMode(dao: UserDAO): Passenger {
        val passenger = Passenger(userId)
        runBlocking(Dispatchers.IO) {
            dao.delete(this@Driver)
            dao.addPassenger(passenger)
        }
        return passenger
    }
}