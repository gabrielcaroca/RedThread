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

    fun registrarPedido(pedido: PedidoEntity) {
        viewModelScope.launch { dao.upsert(pedido) }
    }

    fun actualizar(pedido: PedidoEntity) {
        viewModelScope.launch { dao.update(pedido) }
    }

    fun eliminar(pedido: PedidoEntity) {
        viewModelScope.launch { dao.delete(pedido) }
    }

    /**
     * Punto de entrada desde Checkout:
     * - usuario: nombre visible del comprador (o email)
     * - direccion: texto resultante de la dirección elegida
     * - total: total final CLP
     * - productosSnapshot: texto/JSON con los ítems (p.ej. "[{'sku':'X','cant':2,'precio':...},...]")
     */
    fun crearPedidoDesdeCheckout(
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
            dao.upsert(p)
        }
    }
}
