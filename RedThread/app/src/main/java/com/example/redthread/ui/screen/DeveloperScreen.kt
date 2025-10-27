package com.example.redthread.ui.screen

import com.example.redthread.ui.viewmodel.DeveloperViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*

enum class DevTab { PRODUCTOS, PEDIDOS, RUTAS }

@Composable
fun DeveloperScreen(vm: DeveloperViewModel) {
    var tab by remember { mutableStateOf(DevTab.PRODUCTOS) }

    TabRow(selectedTabIndex = tab.ordinal) {
        DevTab.values().forEach {
            Tab(
                selected = tab == it,
                onClick = { tab = it },
                text = { Text(it.name) }
            )
        }
    }

    when (tab) {
        DevTab.PRODUCTOS -> ProductosTab(vm)
        DevTab.PEDIDOS -> DespachosTab(vm)
        DevTab.RUTAS  -> UsuariosTab(vm)
    }
}

@Composable fun ProductosTab(vm: DeveloperViewModel) {

}
@Composable fun DespachosTab(vm: DeveloperViewModel) {

}
@Composable fun UsuariosTab(vm: DeveloperViewModel)  {

}
