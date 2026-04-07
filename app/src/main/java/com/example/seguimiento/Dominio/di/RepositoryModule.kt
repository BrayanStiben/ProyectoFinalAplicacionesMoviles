package com.example.seguimiento.Dominio.di

import com.example.seguimiento.Data.repositorios.*
import com.example.seguimiento.Data.servicios.AIServiceImpl
import com.example.seguimiento.Dominio.repositorios.*
import com.example.seguimiento.Dominio.servicios.AIService
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

    @Binds
    @Singleton
    abstract fun bindComentarioRepository(
        comentarioRepositoryImpl: ComentarioRepositoryImpl
    ): ComentarioRepository

    @Binds
    @Singleton
    abstract fun bindHistoriaFelizRepository(
        historiaFelizRepositoryImpl: HistoriaFelizRepositoryImpl
    ): HistoriaFelizRepository

    @Binds
    @Singleton
    abstract fun bindAdoptionRepository(
        adoptionRepositoryImpl: AdoptionRepositoryImpl
    ): AdoptionRepository

    @Binds
    @Singleton
    abstract fun bindNotificacionRepository(
        notificacionRepositoryImpl: NotificacionRepositoryImpl
    ): NotificacionRepository

    @Binds
    @Singleton
    abstract fun bindLogrosRepository(
        logrosRepositoryImpl: LogrosRepositoryImpl
    ): LogrosRepository

    @Binds
    @Singleton
    abstract fun bindTiendaRepository(
        tiendaRepositoryImpl: TiendaRepositoryImpl
    ): TiendaRepository

    @Binds
    @Singleton
    abstract fun bindSaludRepository(
        saludRepositoryImpl: SaludRepositoryImpl
    ): SaludRepository

    @Binds
    @Singleton
    abstract fun bindAIService(
        aiServiceImpl: AIServiceImpl
    ): AIService
}
