package com.example.redthread.data.local.database


import android.content.Context
import androidx.room.Room
import com.example.redthread.data.local.pedido.PedidoDao
import com.example.redthread.data.local.pedido.PedidoEntity


class DespachadorRepository(context: Context) {

    // Inicialización de la base de datos (singleton)
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "redthread_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val pedidoDao: PedidoDao = db.pedidoDao()

    // --- FUNCIONES CRUD ---

    suspend fun agregarPedido(pedido: PedidoEntity) {
        pedidoDao.upsert(pedido)
    }

    suspend fun actualizarPedido(pedido: PedidoEntity) {
        pedidoDao.update(pedido)
    }

    suspend fun eliminarPedido(pedido: PedidoEntity) {
        pedidoDao.delete(pedido)
    }

    suspend fun obtenerPedidosPendientes() =
        pedidoDao.observarPorEstado("pendiente")

    suspend fun obtenerPedidosPorEntregar() =
        pedidoDao.observarPorEstado("por_entregar")

    suspend fun obtenerPedidosRetorno() =
        pedidoDao.observarPorEstado("retorno")

    fun observarTodosPedidos() = pedidoDao.observarTodos()
}
