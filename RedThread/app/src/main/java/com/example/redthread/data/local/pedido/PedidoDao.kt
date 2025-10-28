package com.example.redthread.data.local.pedido

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedido ORDER BY fecha DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedido WHERE estado = :estado ORDER BY fecha DESC")
    fun observarPorEstado(estado: String): Flow<List<PedidoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pedido: PedidoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturningId(pedido: PedidoEntity): Long

    @Update
    suspend fun update(pedido: PedidoEntity)

    @Delete
    suspend fun delete(pedido: PedidoEntity)
}
