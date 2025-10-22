package com.example.redthread.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.TextPrimary

@Composable
fun AppTopBar(
    onHomeClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCarritoClick: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val logoId = ctx.resources.getIdentifier("logo_redthread", "drawable", ctx.packageName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo o texto
        if (logoId != 0) {
            Image(
                painter = painterResource(id = logoId),
                contentDescription = "logo red thread",
                modifier = Modifier.height(95.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "red thread",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón Home
        IconButton(onClick = onHomeClick) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = "Home",
                tint = TextPrimary
            )
        }

        // Botón Perfil
        IconButton(onClick = onPerfilClick) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Perfil",
                tint = TextPrimary
            )
        }

        // Botón Carrito
        IconButton(onClick = onCarritoClick) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Carrito",
                tint = TextPrimary
            )
        }
    }
}
