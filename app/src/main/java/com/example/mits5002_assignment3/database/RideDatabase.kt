package com.example.mits5002_assignment3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mits5002_assignment3.data.model.Driver
import com.example.mits5002_assignment3.data.model.Passenger
import com.example.mits5002_assignment3.data.model.Ride
import com.example.mits5002_assignment3.data.model.User
import com.example.mits5002_assignment3.data.model.Vehicle
import com.example.mits5002_assignment3.data.model.dao.GenericDAO
import com.example.mits5002_assignment3.data.model.dao.UserDAO

@Database(
    entities = [
        User::class, Driver::class, Passenger::class, Ride::class, Vehicle::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RideDatabase: RoomDatabase() {
    abstract val userDao: UserDAO
    abstract val genericDao: GenericDAO

    companion object {
        private const val databaseName = "db"

        @Volatile
        private var dbInstance: RideDatabase? = null

        @Synchronized
        fun getInstance(context: Context): RideDatabase {
            synchronized(this) {
                if(dbInstance == null){
                    val builder = Room.databaseBuilder(
                        context.applicationContext,
                        RideDatabase::class.java,
                        databaseName
                    )

                    dbInstance = builder.allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }

                return dbInstance as RideDatabase
            }

        }
    }
}