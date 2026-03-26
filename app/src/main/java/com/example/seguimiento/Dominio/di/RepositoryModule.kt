package com.example.seguimiento.Dominio.di

import com.example.seguimiento.Data.repositorios.AuthRepositoryImpl
import com.example.seguimiento.Data.repositorios.MascotaRepositoryImpl
import com.example.seguimiento.Data.repositorios.RefugioRepositoryImpl
import com.example.seguimiento.Data.repositorios.UserRepositoryImpl
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
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
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMascotaRepository(
        mascotaRepositoryImpl: MascotaRepositoryImpl
    ): MascotaRepository

    @Binds
    @Singleton
    abstract fun bindRefugioRepository(
        refugioRepositoryImpl: RefugioRepositoryImpl
    ): RefugioRepository
}
