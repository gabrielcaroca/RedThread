package com.example.redthread.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redthread.data.local.ruta.RutaEntity
import com.example.redthread.ui.viewmodel.RutaViewModel

/**
 * Pantalla que muestra todas las rutas disponibles para el despachador.
 * Al seleccionar una ruta, se muestran los pedidos (productos) asociados.
 */
@Composable
fun RutasScreen(
    vm: RutaViewModel = viewModel() // usa el ViewModel existente
) {
    val rutas by vm.rutas.collectAsState()
    val rutaSeleccionada by vm.rutaSeleccionada

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Rutas disponibles",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(rutas) { ruta ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { vm.seleccionarRuta(ruta) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (ruta == rutaSeleccionada)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("🛣 ${ruta.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("Zona: ${ruta.zona}")
                        Text("Descripción: ${ruta.descripcion}")
                        Text("Pedidos asignados: ${ruta.pedidosAsignados}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        rutaSeleccionada?.let { ruta ->
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Pedidos de la ruta ${ruta.nombre}", style = MaterialTheme.typography.titleMedium)
            PedidosAsociados(ruta)
        }
    }
}

/**
 * Muestra los productos o pedidos asociados a la ruta seleccionada.
 * En esta versión se usan datos simulados, pero puedes conectarlo con tu tabla de pedidos.
 */
@Composable
fun PedidosAsociados(ruta: RutaEntity) {
    // datos simulados por ahora
    val pedidosDemo = when (ruta.nombre) {
        "Centro" -> listOf("Camisa Azul", "Pantalón Jeans", "Zapatillas Urbanas")
        "Norte" -> listOf("Polera Blanca", "Chaqueta Negra")
        "Sur" -> listOf("Short Deportivo", "Polerón Oversize", "Gorra Negra")
        else -> listOf("Sin pedidos asignados")
    }

    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(pedidosDemo) { producto ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🧾 $producto")
                    Button(onClick = { /* aquí podrías abrir detalle o marcar entregado */ }) {
                        Text("Ver")
                    }
                }
            }
        }
    }
}
