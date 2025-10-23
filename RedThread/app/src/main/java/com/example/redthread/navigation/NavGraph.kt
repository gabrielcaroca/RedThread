package com.example.redthread.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.example.redthread.ui.theme.TextPrimary
import com.example.redthread.ui.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // seguimos colectando para el guard en la ruta Perfil
    val header by authViewModel.header.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                onLogoClick = {
                    navController.navigate(Route.Home.path) {
                        launchSingleTop = true
                        popUpTo(Route.Home.path) { inclusive = false }
                    }
                },
                onPerfilClick = {
                    // lee el valor ACTUAL de sesion al momento del click
                    val logged = authViewModel.header.value.isLoggedIn
                    if (logged) {
                        navController.navigate(Route.Perfil.path) {
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Route.Login.path) {
                            launchSingleTop = true
                        }
                    }
                },
                onCarritoClick = {
                    navController.navigate(Route.Carrito.path) { launchSingleTop = true }
                }
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
                    onCarritoClick = { navController.navigate(Route.Carrito.path) { launchSingleTop = true } }
                )
            }

            composable(Route.Login.path) {
                LoginScreenVm(
                    vm = authViewModel,
                    onLoginOkNavigateHome = {
                        navController.navigate(Route.Home.path) {
                            launchSingleTop = true
                            popUpTo(Route.Home.path) { inclusive = false }
                        }
                    },
                    onGoRegister = { navController.navigate(Route.Register.path) { launchSingleTop = true } }
                )
            }

            composable(Route.Register.path) {
                RegisterScreenVm(
                    vm = authViewModel,
                    onRegisteredNavigateLogin = {
                        navController.navigate(Route.Login.path) { launchSingleTop = true }
                    },
                    onGoLogin = {
                        navController.navigate(Route.Login.path) { launchSingleTop = true }
                    }
                )
            }

            composable(Route.Perfil.path) {
                // guard por si entran directo
                if (!header.isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Route.Login.path) { launchSingleTop = true }
                    }
                } else {
                    PerfilScreen()
                }
            }

            composable(Route.Carrito.path) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Carrito", color = TextPrimary)
                }
            }
        }
    }
}
