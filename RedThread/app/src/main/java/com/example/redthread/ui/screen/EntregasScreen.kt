package com.example.redthread.ui.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redthread.ui.viewmodel.DespachoViewModel

/* =========================
   Modelos de UI (nivel superior)
   ========================= */
data class PedidoUi(
    val id: String,
    val codigo: String,
    val estado: String,           // "Preparando" | "En transito" | "Entregado" | "Cancelado"
    val actualizando: Boolean = false
)

data class EstadoDespacho(
    val filtro: String = "",
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val pedidos: List<PedidoUi> = emptyList()
)

/* =========================================================
   Wrapper con ViewModel: observa estado y muestra la pantalla
   ========================================================= */
@Composable
fun PantallaDespachoVm(
    modifier: Modifier = Modifier,                 // regla compose: modifier primero
    vm: DespachoViewModel = viewModel(),          // obtiene VM
    alAbrirDetalle: (String) -> Unit = {}         // navegacion a detalle
) {
    val estado: EstadoDespacho by vm.estado.collectAsStateWithLifecycle()
    val avisos = remember { SnackbarHostState() }

    // Side effects: snackbar + limpiar mensaje
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            avisos.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    // UI real
    PantallaDespacho(
        filtro = estado.filtro,
        cargando = estado.cargando,
        pedidos = estado.pedidos,
        alCambiarFiltro = vm::alCambiarFiltro,
        alActualizar = vm::actualizar,
        alAvanzarEstado = vm::avanzarEstado,
        alMarcarEntregado = vm::marcarEntregado,
        alCancelar = vm::cancelar,
        alAbrirDetalle = alAbrirDetalle,
        avisos = avisos,
        modifier = modifier
    )
}
