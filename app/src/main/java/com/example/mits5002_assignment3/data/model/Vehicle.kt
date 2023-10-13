package com.example.mits5002_assignment3.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vehicle(@PrimaryKey var vehicleId: Int) {
    @ColumnInfo var model: String = ""
    @ColumnInfo var brand: String = ""
    @ColumnInfo var variant: String = ""
    @ColumnInfo var registrationNumber: String = ""
    @ColumnInfo var mileage: Int = 0
    @ColumnInfo var status: String = ""
}