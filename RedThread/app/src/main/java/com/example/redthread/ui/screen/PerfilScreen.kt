package com.example.redthread.ui.screen



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.TextPrimary
import com.example.redthread.ui.theme.TextSecondary
import com.example.redthread.ui.theme.AccentRed

@Composable
fun PerfilScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mi Perfil",
                fontSize = 26.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Usuario registrado",
                color = TextSecondary,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { /* cerrar sesión o editar perfil */ },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Cerrar sesión", color = TextPrimary)
            }
        }
    }
}
