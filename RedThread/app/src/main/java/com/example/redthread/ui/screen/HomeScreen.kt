package com.example.redthread.ui.screen

import android.content.res.Resources
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.animation.Crossfade
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.CardGray
import com.example.redthread.ui.theme.CardGrayElevated
import com.example.redthread.ui.theme.TextPrimary
import com.example.redthread.ui.theme.TextSecondary
import com.example.redthread.ui.viewmodel.HomeViewModel

enum class Filtro { TODOS, HOMBRES, MUJERES }

data class ProductoUi(
    val id: Int,
    val nombre: String,
    val precio: String,
    val categoria: String,
    val target: Filtro = Filtro.TODOS
)

/* =========================
   Home
   ========================= */
@Composable
fun HomeScreen(
    onProductoClick: (ProductoUi) -> Unit = {},
    onCarritoClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    var filtro by remember { mutableStateOf(Filtro.TODOS) }
    val productos by viewModel.productos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(top = 0.dp)
    ) {
        TabsAnimated(
            selected = filtro,
            onSelect = { filtro = it }
        )

        Spacer(Modifier.height(8.dp))

        AnimatedProductGrid(
            filtro = filtro,
            productos = productos,
            onProductoClick = onProductoClick
        )
    }
}

/* =========================
   Grid animado con transición lateral
   ========================= */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnimatedProductGrid(
    filtro: Filtro,
    productos: List<ProductoUi>,
    onProductoClick: (ProductoUi) -> Unit
) {
    fun Filtro.idx(): Int = when (this) {
        Filtro.TODOS -> 0
        Filtro.HOMBRES -> 1
        Filtro.MUJERES -> 2
    }

    AnimatedContent(
        targetState = filtro,
        transitionSpec = {
            val goingRight = targetState.idx() > initialState.idx()

            val slideIn = slideInHorizontally(
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) { full -> if (goingRight) +full else -full }

            val slideOut = slideOutHorizontally(
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) { full -> if (goingRight) -full else +full }

            (slideIn + fadeIn(tween(250))) togetherWith (slideOut + fadeOut(tween(200)))
        },
        label = "gridTransition"
    ) { filtroActual ->
        val filtrados = remember(productos, filtroActual) {
            productos.filter { filtroActual == Filtro.TODOS || it.target == filtroActual }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(filtrados, key = { _, it -> it.id }) { index, p ->
                ProductCard(
                    producto = p,
                    onClick = { onProductoClick(p) },
                    // ✅ Nuevo API en Compose 1.7+: animateItem()
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(220),
                        fadeOutSpec = tween(180),
                        placementSpec = tween(
                            durationMillis = 300 + (index % 6) * 20,
                            easing = FastOutSlowInEasing
                        )
                    )
                )
            }
        }
    }
}

/* =========================
   Tabs (como los tenías)
   ========================= */
@Composable
private fun TabsAnimated(
    selected: Filtro,
    onSelect: (Filtro) -> Unit
) {
    val items = listOf(
        Filtro.TODOS to "Principal",
        Filtro.HOMBRES to "Hombres",
        Filtro.MUJERES to "Mujeres"
    )

    val tabWidth: Dp = 100.dp
    val selectedIndex = items.indexOfFirst { it.first == selected }.coerceAtLeast(0)

    val targetOffset = (selectedIndex * tabWidth.value)
    val offsetAnim by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "tabOffset"
    )

    val widthAnim by androidx.compose.animation.core.animateDpAsState(
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

        Box(
            Modifier
                .padding(horizontal = 20.dp)
                .height(2.dp)
                .width(widthAnim)
                .offset(x = offsetAnim.dp)
                .background(Color.White)
                .zIndex(1f)
        )
    }
}

/* =========================
   Card producto
   ========================= */
@Composable
private fun ProductCard(
    producto: ProductoUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
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
        modifier = modifier
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
                Image(
                    painter = painterResource(id = imgId),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
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

/* =========================
   helpers
   ========================= */
private fun Resources.safeGetIdentifier(name: String, defType: String, defPackage: String): Int {
    return try { getIdentifier(name, defType, defPackage) } catch (_: Exception) { 0 }
}
private fun android.content.Context.safeDrawableId(name: String): Int {
    return resources.safeGetIdentifier(name, "drawable", packageName)
}
