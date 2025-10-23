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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.TextPrimary

@Composable
fun AppTopBar(
    onLogoClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCarritoClick: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val logoId = ctx.resources.getIdentifier("logo_redthread", "drawable", ctx.packageName)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .statusBarsPadding() // evita solaparse con la status bar
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // altura estándar de top bar
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo clickeable (sin fallback de texto)
            if (logoId != 0) {
                Image(
                    painter = painterResource(id = logoId),
                    contentDescription = null,
                    modifier = Modifier
                        .height(28.dp) // tamaño visual agradable
                        .clickable { onLogoClick() },
                    contentScale = ContentScale.Fit
                )
            } else {
                // Mantén el layout estable si aún no subes el drawable
                Spacer(Modifier.width(120.dp).height(28.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onPerfilClick) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = TextPrimary
                )
            }
            IconButton(onClick = onCarritoClick) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Carrito",
                    tint = TextPrimary
                )
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = TextPrimary.copy(alpha = 0.15f), // separador sutil
            thickness = 1.dp
        )
    }
}
