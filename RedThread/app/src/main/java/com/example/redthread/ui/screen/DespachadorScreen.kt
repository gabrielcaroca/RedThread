package com.example.redthread.ui.screen

import android.Manifest
import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redthread.data.local.pedido.PedidoEntity
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.TextPrimary
import com.example.redthread.ui.theme.TextSecondary
import com.example.redthread.ui.viewmodel.DespachadorViewModel

@Composable
fun DespachadorScreen() {
    val context = LocalContext.current.applicationContext as Application
    val vm: DespachadorViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(context))
    val pedidos by vm.pedidosFlow.collectAsState()

    var pedidoEnDetalle by remember { mutableStateOf<PedidoEntity?>(null) }
    var etapa by remember { mutableStateOf("Recoger") }

    val etapas = listOf("Recoger", "Entregar", "Retorno")

    Box(Modifier.fillMaxSize().background(Black)) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                "Panel de Despacho",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                etapas.forEach { e ->
                    val selected = e == etapa
                    Button(
                        onClick = { etapa = e },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Color(0xFFDD3333) else Color(0xFF2A2A2A)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(e, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            val pendientes = pedidos.filter { it.estado == "pendiente" }
            val porEntregar = pedidos.filter { it.estado == "por_entregar" }
            val retornos = pedidos.filter { it.estado == "retorno" }

            when (etapa) {
                "Recoger" -> ListaPedidos(pendientes, "recoger", onRecoger = { vm.recogerPedido(it) })
                "Entregar" -> ListaPedidos(porEntregar, "entregar", onMostrarDetalle = { pedidoEnDetalle = it })
                "Retorno" -> ListaPedidos(retornos, "retorno", onMostrarDetalle = { pedidoEnDetalle = it })
            }
        }

        pedidoEnDetalle?.let { pedido ->
            DetallePedidoExpandido(
                pedido = pedido,
                esRetorno = pedido.estado == "retorno",
                onCerrar = { pedidoEnDetalle = null },
                onConfirmar = { vm.confirmarEntrega(pedido) },
                onDevolver = { motivo -> vm.marcarDevuelto(pedido, motivo) },
                onTomarFoto = { uri -> vm.guardarEvidencia(pedido, uri) }
            )
        }
    }
}

@Composable
fun ListaPedidos(
    pedidos: List<PedidoEntity>,
    tipo: String,
    onRecoger: ((PedidoEntity) -> Unit)? = null,
    onMostrarDetalle: ((PedidoEntity) -> Unit)? = null
) {
    if (pedidos.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sin pedidos", color = TextSecondary)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(pedidos) { pedido ->
                PedidoCard(pedido, tipo, onRecoger, onMostrarDetalle)
            }
        }
    }
}

@Composable
fun PedidoCard(
    pedido: PedidoEntity,
    tipo: String,
    onRecoger: ((PedidoEntity) -> Unit)? = null,
    onMostrarDetalle: ((PedidoEntity) -> Unit)? = null
) {
    val context = LocalContext.current
    val imgId = context.resources.getIdentifier(pedido.productos, "drawable", context.packageName)

    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color(0xFF2A2A2A))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (imgId != 0) {
                Image(
                    painter = painterResource(imgId),
                    contentDescription = pedido.productos,
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(Modifier.weight(1f).padding(start = 8.dp)) {
                Text(pedido.productos, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Pedido N°${pedido.id}", color = TextSecondary, fontSize = 13.sp)
            }

            if (tipo == "recoger") Button(onClick = { onRecoger?.invoke(pedido) },
                colors = ButtonDefaults.buttonColors(Color(0xFFDD3333))) { Text("Recoger", color = Color.White) }

            if (tipo == "entregar" || tipo == "retorno") Button(onClick = { onMostrarDetalle?.invoke(pedido) },
                colors = ButtonDefaults.buttonColors(Color(0xFFDD3333))) { Text("Más info", color = Color.White) }
        }
    }
}

@Composable
fun DetallePedidoExpandido(
    pedido: PedidoEntity,
    esRetorno: Boolean,
    onCerrar: () -> Unit,
    onConfirmar: () -> Unit,
    onDevolver: (String) -> Unit,
    onTomarFoto: (Uri) -> Unit
) {
    val context = LocalContext.current
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var fotoTomada by remember { mutableStateOf(false) }
    var mostrarMotivoDialog by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf("") }

    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        if (it != null) {
            fotoBitmap = it
            fotoTomada = true
            val uri = Uri.parse("content://temp/${pedido.id}")
            onTomarFoto(uri)
        } else {
            Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    if (mostrarMotivoDialog) {
        AlertDialog(
            onDismissRequest = { mostrarMotivoDialog = false },
            title = { Text("Motivo de devolución") },
            text = {
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Escribe la razón...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDevolver(motivo)
                    mostrarMotivoDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarMotivoDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1C1C1C)).padding(16.dp)
    ) {
        Column(
            Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(Color(0xFF2A2A2A)).padding(20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCerrar) { Text("✕", color = Color.White, fontSize = 22.sp) }
            }

            val imgId = context.resources.getIdentifier(pedido.productos, "drawable", context.packageName)
            if (imgId != 0)
                Image(
                    painter = painterResource(imgId),
                    contentDescription = pedido.productos,
                    modifier = Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

            Spacer(Modifier.height(16.dp))
            Text(pedido.productos, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("Pedido N°${pedido.id}", color = TextSecondary, fontSize = 13.sp)
            Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFF444444))

            Text("Dirección: ${pedido.direccion}", color = TextSecondary)
            Text("Cliente: ${pedido.usuario}", color = TextSecondary)
            Text("Total: $${pedido.total}", color = TextSecondary)
            Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFF444444))

            if (esRetorno) {
                Text("Motivo de devolución:", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(pedido.motivoDevolucion.ifBlank { "Sin motivo registrado" }, color = TextSecondary)
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            permisoCamara.launch(Manifest.permission.CAMERA)
                            camaraLauncher.launch(null)
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFDD3333)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("Foto", color = Color.White)
                    }
                    Button(
                        onClick = onConfirmar,
                        enabled = fotoTomada,
                        colors = ButtonDefaults.buttonColors(if (fotoTomada) Color(0xFF4CAF50) else Color(0xFF424242)),
                        modifier = Modifier.weight(1f)
                    ) { Text("Confirmar", color = Color.White) }
                    Button(
                        onClick = { mostrarMotivoDialog = true },
                        enabled = fotoTomada,
                        colors = ButtonDefaults.buttonColors(if (fotoTomada) Color(0xFFF44336) else Color(0xFF424242)),
                        modifier = Modifier.weight(1f)
                    ) { Text("Devolver", color = Color.White) }
                }

                fotoBitmap?.let {
                    Spacer(Modifier.height(16.dp))
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text("Evidencia guardada ✅", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}
