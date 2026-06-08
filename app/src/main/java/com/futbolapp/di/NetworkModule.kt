// di/NetworkModule.kt
package com.futbolapp.di

import com.futbolapp.BuildConfig
import com.futbolapp.data.local.TokenDataStore
import com.futbolapp.data.remote.api.*
import com.futbolapp.data.remote.interceptor.AuthInterceptor
import com.futbolapp.data.remote.interceptor.BearerTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides @Singleton
    fun provideOkHttpClient(
        tokenDataStore: TokenDataStore,
        authInterceptor: AuthInterceptor,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .authenticator(authInterceptor)                          // renueva el token en 401
        .addInterceptor(BearerTokenInterceptor(tokenDataStore))  // añade Bearer a cada request
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides @Singleton
    fun provideJugadorApi(retrofit: Retrofit): JugadorApi =
        retrofit.create(JugadorApi::class.java)

    @Provides @Singleton
    fun providePartidoApi(retrofit: Retrofit): PartidoApi =
        retrofit.create(PartidoApi::class.java)

    @Provides @Singleton
    fun provideEvaluacionApi(retrofit: Retrofit): EvaluacionApi =
        retrofit.create(EvaluacionApi::class.java)
}