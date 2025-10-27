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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.redthread.ui.theme.Black

@Composable
fun AppTopBar(
    onLogoClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCarritoClick: () -> Unit = {},
    cartCount: Int = 0                                     // <<< NUEVO: contador para el badge
) {
    val ctx = LocalContext.current
    val logoId = ctx.resources.getIdentifier("logo_redthread", "drawable", ctx.packageName)

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
                        .height(145.dp)
                        .clickable { onLogoClick() }
                )
            }

            Spacer(Modifier.weight(1f))

            IconButton(onClick = onPerfilClick) {
                Icon(Icons.Outlined.Person, contentDescription = "Perfil", tint = Color.White)
            }

            // Icono de carrito con badge
            Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onCarritoClick) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Carrito", tint = Color.White)
                }
                if (cartCount > 0) {
                    // Badge redondo arriba a la derecha del icono
                    Box(
                        modifier = Modifier
                            .offset(x = (-2).dp, y = 6.dp)
                            .size(18.dp)
                            .drawBehind {
                                drawCircle(
                                    color = Color(0xFFE53935), // rojo tipo badge
                                    radius = size.minDimension / 2f,
                                    center = Offset(size.width / 2f, size.height / 2f)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val label = if (cartCount > 9) "+9" else cartCount.toString()
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Divider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.08f))
    }
}
