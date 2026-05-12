package com.example.seguimiento.Dominio.di

import com.example.seguimiento.Data.repositorios.ImgBBRepositoryImpl
import com.example.seguimiento.Dominio.repositorios.ImageStorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageModule {

    @Binds
    @Singleton
    abstract fun bindImageStorageRepository(
        impl: ImgBBRepositoryImpl
    ): ImageStorageRepository
}
