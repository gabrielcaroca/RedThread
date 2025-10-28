package com.example.redthread.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redthread.data.local.database.DespachadorRepository
import com.example.redthread.data.local.pedido.PedidoEntity
import kotlinx.coroutines.launch

/**
 * ViewModel del cliente conectado a la base de datos SQLite (Room).
 * Permite agregar productos al carrito y confirmar el checkout.
 */
class ClienteViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = DespachadorRepository(application)

    // Lista temporal del carrito
    private val carrito = mutableListOf<PedidoEntity>()

    /**
     * Agrega un producto al carrito (no lo guarda aún en la BD)
     */
    fun agregarAlCarrito(
        usuario: String,
        direccion: String,
        nombreProducto: String,
        total: Long
    ) {
        val nuevoPedido = PedidoEntity(
            usuario = usuario,
            direccion = direccion,
            total = total,
            productos = nombreProducto,
            estado = "pendiente",
            fotoEvidencia = null,
            devuelto = false,
            motivoDevolucion = "",
            entregado = false
        )
        carrito.add(nuevoPedido)
    }

    /**
     * Guarda todos los pedidos del carrito en la base de datos SQLite.
     */
    fun checkout() {
        viewModelScope.launch {
            carrito.forEach { pedido ->
                repo.agregarPedido(pedido)
            }
            carrito.clear()
        }
    }
}
