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
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun agregarPedido(p: PedidoEntity) = viewModelScope.launch { dao.upsert(p) }
    fun actualizarPedido(p: PedidoEntity) = viewModelScope.launch { dao.update(p) }
}
