package com.vidz.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vidz.data.repository.AuthRepositoryImpl
import com.vidz.data.server.retrofit.AuthInterceptor
import com.vidz.data.server.retrofit.api.TokenRefreshApi
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    companion object {
        @Provides
        @Singleton
        @Named("AuthInterceptor")
        fun provideAuthInterceptor(
            tokenRepository: TokenRepository
        ): Interceptor = AuthInterceptor(tokenRepository)

        @Provides
        @Singleton
        fun provideTokenRefreshApi(
            loggingInterceptor: HttpLoggingInterceptor
        ): TokenRefreshApi {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            
            // Create a separate OkHttpClient without AuthInterceptor to avoid dependency cycle
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
                
            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("http://40.87.80.54:8080/api/v1/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(TokenRefreshApi::class.java)
        }
    }
} 