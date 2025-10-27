package com.example.redthread.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redthread.data.local.database.AppDatabase
import com.example.redthread.data.local.ruta.RutaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RutaViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).rutaDao()

    val rutas = dao.observarTodas()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun crearRuta(nombre: String, pedidosSeleccionados: List<Long>) = viewModelScope.launch {
        val ruta = RutaEntity(
            nombre = nombre,
            pedidosIds = pedidosSeleccionados.joinToString(",")
        )
        dao.upsert(ruta)
    }

    fun actualizarRuta(r: RutaEntity) = viewModelScope.launch { dao.update(r) }
    fun eliminarRuta(r: RutaEntity) = viewModelScope.launch { dao.delete(r) }
}
