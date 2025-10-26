package com.example.redthread.ui.screen

// ========================
// imports basicos
// ========================
import android.content.res.Resources
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat       // <- necesario para la corutina de shimmer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset                 // <- para start/end del brush
import androidx.compose.ui.graphics.Brush
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
import com.example.redthread.ui.theme.Black
import com.example.redthread.ui.theme.CardGray
import com.example.redthread.ui.theme.CardGrayElevated
import com.example.redthread.ui.theme.TextPrimary
import com.example.redthread.ui.theme.TextSecondary
import com.example.redthread.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

// ========================
// modelo de filtro y dto ui
// ========================
enum class Filtro { TODOS, HOMBRES, MUJERES }

data class ProductoUi(
    val id: Int,
    val nombre: String,
    val precio: String,
    val categoria: String,
    val target: Filtro = Filtro.TODOS
)

// ========================
// home principal
// objetivos:
// - mostrar skeletons 3s usando corutinas (sin bloquear hilo ui)
// - shimmer durante la carga
// - luego crossfade a la grilla real
// ========================
@Composable
fun HomeScreen(
    onProductoClick: (ProductoUi) -> Unit = {},
    onCarritoClick: () -> Unit = {},   // no usado aqui, lo dejo para compatibilidad
    onPerfilClick: () -> Unit = {},    // no usado aqui, lo dejo para compatibilidad
    viewModel: HomeViewModel = viewModel()
) {
    // estado de tab
    var filtro by remember { mutableStateOf(Filtro.TODOS) }

    // productos locales del vm
    val productos by viewModel.productos.collectAsState()

    // bandera de carga controlada por corutina
    var isLoading by remember { mutableStateOf(true) }

    // launchedEffect corre en una corutina de compose
    // delay es suspend y no bloquea el hilo principal
    LaunchedEffect(Unit) {
        delay(1000)          // simula carga de 1s
        isLoading = false
    }

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

        // crossfade entre skeletons y contenido real
        Crossfade(targetState = isLoading, label = "homeCrossfadeLoading") { loading ->
            if (loading) {
                SkeletonGrid()
            } else {
                AnimatedProductGrid(
                    filtro = filtro,
                    productos = productos,
                    onProductoClick = onProductoClick
                )
            }
        }
    }
}

// ========================
// grilla real con transicion lateral al cambiar de tab
// ========================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AnimatedProductGrid(
    filtro: Filtro,
    productos: List<ProductoUi>,
    onProductoClick: (ProductoUi) -> Unit
) {
    // helper para decidir direccion del slide
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

        val gridState = rememberLazyGridState()

        LazyVerticalGrid(
            state = gridState,
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
                    // uso animateItemPlacement por compatibilidad amplia
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 300 + (index % 6) * 20,
                            easing = FastOutSlowInEasing
                        )
                    )
                )
            }
        }
    }
}

// ========================
// grid de skeletons (mismo layout que el real)
// ========================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SkeletonGrid(
    skeletonCount: Int = 8
) {
    val placeholders = remember(skeletonCount) { List(skeletonCount) { it } }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(placeholders, key = { it }) { index ->
            SkeletonCard(
                modifier = Modifier.animateItemPlacement(
                    animationSpec = tween(
                        durationMillis = 300 + (index % 6) * 20,
                        easing = FastOutSlowInEasing
                    )
                )
            )
        }
    }
}

// ========================
// shimmer brush para skeletons
// detalle: uso rememberInfiniteTransition + animateFloat
// para desplazar un gradiente lineal horizontal
// ========================
@Composable
private fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color(0xFF2A2A2A),
        Color(0xFF3A3A3A),
        Color(0xFF2A2A2A)
    )

    val transition = rememberInfiniteTransition(label = "skeletonShimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeletonShift"
    )

    // offset horizontal en pixeles logicos
    val startX = 0f + progress * 600f
    val endX = startX + 300f

    // uso offset tipado para evitar problemas de inferencia
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(startX, 0f),
        end = Offset(endX, 0f)
    )
}

// ========================
// card skeleton (mismo alto de imagen y lineas para titulo/precio)
// ========================
@Composable
private fun SkeletonCard(
    modifier: Modifier = Modifier
) {
    val shimmer = rememberShimmerBrush()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(shimmer)
        )
        Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmer)
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmer)
            )
        }
    }
}

// ========================
// tabs con indicador deslizante
// ========================
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
    val offsetAnim by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "tabOffset"
    )

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

// ========================
// card real del producto
// ========================
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2E2E2E))
                )
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

// ========================
// helpers de recursos
// ========================
private fun Resources.safeGetIdentifier(name: String, defType: String, defPackage: String): Int {
    return try { getIdentifier(name, defType, defPackage) } catch (_: Exception) { 0 }
}
private fun android.content.Context.safeDrawableId(name: String): Int {
    return resources.safeGetIdentifier(name, "drawable", packageName)
}
