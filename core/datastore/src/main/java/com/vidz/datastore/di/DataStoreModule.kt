package com.vidz.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.vidz.datastore.BlindBoxDatabase
import com.vidz.datastore.CartDataStore
import com.vidz.datastore.TokenDataStore
import com.vidz.datastore.TokenManager
import com.vidz.datastore.TokenRepositoryImpl
import com.vidz.datastore.dao.CartDao
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

        @Provides
        @Singleton
        fun provideBlindBoxDatabase(
            @ApplicationContext context: Context
        ): BlindBoxDatabase {
            return Room.databaseBuilder(
                context,
                BlindBoxDatabase::class.java,
                BlindBoxDatabase.DATABASE_NAME
            ).build()
        }

        @Provides
        @Singleton
        fun provideCartDao(database: BlindBoxDatabase): CartDao {
            return database.cartDao()
        }

        @Provides
        @Singleton
        fun provideCartDataStore(cartDao: CartDao): CartDataStore {
            return CartDataStore(cartDao)
        }
    }
} 