package com.sedilant.yambol.data.firebaseAuth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sealed class representing the possible results of authentication operations
 */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

/**
 * Repository interface for Firebase Authentication operations
 */
interface AuthRepository {
    val currentUser: FirebaseUser?
    fun getAuthStateFlow(): Flow<FirebaseUser?>
    suspend fun signInWithEmail(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signUpWithEmail(email: String, password: String): AuthResult<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser>
    suspend fun signOut(): AuthResult<Unit>
    suspend fun deleteAccount(): AuthResult<Unit>
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>
    suspend fun reloadUser(): AuthResult<Unit>
    suspend fun sendEmailVerification(): AuthResult<Unit>
    suspend fun checkEmailVerified(): AuthResult<Boolean>
}

/**
 * Implementation of AuthRepository using Firebase Authentication
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Returns a Flow that emits the current authentication state
     * Emits null when user is signed out, FirebaseUser when signed in
     */
    override fun getAuthStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }

        auth.addAuthStateListener(authStateListener)

        // Send current state immediately
        trySend(auth.currentUser)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * Sign in with email and password
     */
    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<FirebaseUser> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return AuthResult.Error(Exception("Email and password cannot be empty"))
            }

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                AuthResult.Success(user)
            } else {
                AuthResult.Error(Exception("Sign in failed: User is null"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Create a new account with email and password
     */
    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): AuthResult<FirebaseUser> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return AuthResult.Error(Exception("Email and password cannot be empty"))
            }

            if (password.length < 6) {
                return AuthResult.Error(Exception("Password must be at least 6 characters"))
            }

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                AuthResult.Success(user)
            } else {
                AuthResult.Error(Exception("Sign up failed: User is null"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Sign out the current user
     */
    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            auth.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Delete the current user account
     */
    override suspend fun deleteAccount(): AuthResult<Unit> {
        return try {
            val user = currentUser
            if (user != null) {
                user.delete().await()
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Send password reset email
     */
    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            if (email.isBlank()) {
                return AuthResult.Error(Exception("Email cannot be empty"))
            }

            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Reload the current user data
     */
    override suspend fun reloadUser(): AuthResult<Unit> {
        return try {
            val user = currentUser
            if (user != null) {
                user.reload().await()
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Sign in with Google using ID token
     */
    override suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                AuthResult.Success(user)
            } else {
                AuthResult.Error(Exception("Google sign in failed: User is null"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Send email verification to the current user
     */
    override suspend fun sendEmailVerification(): AuthResult<Unit> {
        return try {
            val user = currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Check if the current user's email is verified
     * Reloads user data first to get the latest verification status
     */
    override suspend fun checkEmailVerified(): AuthResult<Boolean> {
        return try {
            val user = currentUser
            if (user != null) {
                user.reload().await()
                AuthResult.Success(user.isEmailVerified)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }
}