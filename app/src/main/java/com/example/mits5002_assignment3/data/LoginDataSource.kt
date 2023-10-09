package com.example.mits5002_assignment3.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(username: String, password: String): Result<FirebaseUser> {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        try {
            val result = auth.signInWithEmailAndPassword(username, password).await()
            if (result.user != null) return Result.Success(result.user!!)
            return Result.Error(IOException("Email or password is incorrect"))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}