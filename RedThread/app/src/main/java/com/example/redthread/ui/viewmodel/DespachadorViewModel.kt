package com.example.redthread.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redthread.data.local.database.DespachadorRepository
import com.example.redthread.data.local.pedido.PedidoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel conectado a Room para manejar los pedidos del despachador.
 */
class DespachadorViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = DespachadorRepository(application)

    // Flow que expone todos los pedidos guardados en SQLite
    private val _pedidosFlow = MutableStateFlow<List<PedidoEntity>>(emptyList())
    val pedidosFlow: StateFlow<List<PedidoEntity>> get() = _pedidosFlow

    init {
        cargarPedidosDesdeDB()
    }

    private fun cargarPedidosDesdeDB() {
        viewModelScope.launch {
            repo.observarTodosPedidos().collect { lista ->
                _pedidosFlow.value = lista
            }
        }
    }

    fun recogerPedido(pedido: PedidoEntity) {
        viewModelScope.launch {
            val actualizado = pedido.copy(estado = "por_entregar")
            repo.actualizarPedido(actualizado)
        }
    }

    fun guardarEvidencia(pedido: PedidoEntity, uri: Uri) {
        viewModelScope.launch {
            val actualizado = pedido.copy(fotoEvidencia = uri.toString())
            repo.actualizarPedido(actualizado)
        }
    }

    fun confirmarEntrega(pedido: PedidoEntity) {
        viewModelScope.launch {
            repo.eliminarPedido(pedido)
        }
    }

    fun marcarDevuelto(pedido: PedidoEntity, motivo: String) {
        viewModelScope.launch {
            val actualizado = pedido.copy(
                estado = "retorno",
                devuelto = true,
                motivoDevolucion = motivo
            )
            repo.actualizarPedido(actualizado)
        }
    }
}
