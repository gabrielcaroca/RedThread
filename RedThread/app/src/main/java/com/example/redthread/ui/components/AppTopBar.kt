// app/src/main/java/com/example/redthread/ui/components/AppTopBar.kt
package com.example.redthread.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.redthread.ui.theme.Black

@Composable
fun AppTopBar(
    onLogoClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCarritoClick: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val logoId = ctx.resources.getIdentifier("logo_redthread", "drawable", ctx.packageName)

    // <<< IMPORTANTE >>>
    // Aplicamos statusBarsPadding() en el contenedor con fondo
    // para que el color negro "suba" detrás de la status bar transparente,
    // sin superponer contenido bajo los iconos del sistema.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (logoId != 0) {
                Image(
                    painter = painterResource(id = logoId),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(95.dp)
                        .clickable { onLogoClick() }
                )
            }

            Spacer(Modifier.weight(1f))

            IconButton(onClick = onPerfilClick) {
                Icon(Icons.Outlined.Person, contentDescription = "Perfil", tint = Color.White)
            }
            IconButton(onClick = onCarritoClick) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = "Carrito", tint = Color.White)
            }
        }

        Divider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.08f))
    }
}
