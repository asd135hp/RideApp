package com.example.mits5002_assignment3.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
/**
 * Base user class with all information related to a user
 */
data class User(
    @PrimaryKey val userId: Int,
    @ColumnInfo val userName: String,
    @ColumnInfo val password: String,
    @ColumnInfo val emailAddress: String
) {
    @ColumnInfo var firstName: String = ""
    @ColumnInfo var lastName: String = ""
    @ColumnInfo var dob: String = ""
}