package com.animegatari.hayanime.di

import android.content.Context
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.data.local.datastore.TokenDataStore
import com.animegatari.hayanime.data.remote.AuthInterceptor
import com.animegatari.hayanime.data.remote.RetrofitClient
import com.animegatari.hayanime.data.remote.TokenAuthenticator
import com.animegatari.hayanime.data.remote.api.AuthApiService
import com.animegatari.hayanime.domain.repository.AuthRepository
import com.animegatari.hayanime.domain.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
//    @Provides
//    @Singleton
//    fun provideAuthInterceptor(tokenDataStore: TokenDataStore): AuthInterceptor {
//        return AuthInterceptor(tokenDataStore)
//    }
//
//    @Provides
//    @Singleton
//    fun provideTokenAuthenticator(
//        tokenDataStore: TokenDataStore,
//        authApiService: AuthApiService,
//    ): TokenAuthenticator {
//        return TokenAuthenticator(tokenDataStore, authApiService)
//    }
//
//    @Provides
//    @Singleton
//    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
//        HttpLoggingInterceptor().apply {
//            level = if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor.Level.BODY
//            } else {
//                HttpLoggingInterceptor.Level.NONE
//            }
//        }
//
//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        authInterceptor: AuthInterceptor,
//        tokenAuthenticator: TokenAuthenticator,
//        loggingInterceptor: HttpLoggingInterceptor,
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(authInterceptor)
//            .addInterceptor(loggingInterceptor)
//            .authenticator(tokenAuthenticator)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideAuthApiService(okHttpClient: OkHttpClient): AuthApiService {
//        return Retrofit.Builder()
//            .baseUrl(BuildConfig.BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(AuthApiService::class.java)
//    }
//TODO: fix error on ignored code
    @Provides
    @Singleton
    fun provideAuthApiService(): AuthApiService {
        return RetrofitClient.authApiService
    }

    @Provides
    @Singleton
    fun provideTokenDataStore(@ApplicationContext context: Context): TokenDataStore {
        return TokenDataStore(context)
    }
}