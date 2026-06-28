package com.foxsteven.luminagallery.presentation.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.foxsteven.luminagallery.application.AuthenticationService
import com.foxsteven.luminagallery.infrastructure.security.BiometricManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val biometricManager: BiometricManager
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun authenticateBiometric(activity: FragmentActivity) {
        _errorMessage.value = null
        biometricManager.authenticateBiometric(
            activity = activity,
            onSuccess = {
                authenticationService.authorize()
            },
            onError = { error ->
                _errorMessage.value = error
            }
        )
    }

    fun authenticatePassword(activity: FragmentActivity) {
        _errorMessage.value = null
        biometricManager.authenticatePassword(
            activity = activity,
            onSuccess = {
                authenticationService.authorize()
            },
            onError = { error ->
                _errorMessage.value = error
            }
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
