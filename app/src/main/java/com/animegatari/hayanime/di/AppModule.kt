package com.animegatari.hayanime.di

import android.content.Context
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.data.local.datastore.TokenDataStore
import com.animegatari.hayanime.data.remote.okhttp.AuthInterceptor
import com.animegatari.hayanime.data.remote.okhttp.TokenAuthenticator
import com.animegatari.hayanime.data.remote.api.AnimeApiService
import com.animegatari.hayanime.data.remote.api.UserAnimeListApiService
import com.animegatari.hayanime.data.remote.api.AuthApiService
import com.animegatari.hayanime.data.repository.AnimeRepositoryImpl
import com.animegatari.hayanime.domain.repository.AuthRepository
import com.animegatari.hayanime.data.repository.AuthRepositoryImpl
import com.animegatari.hayanime.data.repository.UserAnimeListRepositoryImpl
import com.animegatari.hayanime.domain.repository.AnimeRepository
import com.animegatari.hayanime.domain.repository.UserAnimeListRepository
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
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAnimeRepository(animeRepositoryImpl: AnimeRepositoryImpl): AnimeRepository

    @Binds
    @Singleton
    abstract fun bindUserAnimeListRepository(userAnimeListRepositoryImpl: UserAnimeListRepositoryImpl): UserAnimeListRepository

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApiRetrofit

    companion object {
        @Provides
        @Singleton
        fun provideAuthInterceptor(tokenDataStore: TokenDataStore): AuthInterceptor {
            return AuthInterceptor(tokenDataStore)
        }

        @Provides
        @Singleton
        fun provideTokenAuthenticator(
            tokenDataStore: TokenDataStore,
            authApiService: Provider<AuthApiService>,
        ): TokenAuthenticator {
            return TokenAuthenticator(tokenDataStore, authApiService)
        }

        @Provides
        @Singleton
        fun provideLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

        @Provides
        @Singleton
        fun provideOkHttpClient(
            authInterceptor: AuthInterceptor,
            tokenAuthenticator: TokenAuthenticator,
            loggingInterceptor: HttpLoggingInterceptor,
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .authenticator(tokenAuthenticator)
                .build()
        }

        @Provides
        @Singleton
        @AuthRetrofit
        fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        @ApiRetrofit
        fun provideApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideAuthApiService(@AuthRetrofit retrofit: Retrofit): AuthApiService {
            return retrofit.create(AuthApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideAnimeApiService(@ApiRetrofit retrofit: Retrofit): AnimeApiService {
            return retrofit.create(AnimeApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideUserAnimeListApiService(@ApiRetrofit retrofit: Retrofit): UserAnimeListApiService {
            return retrofit.create(UserAnimeListApiService::class.java)
        }

        @Provides
        @Singleton
        fun provideTokenDataStore(@ApplicationContext context: Context): TokenDataStore {
            return TokenDataStore(context)
        }
    }
}