package com.sedilant.yambol.data.firebaseAuth

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

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

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override fun getAuthStateFlow(): Flow<FirebaseUser?> = auth.authStateChanged

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AuthResult<FirebaseUser> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return AuthResult.Error(Exception("Email and password cannot be empty"))
            }
            val result = auth.signInWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) AuthResult.Success(user)
            else AuthResult.Error(Exception("Sign in failed: User is null"))
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

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
            val result = auth.createUserWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) AuthResult.Success(user)
            else AuthResult.Error(Exception("Sign up failed: User is null"))
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            auth.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun deleteAccount(): AuthResult<Unit> {
        return try {
            val user = currentUser
            if (user != null) {
                user.delete()
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            if (email.isBlank()) {
                return AuthResult.Error(Exception("Email cannot be empty"))
            }
            auth.sendPasswordResetEmail(email)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun reloadUser(): AuthResult<Unit> {
        return try {
            val user = currentUser
            if (user != null) {
                user.reload()
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, null)
            val result = auth.signInWithCredential(credential)
            val user = result.user
            if (user != null) AuthResult.Success(user)
            else AuthResult.Error(Exception("Google sign in failed: User is null"))
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun sendEmailVerification(): AuthResult<Unit> {
        return try {
            val user = currentUser
            if (user != null) {
                user.sendEmailVerification()
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun checkEmailVerified(): AuthResult<Boolean> {
        return try {
            val user = currentUser
            if (user != null) {
                user.reload()
                AuthResult.Success(user.isEmailVerified)
            } else {
                AuthResult.Error(Exception("No user is currently signed in"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }
}
