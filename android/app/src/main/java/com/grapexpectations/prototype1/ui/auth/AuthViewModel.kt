package com.grapexpectations.prototype1.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.grapexpectations.prototype1.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _user = MutableStateFlow<FirebaseUser?>(repository.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess {
                _user.value = it
                _error.value = null
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            val result = repository.signUp(email, pass)
            result.onSuccess {
                _user.value = it
                _error.value = null
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun logout() {
        repository.logout()
        _user.value = null
    }
}
