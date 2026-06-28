package com.foxsteven.luminagallery.application

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationServiceTest {

    private lateinit var authenticationService: AuthenticationService

    @Before
    fun setup() {
        authenticationService = AuthenticationService()
    }

    @Test
    fun `initial state is locked`() = runTest {
        assertFalse(authenticationService.isAuthorized.value)
    }

    @Test
    fun `authorize sets state to true`() = runTest {
        authenticationService.authorize()
        assertTrue(authenticationService.isAuthorized.value)
    }

    @Test
    fun `lock sets state to false`() = runTest {
        authenticationService.authorize()
        authenticationService.lock()
        assertFalse(authenticationService.isAuthorized.value)
    }

    @Test
    fun `backgrounding for less than 2 minutes keeps authorized state`() = runTest {
        authenticationService.authorize()
        
        val backgroundTime = 1000L
        authenticationService.onAppBackgrounded(backgroundTime)
        
        val foregroundTime = backgroundTime + 1 * 60 * 1000L // 1 minute
        authenticationService.onAppForegrounded(foregroundTime)
        
        assertTrue(authenticationService.isAuthorized.value)
    }

    @Test
    fun `backgrounding for more than 2 minutes locks the app`() = runTest {
        authenticationService.authorize()
        
        val backgroundTime = 1000L
        authenticationService.onAppBackgrounded(backgroundTime)
        
        val foregroundTime = backgroundTime + 2 * 60 * 1000L + 1L // 2 mins + 1ms
        authenticationService.onAppForegrounded(foregroundTime)
        
        assertFalse(authenticationService.isAuthorized.value)
    }

    @Test
    fun `foregrounding without backgrounding doesn't change state`() = runTest {
        authenticationService.authorize()
        authenticationService.onAppForegrounded(1000L)
        assertTrue(authenticationService.isAuthorized.value)
    }
}
