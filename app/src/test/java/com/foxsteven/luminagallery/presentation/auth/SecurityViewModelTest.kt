package com.foxsteven.luminagallery.presentation.auth

import androidx.fragment.app.FragmentActivity
import com.foxsteven.luminagallery.application.AuthenticationService
import com.foxsteven.luminagallery.infrastructure.security.BiometricManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SecurityViewModelTest {

    private lateinit var viewModel: SecurityViewModel
    private val authenticationService = mockk<AuthenticationService>(relaxed = true)
    private val biometricManager = mockk<BiometricManager>(relaxed = true)
    private val activity = mockk<FragmentActivity>(relaxed = true)

    @Before
    fun setup() {
        viewModel = SecurityViewModel(authenticationService, biometricManager)
    }

    @Test
    fun `authenticateBiometric calls manager and authorizes on success`() {
        every { 
            biometricManager.authenticateBiometric(any(), any(), any()) 
        } answers {
            secondArg<() -> Unit>().invoke()
        }

        viewModel.authenticateBiometric(activity)

        verify { biometricManager.authenticateBiometric(activity, any(), any()) }
        verify { authenticationService.authorize() }
    }

    @Test
    fun `authenticatePassword calls manager and authorizes on success`() {
        every { 
            biometricManager.authenticatePassword(any(), any(), any()) 
        } answers {
            secondArg<() -> Unit>().invoke()
        }

        viewModel.authenticatePassword(activity)

        verify { biometricManager.authenticatePassword(activity, any(), any()) }
        verify { authenticationService.authorize() }
    }

    @Test
    fun `authenticateBiometric updates error message on failure`() {
        val error = "Auth failed"
        every { 
            biometricManager.authenticateBiometric(any(), any(), any()) 
        } answers {
            thirdArg<(String) -> Unit>().invoke(error)
        }

        viewModel.authenticateBiometric(activity)

        assert(viewModel.errorMessage.value == error)
    }
}
