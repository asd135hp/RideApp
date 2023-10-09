package com.example.mits5002_assignment3.data.model

import com.example.mits5002_assignment3.data.model.common.UserType

open abstract class User(
    protected val userId: Int,
    protected val userName: String,
    protected val password: String,
    protected val userType: UserType,
    protected val emailAddress: String
) {
    var firstName: String = ""
    var lastName: String = ""
    var dob: String = ""
}