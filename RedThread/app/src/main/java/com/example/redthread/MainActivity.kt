package com.example.redthread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.redthread.ui.screen.HomeScreen
import com.example.redthread.ui.theme.RedThreadTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            RedThreadTheme {
                HomeScreen(
                    onProductoClick = { /* TODO: navegar a detalle */ },
                    onCarritoClick = { /* TODO: ir a carrito */ },
                )
            }
        }
    }
}
