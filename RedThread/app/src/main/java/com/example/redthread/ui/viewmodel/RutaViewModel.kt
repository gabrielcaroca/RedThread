package com.example.redthread.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redthread.data.local.database.AppDatabase
import com.example.redthread.data.local.ruta.RutaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona las rutas disponibles en la base de datos local (Room).
 * Se utiliza tanto por el administrador (para crear rutas) como por el despachador (para seleccionarlas).
 */
class RutaViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getInstance(app).rutaDao()

    // --- Rutas observables en tiempo real ---
    val rutas = dao.observarRutas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // --- Ruta seleccionada por el despachador ---
    var rutaSeleccionada = androidx.compose.runtime.mutableStateOf<RutaEntity?>(null)

    fun seleccionarRuta(ruta: RutaEntity) {
        rutaSeleccionada.value = ruta
    }

    // --- Crear nueva ruta (normalmente lo haría el administrador) ---
    fun crearRuta(nombre: String, descripcion: String, zona: String, pedidosAsignados: Int = 0) {
        viewModelScope.launch {
            val nuevaRuta = RutaEntity(
                nombre = nombre,
                descripcion = descripcion,
                zona = zona,
                pedidosAsignados = pedidosAsignados
            )
            dao.upsert(nuevaRuta)
        }
    }

    // --- Actualizar datos de una ruta ---
    fun actualizarRuta(ruta: RutaEntity) {
        viewModelScope.launch { dao.update(ruta) }
    }

    // --- Eliminar una ruta ---
    fun eliminarRuta(ruta: RutaEntity) {
        viewModelScope.launch { dao.delete(ruta) }
    }

    // --- Cargar rutas de ejemplo (solo para pruebas) ---
    fun insertarRutasDemo() {
        viewModelScope.launch {
            val existentes = rutas.value
            if (existentes.isEmpty()) {
                val rutasDemo = listOf(
                    RutaEntity(nombre = "Centro", descripcion = "Entregas en zona céntrica", zona = "Centro", pedidosAsignados = 4),
                    RutaEntity(nombre = "Norte", descripcion = "Sector residencial norte", zona = "Norte", pedidosAsignados = 2),
                    RutaEntity(nombre = "Sur", descripcion = "Zona industrial y periferia", zona = "Sur", pedidosAsignados = 3)
                )
                rutasDemo.forEach { dao.upsert(it) }
            }
        }
    }
}
