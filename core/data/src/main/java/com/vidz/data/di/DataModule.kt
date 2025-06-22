package com.vidz.data.di

import com.vidz.data.repository.MessageRepositoryImpl
import com.vidz.domain.repository.MessageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageDataModule {

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository
} 