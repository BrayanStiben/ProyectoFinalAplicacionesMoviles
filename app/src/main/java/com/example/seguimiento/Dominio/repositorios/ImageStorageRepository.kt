package com.example.seguimiento.Dominio.repositorios

import android.net.Uri

interface ImageStorageRepository {
    /**
     * Sube una imagen a Google Drive en la subcarpeta de la categoría especificada.
     * @param uri URI local de la imagen.
     * @param category Categoría (ej: "mascotas", "productos").
     * @param fileName Nombre deseado para el archivo.
     * @return URL pública o ID del archivo en Drive.
     */
    suspend fun uploadImage(uri: Uri, category: String, fileName: String): String?

    /**
     * Obtiene la URL de visualización de una imagen dado su ID o ruta.
     */
    suspend fun getImageUrl(fileId: String): String?
}
