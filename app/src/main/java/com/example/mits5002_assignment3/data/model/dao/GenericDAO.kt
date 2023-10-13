package com.example.mits5002_assignment3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mits5002_assignment3.data.model.Ride
import com.example.mits5002_assignment3.data.model.Vehicle

/**
 * CRUD methods for generic DAO for the rest of entities
 *
 * Recommended to make the methods be asynchronous
 * (https://stackoverflow.com/questions/65449001/using-room-database-in-multiple-fragments)
 */
@Dao
interface GenericDAO {
    @Query("SELECT * FROM Vehicle WHERE vehicleId=:vehicleId")
    fun getVehicle(vehicleId: Int): Vehicle

    @Query("SELECT * FROM Ride WHERE rideId=:rideId")
    fun getRide(rideId: Int): Ride

    @Insert
    fun add(vehicle: Vehicle)
    @Insert
    fun add(ride: Ride)

    @Delete
    fun delete(vehicle: Vehicle)
    @Delete
    fun delete(ride: Ride)

    @Update
    fun update(vehicle: Vehicle)
    @Update
    fun update(ride: Ride)

    @Query("DELETE FROM Vehicle")
    fun deleteAllVehicles()
}