package com.example.redthread.data.repository

import com.example.redthread.data.local.pedido.PedidoDao
import com.example.redthread.data.local.pedido.PedidoEntity
import kotlinx.coroutines.flow.Flow

class PedidoRepository(private val dao: PedidoDao) {

    fun observarTodos(): Flow<List<PedidoEntity>> = dao.observarTodos()
    fun observarPorEstado(estado: String): Flow<List<PedidoEntity>> = dao.observarPorEstado(estado)

    suspend fun insertarPedido(pedido: PedidoEntity) = dao.upsert(pedido)
    suspend fun actualizarPedido(pedido: PedidoEntity) = dao.update(pedido)
    suspend fun eliminarPedido(pedido: PedidoEntity) = dao.delete(pedido)
}
