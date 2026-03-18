package com.sedilant.yambol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class YambolAppViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    /**
     * Flow that emits true if user is authenticated, false otherwise
     */
    val isAuthenticated: StateFlow<Boolean> = authRepository.getAuthStateFlow()
        .map { user -> user != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}

