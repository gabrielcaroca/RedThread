package com.example.redthread.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redthread.ui.theme.*

// Modelos de datos
data class PedidoDespacho(
    val id: String,
    val codigo: String,
    val cliente: String,
    val direccion: String,
    val telefono: String,
    val estado: EstadoPedido,
    val fechaCreacion: String,
    val fechaEntrega: String,
    val productos: List<ProductoPedido>,
    val imagenAdjunta: Uri? = null,
    val rutaAsignada: RutaDespacho? = null,
    val notas: String = ""
)

data class ProductoPedido(
    val id: String,
    val nombre: String,
    val cantidad: Int,
    val precio: Double,
    val imagen: String
)

data class RutaDespacho(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val distancia: Double, // en km
    val tiempoEstimado: Int, // en minutos
    val pedidosAsignados: List<String>,
    val color: Color
)

enum class EstadoPedido(val displayName: String, val color: Color, val icon: ImageVector) {
    PENDIENTE("Pendiente", Color(0xFFFF9800), Icons.Default.Schedule),
    EN_PREPARACION("En Preparación", Color(0xFF2196F3), Icons.Default.Build),
    LISTO_PARA_ENVIO("Listo para Envío", Color(0xFF9C27B0), Icons.Default.LocalShipping),
    EN_CAMINO("En Camino", Color(0xFF3F51B5), Icons.Default.DirectionsCar),
    ENTREGADO("Entregado", Color(0xFF4CAF50), Icons.Default.CheckCircle),
    CANCELADO("Cancelado", Color(0xFFF44336), Icons.Default.Cancel)
}

data class EstadoDespachador(
    val pedidos: List<PedidoDespacho> = emptyList(),
    val rutasDisponibles: List<RutaDespacho> = emptyList(),
    val pedidoSeleccionado: PedidoDespacho? = null,
    val cargando: Boolean = false,
    val mensaje: String? = null
)



@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun DespachadorScreen(
    modifier: Modifier = Modifier
) {
    var estado by remember { mutableStateOf(EstadoDespachador()) }
    var mostrarSelectorRuta by remember { mutableStateOf(false) }
    var mostrarDetallePedido by remember { mutableStateOf(false) }
    var uriImagen by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Launcher para cámara
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // La imagen se guardó exitosamente
            estado = estado.copy(
                pedidoSeleccionado = estado.pedidoSeleccionado?.copy(imagenAdjunta = uriImagen)
            )
        }
    }

    // Inicializar datos de ejemplo
    LaunchedEffect(Unit) {
        estado = estado.copy(
            pedidos = obtenerPedidosEjemplo(),
            rutasDisponibles = obtenerRutasEjemplo()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Panel del Despachador",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { /* Refrescar datos */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.
                topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Resumen rápido
            ResumenDespachador(
                pedidos = estado.pedidos,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Lista de pedidos
            Text(
                "Pedidos Asignados",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.pedidos, key = { it.id }) { pedido ->
                    PedidoCard(
                        pedido = pedido,
                        onSeleccionar = {
                            estado = estado.copy(pedidoSeleccionado = pedido)
                            mostrarDetallePedido = true
                        },
                        onCambiarEstado = { nuevoEstado ->
                            estado = estado.copy(
                                pedidos = estado.pedidos.map {
                                    if (it.id == pedido.id) it.copy(estado = nuevoEstado) else it
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    // Dialog de detalle del pedido
    if (mostrarDetallePedido && estado.pedidoSeleccionado != null) {
        DetallePedidoDialog(
            pedido = estado.pedidoSeleccionado!!,
            rutasDisponibles = estado.rutasDisponibles,
            onDismiss = { mostrarDetallePedido = false },
            onAsignarRuta = { ruta ->
                estado = estado.copy(
                    pedidos = estado.pedidos.map {
                        if (it.id == estado.pedidoSeleccionado!!.id)
                            it.copy(rutaAsignada = ruta)
                        else it
                    }
                )
                mostrarDetallePedido = false
            },
            onTomarFoto = {
                uriImagen = it
                launcherCamara.launch(it)
            }
        )
    }
}

@Composable
private fun ResumenDespachador(
    pedidos: List<PedidoDespacho>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricItem(
                icon = Icons.Default.ShoppingCart,
                label = "Total",
                value = pedidos.size.toString(),
                color = AccentRed
            )
            MetricItem(
                icon = Icons.Default.Schedule,
                label = "Pendientes",
                value = pedidos.count { it.estado == EstadoPedido.PENDIENTE }.toString(),
                color = Color(0xFFFF9800)
            )
            MetricItem(
                icon = Icons.Default.LocalShipping,
                label = "En Camino",
                value = pedidos.count { it.estado == EstadoPedido.EN_CAMINO }.toString(),
                color = Color(0xFF3F51B5)
            )
            MetricItem(
                icon = Icons.Default.CheckCircle,
                label = "Entregados",
                value = pedidos.count { it.estado == EstadoPedido.ENTREGADO }.toString(),
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun PedidoCard(
    pedido: PedidoDespacho,
    onSeleccionar: () -> Unit,
    onCambiarEstado: (EstadoPedido) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSeleccionar() },
        colors = CardDefaults.cardColors(containerColor = CardGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con código y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        pedido.codigo,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        pedido.cliente,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                EstadoChip(estado = pedido.estado)
            }

            Spacer(Modifier.height(12.dp))

            // Información del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.LocationOn,
                    label = "Dirección",
                    value = pedido.direccion,
                    color = TextSecondary
                )
                InfoItem(
                    icon = Icons.Default.Schedule,
                    label = "Entrega",
                    value = pedido.fechaEntrega,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(12.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onSeleccionar() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ver Detalle", fontSize = 12.sp)
                }

                Button(
                    onClick = { /* Mostrar selector de estado */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Cambiar Estado", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun EstadoChip(estado: EstadoPedido) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            estado.icon,
            contentDescription = null,
            tint = estado.color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(estado.color.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                estado.displayName,
                color = estado.color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
data class PedidoUi(
    val id: Int,
    val codigo: String,
    val estado: String,
    val fechaEntrega: String
)
@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                label,
                color = color,
                fontSize = 12.sp
            )
            Text(
                value,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DetallePedidoDialog(
    pedido: PedidoDespacho,
    rutasDisponibles: List<RutaDespacho>,
    onDismiss: () -> Unit,
    onAsignarRuta: (RutaDespacho) -> Unit,
    onTomarFoto: (Uri) -> Unit
) {
    var mostrarSelectorRuta by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Detalle del Pedido",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Código: ${pedido.codigo}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Text("Cliente: ${pedido.cliente}", color = TextSecondary)
                }
                item {
                    Text("Dirección: ${pedido.direccion}", color = TextSecondary)
                }
                item {
                    Text("Teléfono: ${pedido.telefono}", color = TextSecondary)
                }
                item {
                    Text("Fecha de entrega: ${pedido.fechaEntrega}", color = TextSecondary)
                }

                // Productos
                item {
                    Text("Productos:", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                items(pedido.productos) { producto ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${producto.nombre} x${producto.cantidad}", color = TextSecondary)
                        Text("$${producto.precio}", color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                }

                // Ruta asignada
                item {
                    if (pedido.rutaAsignada != null) {
                        Text(
                            "Ruta: ${pedido.rutaAsignada.nombre}",
                            color = TextSecondary
                        )
                    } else {
                        Button(
                            onClick = { mostrarSelectorRuta = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                        ) {
                            Text("Asignar Ruta")
                        }
                    }
                }

                // Imagen adjunta
                item {
                    if (pedido.imagenAdjunta != null) {
                        Text("Imagen adjunta: ✓", color = Color(0xFF4CAF50))
                    } else {
                        Button(
                            onClick = { /* Tomar foto */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("Tomar Foto")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TextSecondary)
            }
        },
        containerColor = CardGray,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary
    )

    // Selector de rutas
    if (mostrarSelectorRuta) {
        SelectorRutaDialog(
            rutas = rutasDisponibles,
            onRutaSeleccionada = { ruta ->
                onAsignarRuta(ruta)
                mostrarSelectorRuta = false
            },
            onDismiss = { mostrarSelectorRuta = false }
        )
    }
}

@Composable
private fun SelectorRutaDialog(
    rutas: List<RutaDespacho>,
    onRutaSeleccionada: (RutaDespacho) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Seleccionar Ruta",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rutas) { ruta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRutaSeleccionada(ruta) },
                        colors = CardDefaults.cardColors(containerColor = CardGrayElevated)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Route,
                                contentDescription = null,
                                tint = ruta.color,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    ruta.nombre,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${ruta.distancia}km - ${ruta.tiempoEstimado}min",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "${ruta.pedidosAsignados.size} pedidos asignados",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        containerColor = CardGray,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary
    )
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
        )
    )
}
