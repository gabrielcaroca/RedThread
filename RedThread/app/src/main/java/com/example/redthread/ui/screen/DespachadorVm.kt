package com.example.redthread.ui.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redthread.ui.viewmodel.DespachadorViewModel

@Composable
fun DespachadorVm(
    modifier: Modifier = Modifier,
    vm: DespachadorViewModel = viewModel()
) {
    val estado: EstadoDespachador by vm.estado.collectAsStateWithLifecycle()
    val avisos = remember { SnackbarHostState() }

    // Side effects: snackbar + limpiar mensaje
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            avisos.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    DespachadorScreen(
        modifier = modifier
    )
}
