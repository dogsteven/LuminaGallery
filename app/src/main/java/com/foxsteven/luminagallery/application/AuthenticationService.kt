package com.foxsteven.luminagallery.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationService @Inject constructor() {
    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized = _isAuthorized.asStateFlow()

    private var backgroundTimestamp: Long = 0
    private val gracePeriodMillis = 2 * 60 * 1000L // 2 minutes

    fun authorize() {
        _isAuthorized.value = true
    }

    fun lock() {
        _isAuthorized.value = false
    }

    fun onAppBackgrounded(currentTime: Long) {
        backgroundTimestamp = currentTime
    }

    fun onAppForegrounded(currentTime: Long) {
        if (_isAuthorized.value && backgroundTimestamp != 0L) {
            val durationInBackground = currentTime - backgroundTimestamp
            if (durationInBackground > gracePeriodMillis) {
                _isAuthorized.value = false
            }
        }
        backgroundTimestamp = 0
    }
}
