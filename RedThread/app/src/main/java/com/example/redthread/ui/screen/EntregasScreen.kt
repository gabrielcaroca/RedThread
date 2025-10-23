package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background                 // Fondo
import androidx.compose.foundation.layout.*                   // Box/Column/Row/Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons                  // Íconos Material
import androidx.compose.material.icons.filled.Visibility      // Ícono mostrar
import androidx.compose.material.icons.filled.VisibilityOff   // Ícono ocultar
import androidx.compose.material3.*                           // Material 3
import androidx.compose.runtime.*                             // remember, Composable
import androidx.compose.ui.Alignment                          // Alineaciones
import androidx.compose.ui.Modifier                           // Modificador
import androidx.compose.ui.text.input.*                       // KeyboardOptions/Types/Transformations
import androidx.compose.ui.unit.dp                            // DPs
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Observa StateFlow
import androidx.lifecycle.viewmodel.compose.viewModel         // Obtiene VM
import com.example.uinavegacion.ui.viewmodel.AuthViewModel         // ViewModel
@Composable
fun PantallaDespachoVm(
    vm: DespachoVm = viewModel<DespachoViewModel>(),  // obtiene el VM por defecto
    alAbrirDetalle: (String) -> Unit = {}             // navegación a detalle (si la tienes)
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val avisos = remember { SnackbarHostState() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            avisos.showSnackbar(it)
            vm.limpiarMensaje()
        }
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
            avisos = avisos
        )
    }
    data class PedidoUi(
        val id: String,
        val codigo: String,
        val estado: String,           // "Preparando" | "En tránsito" | "Entregado" | "Cancelado"
        val actualizando: Boolean = false
    )

    data class EstadoDespacho(
        val filtro: String = "",
        val cargando: Boolean = false,
        val mensaje: String? = null,
        val pedidos: List<PedidoUi> = emptyList()
    )

}