package com.example.redthread.ui.viewmodel
import androidx.room.*      // Para @Dao, @Query, @Insert, @Update, etc.
import kotlinx.coroutines.flow.Flow // Para los retornos reactivos de Room
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redthread.ui.screen.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
class DespachadorViewModel : ViewModel() {

    private val _estado = MutableStateFlow(
        EstadoDespachador(
            pedidos = obtenerPedidosEjemplo(),
            rutasDisponibles = obtenerRutasEjemplo(),
            cargando = false
        )
    )
    val estado: StateFlow<EstadoDespachador> = _estado.asStateFlow()

    fun actualizarPedidos() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true)
            // Simular carga de datos
            delay(1000)
            _estado.value = _estado.value.copy(
                cargando = false,
                mensaje = "Pedidos actualizados correctamente"
            )
        }
    }

    fun cambiarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido) {
        _estado.value = _estado.value.copy(
            pedidos = _estado.value.pedidos.map { pedido ->
                if (pedido.id == pedidoId) {
                    pedido.copy(estado = nuevoEstado)
                } else {
                    pedido
                }
            },
            mensaje = "Estado del pedido actualizado a ${nuevoEstado.displayName}"
        )
    }

    fun asignarRuta(pedidoId: String, ruta: RutaDespacho) {
        _estado.value = _estado.value.copy(
            pedidos = _estado.value.pedidos.map { pedido ->
                if (pedido.id == pedidoId) {
                    pedido.copy(rutaAsignada = ruta)
                } else {
                    pedido
                }
            },
            mensaje = "Ruta ${ruta.nombre} asignada al pedido"
        )
    }

    fun adjuntarImagen(pedidoId: String, imagenUri: Uri) {
        _estado.value = _estado.value.copy(
            pedidos = _estado.value.pedidos.map { pedido ->
                if (pedido.id == pedidoId) {
                    pedido.copy(imagenAdjunta = imagenUri)
                } else {
                    pedido
                }
            },
            mensaje = "Imagen adjuntada al pedido"
        )
    }

    fun agregarNotas(pedidoId: String, notas: String) {
        _estado.value = _estado.value.copy(
            pedidos = _estado.value.pedidos.map { pedido ->
                if (pedido.id == pedidoId) {
                    pedido.copy(notas = notas)
                } else {
                    pedido
                }
            },
            mensaje = "Notas agregadas al pedido"
        )
    }

    fun filtrarPedidosPorEstado(estado: EstadoPedido?) {
        val pedidosFiltrados = if (estado == null) {
            obtenerPedidosEjemplo()
        } else {
            obtenerPedidosEjemplo().filter { it.estado == estado }
        }

        _estado.value = _estado.value.copy(pedidos = pedidosFiltrados)
    }

    fun limpiarMensaje() {
        _estado.value = _estado.value.copy(mensaje = null)
    }

    fun obtenerEstadisticas(): Map<String, Int> {
        val pedidos = _estado.value.pedidos
        return mapOf(
            "Total" to pedidos.size,
            "Pendientes" to pedidos.count { it.estado == EstadoPedido.PENDIENTE },
            "En Preparación" to pedidos.count { it.estado == EstadoPedido.EN_PREPARACION },
            "Listo para Envío" to pedidos.count { it.estado == EstadoPedido.LISTO_PARA_ENVIO },
            "En Camino" to pedidos.count { it.estado == EstadoPedido.EN_CAMINO },
            "Entregados" to pedidos.count { it.estado == EstadoPedido.ENTREGADO },
            "Cancelados" to pedidos.count { it.estado == EstadoPedido.CANCELADO }
        )
    }

    fun obtenerRutasDisponibles(): List<RutaDespacho> {
        return _estado.value.rutasDisponibles
    }

    fun obtenerPedidosPorRuta(rutaId: String): List<PedidoDespacho> {
        return _estado.value.pedidos.filter { it.rutaAsignada?.id == rutaId }
    }

    fun marcarPedidoComoEntregado(pedidoId: String) {
        cambiarEstadoPedido(pedidoId, EstadoPedido.ENTREGADO)
    }

    fun cancelarPedido(pedidoId: String, motivo: String) {
        agregarNotas(pedidoId, "Cancelado: $motivo")
        cambiarEstadoPedido(pedidoId, EstadoPedido.CANCELADO)
    }

    // Funciones de datos de ejemplo
    private fun obtenerPedidosEjemplo(): List<PedidoDespacho> {
        return listOf(
            PedidoDespacho(
                id = "1",
                codigo = "ORD-001",
                cliente = "María González",
                direccion = "Av. Principal 123, Santiago",
                telefono = "+56 9 1234 5678",
                estado = EstadoPedido.PENDIENTE,
                fechaCreacion = "15 Dic 2024",
                fechaEntrega = "16 Dic 2024",
                productos = listOf(
                    ProductoPedido("1", "Chaqueta Roja", 1, 25000.0, "ph_chaqueta.png"),
                    ProductoPedido("2", "Pantalón Azul", 2, 18000.0, "ph_pantalon.png")
                )
            ),
            PedidoDespacho(
                id = "2",
                codigo = "ORD-002",
                cliente = "Carlos López",
                direccion = "Calle Secundaria 456, Valparaíso",
                telefono = "+56 9 8765 4321",
                estado = EstadoPedido.EN_PREPARACION,
                fechaCreacion = "15 Dic 2024",
                fechaEntrega = "17 Dic 2024",
                productos = listOf(
                    ProductoPedido("3", "Polera Blanca", 3, 12000.0, "ph_polera.png")
                )
            ),
            PedidoDespacho(
                id = "3",
                codigo = "ORD-003",
                cliente = "Ana Martínez",
                direccion = "Plaza Central 789, Concepción",
                telefono = "+56 9 5555 1234",
                estado = EstadoPedido.EN_CAMINO,
                fechaCreacion = "14 Dic 2024",
                fechaEntrega = "15 Dic 2024",
                productos = listOf(
                    ProductoPedido("4", "Zapatillas Negras", 1, 35000.0, "ph_zapatillas.png"),
                    ProductoPedido("5", "Accesorio Dorado", 2, 8000.0, "ph_accesorio.png")
                )
            ),
            PedidoDespacho(
                id = "4",
                codigo = "ORD-004",
                cliente = "Roberto Silva",
                direccion = "Avenida Norte 321, Antofagasta",
                telefono = "+56 9 4444 5678",
                estado = EstadoPedido.ENTREGADO,
                fechaCreacion = "13 Dic 2024",
                fechaEntrega = "14 Dic 2024",
                productos = listOf(
                    ProductoPedido("6", "Chaqueta Verde", 1, 22000.0, "ph_chaqueta.png")
                )
            ),
            PedidoDespacho(
                id = "5",
                codigo = "ORD-005",
                cliente = "Laura Fernández",
                direccion = "Calle Sur 654, Temuco",
                telefono = "+56 9 3333 9876",
                estado = EstadoPedido.LISTO_PARA_ENVIO,
                fechaCreacion = "16 Dic 2024",
                fechaEntrega = "18 Dic 2024",
                productos = listOf(
                    ProductoPedido("7", "Pantalón Negro", 2, 20000.0, "ph_pantalon.png"),
                    ProductoPedido("8", "Polera Azul", 1, 15000.0, "ph_polera.png")
                )
            )
        )
    }

    private fun obtenerRutasEjemplo(): List<RutaDespacho> {
        return listOf(
            RutaDespacho(
                id = "1",
                nombre = "Ruta Centro",
                descripcion = "Zona céntrica de Santiago",
                distancia = 15.5,
                tiempoEstimado = 45,
                pedidosAsignados = listOf("1", "2"),
                color = Color(0xFF4CAF50)
            ),
            RutaDespacho(
                id = "2",
                nombre = "Ruta Norte",
                descripcion = "Sector norte de la ciudad",
                distancia = 22.3,
                tiempoEstimado = 60,
                pedidosAsignados = listOf("3"),
                color = Color(0xFF2196F3)
            ),
            RutaDespacho(
                id = "3",
                nombre = "Ruta Sur",
                descripcion = "Zona sur y periférica",
                distancia = 35.7,
                tiempoEstimado = 90,
                pedidosAsignados = emptyList(),
                color = Color(0xFFFF9800)
            ),
            RutaDespacho(
                id = "4",
                nombre = "Ruta Express",
                descripcion = "Entrega rápida en zona premium",
                distancia = 8.2,
                tiempoEstimado = 25,
                pedidosAsignados = listOf("4"),
                color = Color(0xFF9C27B0)
            )
        )
    }
}
