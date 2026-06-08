// di/RepositoryModule.kt
package com.futbolapp.di

import com.futbolapp.data.repository.*
import com.futbolapp.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindJugadorRepository(impl: JugadorRepositoryImpl): JugadorRepository

    @Binds @Singleton
    abstract fun bindPartidoRepository(impl: PartidoRepositoryImpl): PartidoRepository

    @Binds @Singleton
    abstract fun bindEvaluacionRepository(impl: EvaluacionRepositoryImpl): EvaluacionRepository
}