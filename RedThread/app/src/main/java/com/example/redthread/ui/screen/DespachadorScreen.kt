package com.example.redthread.ui.screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.TextPrimary
import com.example.redthread.ui.theme.TextSecondary
import com.example.redthread.ui.viewmodel.DespachadorViewModel
import com.example.redthread.ui.viewmodel.Pedido
import com.example.redthread.ui.viewmodel.Ruta

@Composable
fun DespachadorScreen(vm: DespachadorViewModel = viewModel()) {

    val rutaSeleccionada by vm.rutaSeleccionada
    val pedidos = vm.pedidos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(text = "Panel de Despacho", color = TextPrimary, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))

        // --- RUTAS ---
        Text("Rutas disponibles", color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            vm.rutas.forEach { ruta ->
                val selected = ruta == rutaSeleccionada
                Button(
                    onClick = { vm.cargarPedidos(ruta) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) Color(0xFFDD3333) else Color(0xFF2A2A2A)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("${ruta.nombre}\n$${ruta.precio}", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (rutaSeleccionada == null) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Selecciona una ruta para ver los pedidos", color = TextSecondary)
            }
        } else {
            Text(
                "Despachos (${rutaSeleccionada!!.nombre})",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(pedidos) { index, pedido ->
                    PedidoCard(
                        pedido = pedido,
                        onTomarFoto = { uri -> vm.guardarEvidencia(index, uri) },
                        onConfirmar = { vm.confirmarEntrega(index) },
                        onDevolver = { vm.marcarDevuelto(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun PedidoCard(
    pedido: Pedido,
    onTomarFoto: (Uri) -> Unit,
    onConfirmar: () -> Unit,
    onDevolver: () -> Unit
) {
    val context = LocalContext.current
    val imgId = context.resources.getIdentifier(pedido.imagen, "drawable", context.packageName)

    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // --- Lanza cámara sin FileProvider ---
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            fotoBitmap = it
            // crea una uri temporal simulada
            val tempUri = Uri.parse("content://temp/${pedido.nombre}")
            onTomarFoto(tempUri)
        }
    }

    val puedeConfirmar = fotoBitmap != null && !pedido.entregado && !pedido.devuelto
    val puedeDevolver = fotoBitmap != null && !pedido.entregado && !pedido.devuelto

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF202020))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (imgId != 0) {
                Image(
                    painter = painterResource(id = imgId),
                    contentDescription = pedido.nombre,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 10.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column {
                Text(pedido.nombre, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(
                    when {
                        pedido.entregado -> "Entregado ✅"
                        pedido.devuelto -> "Devuelto al almacén 🏠"
                        fotoBitmap != null -> "Evidencia lista 📸"
                        else -> "Pendiente ⏳"
                    },
                    color = when {
                        pedido.entregado -> Color(0xFF4CAF50)
                        pedido.devuelto -> Color(0xFFF44336)
                        fotoBitmap != null -> Color(0xFFFFC107)
                        else -> TextSecondary
                    },
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // --- FILA DE BOTONES ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tomar Foto
            Button(
                onClick = { launcher.launch(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDD3333)),
                modifier = Modifier.weight(1f)
            ) {
                Text("Tomar Foto", color = Color.White)
            }

            // Confirmar Entrega
            Button(
                onClick = onConfirmar,
                enabled = puedeConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (puedeConfirmar)
                        Color(0xFF4CAF50) else Color(0xFF424242)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirmar", color = Color.White)
            }

            // Devolver al Almacén
            Button(
                onClick = onDevolver,
                enabled = puedeDevolver,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (puedeDevolver)
                        Color(0xFFF44336) else Color(0xFF424242)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Devolver", color = Color.White)
            }
        }

        fotoBitmap?.let {
            Spacer(Modifier.height(8.dp))
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Foto evidencia",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 4.dp),
                contentScale = ContentScale.Crop
            )
            Text("Evidencia guardada ✅", color = TextSecondary, fontSize = 12.sp)
        }
    }
}
