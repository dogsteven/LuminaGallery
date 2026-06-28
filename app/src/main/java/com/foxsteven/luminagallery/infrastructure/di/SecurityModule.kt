package com.foxsteven.luminagallery.infrastructure.di

import com.foxsteven.luminagallery.infrastructure.security.BiometricManager
import com.foxsteven.luminagallery.infrastructure.security.BiometricManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindBiometricManager(
        biometricManagerImpl: BiometricManagerImpl
    ): BiometricManager
}
