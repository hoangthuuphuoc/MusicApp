package com.example.musicapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(
        email: String, password: String
    ): Result<Unit> {

        return try {

            firebaseAuth.signInWithEmailAndPassword(
                email, password
            ).await()

            Result.success(Unit)

        } catch (exception: Exception) {

            Result.failure(exception)
        }
    }

    suspend fun register(
        fullName: String, email: String, password: String
    ): Result<Unit> {

        return try {

            val result = firebaseAuth.createUserWithEmailAndPassword(
                email, password
            ).await()

            val user = result.user

            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(
                fullName
            ).build()

            user?.updateProfile(
                profileUpdates
            )?.await()

            Result.success(Unit)

        } catch (exception: Exception) {

            Result.failure(exception)
        }
    }


}