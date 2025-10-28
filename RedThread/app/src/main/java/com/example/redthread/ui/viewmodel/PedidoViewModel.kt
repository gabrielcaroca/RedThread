package com.example.redthread.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redthread.data.local.database.AppDatabase
import com.example.redthread.data.local.pedido.PedidoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PedidoViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).pedidoDao()

    val pedidos = dao.observarTodos()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createPedido(
        usuario: String,
        direccion: String,
        total: Long,
        productosSnapshot: String
    ) {
        viewModelScope.launch {
            val p = PedidoEntity(
                usuario = usuario,
                direccion = direccion,
                total = total,
                productos = productosSnapshot
            )
            dao.upsert(p) // sigue disponible para otros usos
        }
    }

    // NUEVO: crear pedido y obtener el ID insertado
    suspend fun createPedidoReturnId(
        usuario: String,
        direccion: String,
        total: Long,
        productosSnapshot: String
    ): Long {
        val p = PedidoEntity(
            usuario = usuario,
            direccion = direccion,
            total = total,
            productos = productosSnapshot
        )
        return dao.insertReturningId(p)
    }
}
