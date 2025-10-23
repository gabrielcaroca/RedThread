package com.example.redthread.ui.screen

import android.content.res.Resources
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.redthread.ui.components.AppTopBar
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.CardGray
import com.example.redthread.ui.theme.CardGrayElevated
import com.example.redthread.ui.theme.TextPrimary
import com.example.redthread.ui.theme.TextSecondary
import com.example.redthread.ui.viewmodel.HomeViewModel

enum class Filtro { TODOS, HOMBRES, MUJERES }

// modelo de item de la grilla
data class ProductoUi(
    val id: Int,
    val nombre: String,
    val precio: String,
    val categoria: String, // polera | chaqueta | pantalon | zapatillas | accesorio
    val target: Filtro = Filtro.TODOS
)

@Composable
fun HomeScreen(
    onProductoClick: (ProductoUi) -> Unit = {},
    onCarritoClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    // estado de tab seleccionado
    var filtro by remember { mutableStateOf(Filtro.TODOS) }

    // consumimos productos desde el vm
    val productos by viewModel.productos.collectAsState()
    val filtrados = productos.filter { filtro == Filtro.TODOS || it.target == filtro }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        // Barra superior global
        AppTopBar(
            onLogoClick = { /* navegación a Home */ },
            onPerfilClick = onPerfilClick,
            onCarritoClick = onCarritoClick
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
                .padding(top = 8.dp)
        ) {
            TabsAnimated(
                selected = filtro,
                onSelect = { filtro = it }
            )

            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtrados, key = { it.id }) { p ->
                    ProductCard(producto = p, onClick = { onProductoClick(p) })
                }
            }
        }
    }
}

@Composable
private fun TabsAnimated(
    selected: Filtro,
    onSelect: (Filtro) -> Unit
) {
    // definimos las 3 pestañas y su orden
    val items = listOf(
        Filtro.TODOS to "Principal",
        Filtro.HOMBRES to "Hombres",
        Filtro.MUJERES to "Mujeres"
    )

    // ancho fijo por tab para facilitar la animacion del indicador
    val tabWidth: Dp = 100.dp
    val selectedIndex = items.indexOfFirst { it.first == selected }.coerceAtLeast(0)

    // animamos la posicion x del indicador
    val targetOffset = (selectedIndex * tabWidth.value)
    val offsetAnim by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "tabOffset"
    )

    // animacion del ancho (por si luego cambias a tabs scrollables)
    val widthAnim by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "tabWidth"
    )

    Box(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { idx, pair ->
                val isSelected = idx == selectedIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(tabWidth)
                        .clickable { onSelect(pair.first) }
                        .padding(vertical = 8.dp)
                ) {
                    // crossfade para el color del texto
                    Crossfade(targetState = isSelected, label = "tabText") { sel ->
                        Text(
                            text = pair.second,
                            color = if (sel) TextPrimary else TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        // indicador deslizante bajo el tab activo
        Box(
            Modifier
                .padding(horizontal = 20.dp)
                .height(2.dp)
                .width(widthAnim)
                .offset(x = Dp(offsetAnim))
                .background(Color.White)
                .zIndex(1f)
        )
    }
}

@Composable
private fun ProductCard(producto: ProductoUi, onClick: () -> Unit) {
    val ctx = LocalContext.current

    // resolvemos el nombre del drawable si existe, sino mostramos emoji
    val drawableName = when (producto.categoria) {
        "polera" -> "ph_polera"
        "chaqueta" -> "ph_chaqueta"
        "pantalon" -> "ph_pantalon"
        "zapatillas" -> "ph_zapatillas"
        "accesorio" -> "ph_accesorio"
        else -> "ph_polera"
    }
    val imgId = remember(drawableName) { ctx.safeDrawableId(drawableName) }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardGray)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(CardGrayElevated),
            contentAlignment = Alignment.Center
        ) {
            if (imgId != 0) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = imgId),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // por si no hay imagen carga un emoji
                val emoji = when (producto.categoria) {
                    "polera" -> "👕"
                    "chaqueta" -> "🧥"
                    "pantalon" -> "👖"
                    "zapatillas" -> "👟"
                    else -> "🛍️"
                }
                Text(text = emoji, fontSize = 56.sp, color = TextPrimary)
            }
        }

        Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Text(
                text = producto.nombre,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = producto.precio,
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

// --------------------------------------------------------
// helper: obtener id de drawable por nombre, 0 si no existe
// esto evita errores de compilacion cuando aun no subes las imagenes
// --------------------------------------------------------
private fun Resources.safeGetIdentifier(name: String, defType: String, defPackage: String): Int {
    return try {
        getIdentifier(name, defType, defPackage)
    } catch (_: Exception) {
        0
    }
}

private fun android.content.Context.safeDrawableId(name: String): Int {
    return resources.safeGetIdentifier(name, "drawable", packageName)
}
