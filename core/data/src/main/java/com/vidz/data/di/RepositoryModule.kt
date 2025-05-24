package com.vidz.data.di

import com.vidz.data.repository.AuthRepositoryImpl
import com.vidz.data.repository.BlindBoxRepositoryImpl
import com.vidz.data.repository.NotificationRepositoryImpl
import com.vidz.data.repository.OrderRepositoryImpl
import com.vidz.data.repository.SkuRepositoryImpl
import com.vidz.data.repository.AppConfigRepositoryImpl
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.BlindBoxRepository
import com.vidz.domain.repository.NotificationRepository
import com.vidz.domain.repository.OrderRepository
import com.vidz.domain.repository.SkuRepository
import com.vidz.domain.repository.AppConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindSkuRepository(
        skuRepositoryImpl: SkuRepositoryImpl
    ): SkuRepository

    @Binds
    @Singleton
    abstract fun bindAppConfigRepository(
        appConfigRepositoryImpl: AppConfigRepositoryImpl
    ): AppConfigRepository

    @Binds
    @Singleton
    abstract fun bindBlindBoxRepository(
        blindBoxRepositoryImpl: BlindBoxRepositoryImpl
    ): BlindBoxRepository
}

