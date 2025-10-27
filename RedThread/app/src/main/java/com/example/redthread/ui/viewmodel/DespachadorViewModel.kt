package com.example.redthread.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// ===============================
// ViewModel del módulo despachador
// ===============================
class DespachadorViewModel : ViewModel() {

    // Rutas disponibles
    val rutas = listOf(
        Ruta("Centro", 2500),
        Ruta("Norte", 3500),
        Ruta("Sur", 4000)
    )

    // Estado actual de la ruta seleccionada
    var rutaSeleccionada = mutableStateOf<Ruta?>(null)

    // Lista observable de pedidos por ruta
    var pedidos = mutableStateListOf<Pedido>()

    // Pedido actualmente seleccionado (para vista detallada)
    var pedidoSeleccionado = mutableStateOf<Pedido?>(null)

    // Cargar los pedidos según la ruta elegida
    fun cargarPedidos(ruta: Ruta) {
        rutaSeleccionada.value = ruta
        pedidos.clear()

        val data = when (ruta.nombre) {
            "Centro" -> listOf(
                Pedido("Pedido #1", "ph_polera", false, null),
                Pedido("Pedido #2", "ph_zapatillas", false, null)
            )
            "Norte" -> listOf(
                Pedido("Pedido #3", "ph_pantalon", false, null)
            )
            "Sur" -> listOf(
                Pedido("Pedido #4", "ph_chaqueta", false, null),
                Pedido("Pedido #5", "ph_accesorio", false, null)
            )
            else -> emptyList()
        }

        pedidos.addAll(data)
    }

    // Selecciona un pedido para verlo en detalle
    fun seleccionarPedido(pedido: Pedido) {
        pedidoSeleccionado.value = pedido
    }

    // Cierra la vista de detalle
    fun cerrarDetalle() {
        pedidoSeleccionado.value = null
    }

    // Marca un pedido como entregado
    fun confirmarEntrega(index: Int) {
        val actual = pedidos[index]
        pedidos[index] = actual.copy(entregado = true)
    }

    // Guarda una imagen como evidencia de entrega
    fun guardarEvidencia(index: Int, uri: Uri) {
        val actual = pedidos[index]
        pedidos[index] = actual.copy(fotoEvidencia = uri)
    }

    // Marca un pedido como devuelto al almacén
    fun marcarDevuelto(index: Int) {
        val actual = pedidos[index]
        pedidos[index] = actual.copy(devuelto = true)
    }
}

// ===============================
// Data classes de apoyo
// ===============================
data class Ruta(
    val nombre: String,
    val precio: Int
)

data class Pedido(
    val nombre: String,
    val imagen: String,
    val entregado: Boolean,
    val fotoEvidencia: Uri?,
    val devuelto: Boolean = false     // <<< NUEVO ESTADO agregado
)
