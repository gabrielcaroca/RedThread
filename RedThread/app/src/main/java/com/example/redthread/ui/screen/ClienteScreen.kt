package com.example.redthread.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redthread.ui.viewmodel.ClienteViewModel

@Composable
fun ClienteScreen() {
    val context = LocalContext.current.applicationContext as Application
    val vm: ClienteViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(context))

    var usuario by remember { mutableStateOf("Cliente Demo") }
    var direccion by remember { mutableStateOf("Av. Los Aromos #123") }
    var productos by remember { mutableStateOf("Polera Roja, Jeans Azul") }
    var total by remember { mutableStateOf("25000") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🛍️ Nueva Compra", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        // Campos de texto del pedido
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = productos,
            onValueChange = { productos = it },
            label = { Text("Productos") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = total,
            onValueChange = { total = it },
            label = { Text("Total (CLP)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Botón para agregar pedido al carrito
        Button(
            onClick = {
                vm.agregarAlCarrito(
                    usuario = usuario,
                    direccion = direccion,
                    nombreProducto = productos,
                    total = total.toLongOrNull() ?: 0
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Agregar al carrito", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(Modifier.height(8.dp))

        // Botón para confirmar la compra (checkout)
        Button(
            onClick = {
                vm.checkout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Confirmar compra", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}
