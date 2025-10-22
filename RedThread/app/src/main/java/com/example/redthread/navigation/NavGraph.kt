package com.example.redthread.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.redthread.ui.components.AppTopBar
import com.example.redthread.ui.screen.HomeScreen
import com.example.redthread.ui.screen.LoginScreenVm
import com.example.redthread.ui.screen.RegisterScreenVm
import com.example.redthread.ui.screen.PerfilScreen
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    Scaffold(
        topBar = {
            AppTopBar(
                onHomeClick = { navController.navigate(Route.Home.path) },
                onPerfilClick = { navController.navigate(Route.Perfil.path) },
                onCarritoClick = { navController.navigate(Route.Carrito.path) }
            )
        },
        containerColor = Black
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Route.Home.path,
            modifier = Modifier
                .padding(innerPadding)
                .background(Black)
        ) {
            composable(Route.Home.path) {
                HomeScreen(
                    onProductoClick = { /* detalle producto */ },
                    onCarritoClick = { navController.navigate(Route.Carrito.path) }
                )
            }

            composable(Route.Login.path) {
                LoginScreenVm(
                    vm = authViewModel,
                    onLoginOkNavigateHome = { navController.navigate(Route.Home.path) },
                    onGoRegister = { navController.navigate(Route.Register.path) }
                )
            }

            composable(Route.Register.path) {
                RegisterScreenVm(
                    vm = authViewModel,
                    onRegisteredNavigateLogin = { navController.navigate(Route.Login.path) },
                    onGoLogin = { navController.navigate(Route.Login.path) }
                )
            }

            composable(Route.Perfil.path) {
                PerfilScreen()
            }
        }
    }
}
