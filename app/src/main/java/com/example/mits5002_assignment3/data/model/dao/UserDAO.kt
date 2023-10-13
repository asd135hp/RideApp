package com.example.mits5002_assignment3.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mits5002_assignment3.data.model.Driver
import com.example.mits5002_assignment3.data.model.Passenger
import com.example.mits5002_assignment3.data.model.User

/**
 * Crucial User DAO where it will specifies ways for the Room database
 * to interact with the app using crucial entities related to User class
 *
 * Recommended to make the methods be asynchronous
 * (https://stackoverflow.com/questions/65449001/using-room-database-in-multiple-fragments)
 */
@Dao
interface UserDAO {
    @Query("SELECT * FROM Driver")
    fun getDrivers(): List<Driver>

    @Query("SELECT * FROM Driver WHERE Driver.userId=:userId LIMIT 1")
    fun getDriver(userId: Int): Driver

    @Query("SELECT * FROM Passenger WHERE Passenger.userId=:userId LIMIT 1")
    fun getPassenger(userId: Int): Passenger

    @Query("SELECT * FROM User WHERE userId=:userId LIMIT 1")
    fun getUser(userId: Int): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDriver(driver: Driver)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPassenger(passenger: Passenger)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: User)

    @Delete
    fun delete(user: User)
    @Delete
    fun delete(driver: Driver)
    @Delete
    fun delete(passenger: Passenger)

    @Update
    fun updateDriver(driver: Driver)
    @Update
    fun updatePassenger(passenger: Passenger)
    @Update
    fun updateUser(user: User)

    @Query("DELETE FROM Driver")
    fun deleteAllDrivers()
    @Query("DELETE FROM User")
    fun deleteAllUsers()
}