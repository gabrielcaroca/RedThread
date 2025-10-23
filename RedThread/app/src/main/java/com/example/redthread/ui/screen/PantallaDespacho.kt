package com.example.redthread.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaDespacho(
    filtro: String,
    cargando: Boolean,
    pedidos: List<PedidoUi>,
    alCambiarFiltro: (String) -> Unit,
    alActualizar: () -> Unit,
    alAvanzarEstado: (String) -> Unit,
    alMarcarEntregado: (String) -> Unit,
    alCancelar: (String) -> Unit,
    alAbrirDetalle: (String) -> Unit,
    avisos: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = avisos) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = filtro,
                    onValueChange = alCambiarFiltro,
                    label = { Text("Filtrar por codigo o estado") },
                    singleLine = true
                )
                Button(onClick = alActualizar) {
                    Text("Actualizar")
                }
            }

            Spacer(Modifier.height(12.dp))

            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val lista = if (filtro.isBlank()) pedidos
                else pedidos.filter {
                    it.codigo.contains(filtro, ignoreCase = true) ||
                            it.estado.contains(filtro, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lista, key = { it.id }) { p ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { alAbrirDetalle(p.id) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(text = "Codigo: ${p.codigo}")
                                Text(text = "Estado: ${p.estado}")
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(onClick = { alAvanzarEstado(p.id) }) {
                                        Text("Avanzar")
                                    }
                                    Button(onClick = { alMarcarEntregado(p.id) }) {
                                        Text("Entregar")
                                    }
                                    Button(onClick = { alCancelar(p.id) }) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
