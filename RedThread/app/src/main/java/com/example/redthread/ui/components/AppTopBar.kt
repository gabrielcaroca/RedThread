package com.example.redthread.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
            .statusBarsPadding()                 // evita que quede bajo la status bar
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)          // altura minima garantizada del top bar
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // logo clickeable (o texto fallback)
            if (logoId != 0) {
                Image(
                    painter = painterResource(id = logoId),
                    contentDescription = "Logo RedThread",
                    modifier = Modifier
                        .height(70.dp)
                        .clickable { onLogoClick() },
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "RedThread",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLogoClick() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // boton perfil
            IconButton(onClick = onPerfilClick) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = TextPrimary
                )
            }

            // boton carrito
            IconButton(onClick = onCarritoClick) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Carrito",
                    tint = TextPrimary
                )
            }
        }

        // divisor sutil para separar de las tabs
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0x22FFFFFF))
        )
    }
}
