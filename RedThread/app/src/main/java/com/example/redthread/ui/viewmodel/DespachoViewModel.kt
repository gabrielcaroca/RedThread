package com.example.redthread.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redthread.ui.screen.EstadoDespacho
import com.example.redthread.ui.screen.PedidoUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DespachoViewModel : ViewModel() {

    private val _estado = MutableStateFlow(
        EstadoDespacho(
            filtro = "",
            cargando = false,
            mensaje = null,
            pedidos = listOf(
                PedidoUi(id = "1", codigo = "ORD-0001", estado = "Preparando"),
                PedidoUi(id = "2", codigo = "ORD-0002", estado = "En transito"),
                PedidoUi(id = "3", codigo = "ORD-0003", estado = "Preparando"),
                PedidoUi(id = "4", codigo = "ORD-0004", estado = "Entregado")
            )
        )
    )
    val estado: StateFlow<EstadoDespacho> = _estado.asStateFlow()

    fun limpiarMensaje() {
        _estado.value = _estado.value.copy(mensaje = null)
    }

    fun alCambiarFiltro(nuevo: String) {
        _estado.value = _estado.value.copy(filtro = nuevo)
    }

    fun actualizar() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true)
            // simulacion de carga
            delay(600)
            _estado.value = _estado.value.copy(
                cargando = false,
                mensaje = "Lista actualizada"
            )
        }
    }

    fun avanzarEstado(id: String) {
        val next = mapOf(
            "Preparando" to "En transito",
            "En transito" to "Entregado",
            "Entregado" to "Entregado",
            "Cancelado" to "Cancelado"
        )
        _estado.value = _estado.value.copy(
            pedidos = _estado.value.pedidos.map {
                if (it.id == id) it.copy(estado = next[it.estado] ?: it.estado) else it
            },
            mensaje = "Pedido $id avanzado"
        )
    }

    fun marcarEntregado(id: String) {
        _estado.value = _estado.value.copy(
            pedidos = _estado.value.pedidos.map {
                if (it.id == id) it.copy(estado = "Entregado") else it
            },
            mensaje = "Pedido $id marcado como entregado"
        )
    }

    fun cancelar(id: String) {
        _estado.value = _estado.value.copy(
            pedidos = _estado.value.pedidos.map {
                if (it.id == id) it.copy(estado = "Cancelado") else it
            },
            mensaje = "Pedido $id cancelado"
        )
    }
}
