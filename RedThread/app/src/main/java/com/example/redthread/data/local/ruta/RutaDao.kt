package com.example.redthread.data.local.ruta

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la tabla de rutas.
 * Permite observar, insertar y actualizar rutas asignadas por el administrador.
 */
@Dao
interface RutaDao {

    // --- OBTENER TODAS LAS RUTAS ---
    @Query("SELECT * FROM ruta ORDER BY id DESC")
    fun observarRutas(): Flow<List<RutaEntity>>

    // --- INSERTAR O ACTUALIZAR UNA RUTA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(ruta: RutaEntity)

    // --- INSERTAR VARIAS RUTAS A LA VEZ ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRutas(rutas: List<RutaEntity>)

    // --- ACTUALIZAR DATOS DE UNA RUTA ---
    @Update
    suspend fun update(ruta: RutaEntity)

    // --- ELIMINAR UNA RUTA ---
    @Delete
    suspend fun delete(ruta: RutaEntity)

    // --- ELIMINAR TODAS LAS RUTAS (para limpiar o reiniciar base) ---
    @Query("DELETE FROM ruta")
    suspend fun eliminarTodas()
}
