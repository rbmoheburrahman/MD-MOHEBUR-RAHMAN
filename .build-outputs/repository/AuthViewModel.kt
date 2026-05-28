package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ServiceLocator
import com.example.data.database.UserAccount
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: UserAccount) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ServiceLocator.getRepository(application)

    val currentUser: StateFlow<UserAccount?> = repository.currentUserFlow

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    init {
        // Try auto seeding & check session
        viewModelScope.launch {
            repository.performStartupSeeding()
            // Auto-login a guest user so they can inspect immediately without registering, 
            // but can also register a real custom profile.
            repository.tryAutoLoginFromCache("benzol_team@noor.com")
        }
    }

    fun login(emailOrUsername: String, passwordPlain: String) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.loginUser(emailOrUsername, passwordPlain)
            result.onSuccess { user ->
                _authUiState.value = AuthUiState.Success(user)
            }
            result.onFailure { exception ->
                _authUiState.value = AuthUiState.Error(exception.message ?: "Authentication failed.")
            }
        }
    }

    fun signup(
        email: String,
        username: String,
        displayName: String,
        passwordPlain: String,
        avatarUrl: String,
        bio: String
    ) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.registerUser(
                email = email,
                username = username,
                displayName = displayName,
                passwordPlain = passwordPlain,
                avatarUrl = if (avatarUrl.trim().isEmpty()) {
                    // Generate cool looking premium placeholder avatar
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
                } else avatarUrl,
                bio = bio
            )
            result.onSuccess { user ->
                _authUiState.value = AuthUiState.Success(user)
            }
            result.onFailure { exception ->
                _authUiState.value = AuthUiState.Error(exception.message ?: "Registration failed.")
            }
        }
    }

    fun resetState() {
        _authUiState.value = AuthUiState.Idle
    }

    fun logout() {
        repository.logout()
        _authUiState.value = AuthUiState.Idle
    }
}
