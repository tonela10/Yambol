package com.sedilant.yambol.data

import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authUI: AuthUI
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun isUserSignedIn(): Boolean = currentUser != null

    fun createSignInIntent(providers: List<AuthUI.IdpConfig>): Intent {
        return authUI.createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    suspend fun signOut(activity: Activity): Result<Unit> {
        return try {
            authUI.signOut(activity).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(activity: Activity): Result<Unit> {
        return try {
            authUI.delete(activity).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserEmail(): String? = currentUser?.email

    fun getUserDisplayName(): String? = currentUser?.displayName

    fun getUserPhotoUrl(): String? = currentUser?.photoUrl?.toString()
}