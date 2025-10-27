package com.example.redthread.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redthread.ui.theme.*

// vista principal del despachador
@Composable
fun DespachadorScreen() {

    // lista de pedidos simulada (puedes reemplazar por tu VM)
    var pedidos by remember {
        mutableStateOf(
            listOf(
                mutableStateOf(Triple("Pedido #1", "Preparado", "ph_polera")),
                mutableStateOf(Triple("Pedido #2", "En camino", "ph_zapatillas")),
                mutableStateOf(Triple("Pedido #3", "Entregado", "ph_chaqueta")),
                mutableStateOf(Triple("Pedido #4", "Cancelado", "ph_pantalon"))
            )
        )
    }

    // fondo negro del tema redthread
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Despachos asignados",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // lista de pedidos
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(pedidos) { pedidoState ->

                val pedido = pedidoState.value
                val nombre = pedido.first
                val estado = pedido.second
                val imagen = pedido.third

                PedidoCard(
                    nombre = nombre,
                    estado = estado,
                    imagen = imagen,
                    onEstadoChange = { nuevo ->
                        // actualiza el estado del pedido al presionar el boton
                        pedidoState.value = pedido.copy(second = nuevo)
                    }
                )
            }
        }
    }
}

// composable que muestra una card por pedido
@Composable
private fun PedidoCard(
    nombre: String,
    estado: String,
    imagen: String,
    onEstadoChange: (String) -> Unit
) {
    val ctx = LocalContext.current
    val imgId = ctx.resources.getIdentifier(imagen, "drawable", ctx.packageName)

    // definimos color segun estado
    val colorEstado = when (estado) {
        "Preparado" -> Color(0xFFFFC107) // amarillo
        "En camino" -> Color(0xFF9E9E9E) // gris
        "Entregado" -> Color(0xFF4CAF50) // verde
        "Cancelado" -> Color(0xFFF44336) // rojo
        else -> TextSecondary
    }

    Surface(
        color = CardGray,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (imgId != 0) {
                    Image(
                        painter = painterResource(id = imgId),
                        contentDescription = nombre,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardGrayElevated)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(nombre, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Estado: $estado", color = colorEstado, fontSize = 14.sp)
                    Text("Entrega estimada: 28/10/2025", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                EstadoButton("Preparado", estado, onEstadoChange)
                EstadoButton("En camino", estado, onEstadoChange)
                EstadoButton("Entregado", estado, onEstadoChange)
                EstadoButton("Cancelado", estado, onEstadoChange)
            }
        }
    }
}

// boton reutilizable que cambia el estado del pedido
@Composable
private fun EstadoButton(
    label: String,
    estadoActual: String,
    onEstadoChange: (String) -> Unit
) {
    val isSelected = label == estadoActual
    val bg = if (isSelected) AccentRed else CardGrayElevated
    val textColor = if (isSelected) TextPrimary else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onEstadoChange(label) }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(label, color = textColor, fontSize = 12.sp)
    }
}
