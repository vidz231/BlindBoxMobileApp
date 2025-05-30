package com.vidz.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vidz.datastore.TokenDataStore
import com.vidz.datastore.TokenManager
import com.vidz.datastore.TokenRepositoryImpl
import com.vidz.domain.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_preferences")

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        tokenRepositoryImpl: TokenRepositoryImpl
    ): TokenRepository


    companion object {
        @Provides
        @Singleton
        fun provideTokenDataStore(
            @ApplicationContext context: Context
        ): TokenDataStore {
            return TokenDataStore(context)
        }
    }
} 