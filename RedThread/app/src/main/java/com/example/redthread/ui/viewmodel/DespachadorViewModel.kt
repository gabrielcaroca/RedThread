package com.example.redthread.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

// viewmodel del despachador
// maneja la lista de pedidos y los cambios de estado
class DespachadorViewModel : ViewModel() {

    // lista observable con pedidos (nombre, estado, imagen)
    val pedidos: SnapshotStateList<Triple<String, String, String>> = mutableStateListOf(
        Triple("Pedido #1", "Preparado", "ph_polera"),
        Triple("Pedido #2", "En camino", "ph_zapatillas"),
        Triple("Pedido #3", "Entregado", "ph_chaqueta"),
        Triple("Pedido #4", "Cancelado", "ph_pantalon")
    )

    // funcion para cambiar el estado de un pedido
    fun cambiarEstado(index: Int, nuevoEstado: String) {
        val actual = pedidos[index]
        pedidos[index] = actual.copy(second = nuevoEstado)
    }

    // funcion para agregar pedidos nuevos
    fun agregarPedido(nombre: String, estado: String, imagen: String) {
        pedidos.add(Triple(nombre, estado, imagen))
    }

    // funcion para eliminar pedidos
    fun eliminarPedido(index: Int) {
        if (index in pedidos.indices) {
            pedidos.removeAt(index)
        }
    }
}
