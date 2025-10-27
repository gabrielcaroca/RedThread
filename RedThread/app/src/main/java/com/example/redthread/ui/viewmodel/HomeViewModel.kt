package com.example.redthread.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.redthread.ui.screen.Filtro
import com.example.redthread.ui.screen.ProductoUi
import com.example.redthread.R

/**
 * ViewModel central de Home:
 * - Exponde la lista ligera para la grilla (ProductoUi).
 * - Mantiene un índice de detalles por id (ProductoDetalle) con tallas, colores e imagen.
 * - Provee getDetalle(id) para que la pantalla de detalle lea todo desde aquí.
 */
class HomeViewModel : ViewModel() {

    // -----------------------------
    // Modelo de detalle
    // -----------------------------
    data class ProductoDetalle(
        val id: Int,
        val nombre: String,
        val precio: String,
        val categoria: String,
        val tallas: List<String>,
        val colores: List<String>,
        val imagenRes: Int       // referencia directa a R.drawable (sin getIdentifier)
    )

    // -----------------------------
    // Estado para la grilla (UI)
    // -----------------------------
    private val _productos = MutableStateFlow<List<ProductoUi>>(emptyList())
    val productos: StateFlow<List<ProductoUi>> = _productos.asStateFlow()

    // Índice de detalles por id (solo lectura pública vía método)
    private val detalles: MutableMap<Int, ProductoDetalle> = linkedMapOf()

    init {
        seed()
    }

    // -----------------------------
    // API pública
    // -----------------------------
    fun getDetalle(id: Int): ProductoDetalle? = detalles[id]

    // (Opcional) por si quieres exponer tallas/colores directo
    fun getTallasDisponibles(id: Int): List<String> = detalles[id]?.tallas.orEmpty()
    fun getColoresDisponibles(id: Int): List<String> = detalles[id]?.colores.orEmpty()

    // -----------------------------
    // Datos de ejemplo (puedes conectar a SQLite más tarde)
    // -----------------------------
    private fun seed() {
        // Catálogo de ejemplo con imagen por categoría
        val data = listOf(
            ProductoDetalle(
                id = 1,
                nombre = "Polera básica",
                precio = "$20.000",
                categoria = "polera",
                tallas = listOf("XS", "S", "M", "L", "XL"),
                colores = listOf("Negro", "Blanco", "Rojo"),
                imagenRes = R.drawable.ph_polera
            ),
            ProductoDetalle(
                id = 2,
                nombre = "Hoodie arena",
                precio = "$50.000",
                categoria = "chaqueta",
                tallas = listOf("S", "M", "L"),
                colores = listOf("Arena", "Negro", "Gris"),
                imagenRes = R.drawable.ph_chaqueta
            ),
            ProductoDetalle(
                id = 3,
                nombre = "Jeans slim",
                precio = "$40.000",
                categoria = "pantalon",
                tallas = listOf("28", "30", "32", "34", "36"),
                colores = listOf("Azul", "Negro"),
                imagenRes = R.drawable.ph_pantalon
            ),
            ProductoDetalle(
                id = 4,
                nombre = "Zapatillas urban",
                precio = "$60.000",
                categoria = "zapatillas",
                tallas = listOf("38", "39", "40", "41", "42", "43"),
                colores = listOf("Blanco", "Negro"),
                imagenRes = R.drawable.ph_zapatillas
            ),
            ProductoDetalle(
                id = 5,
                nombre = "Polera oversize",
                precio = "$24.990",
                categoria = "polera",
                tallas = listOf("S", "M", "L", "XL"),
                colores = listOf("Negro", "Gris"),
                imagenRes = R.drawable.ph_polera
            ),
            ProductoDetalle(
                id = 6,
                nombre = "Chaqueta bomber",
                precio = "$54.990",
                categoria = "chaqueta",
                tallas = listOf("M", "L", "XL"),
                colores = listOf("Negro", "Verde"),
                imagenRes = R.drawable.ph_chaqueta
            ),
            ProductoDetalle(
                id = 7,
                nombre = "Cinturón cuero",
                precio = "$14.990",
                categoria = "accesorio",
                tallas = listOf("S", "M", "L"),
                colores = listOf("Café", "Negro"),
                imagenRes = R.drawable.ph_accesorio
            ),
            ProductoDetalle(
                id = 8,
                nombre = "Gorro beanie",
                precio = "$9.990",
                categoria = "accesorio",
                tallas = listOf("Única"),
                colores = listOf("Negro", "Gris", "Azul"),
                imagenRes = R.drawable.ph_accesorio
            )
        )

        // Relleno índice de detalles
        detalles.clear()
        data.forEach { detalles[it.id] = it }

        // Proyección ligera para la grilla (usa tu ProductoUi original)
        _productos.value = data.map {
            ProductoUi(
                id = it.id,
                nombre = it.nombre,
                precio = it.precio,
                categoria = it.categoria,
                // Puedes mapear target como prefieras para el filtro inicial
                target = when (it.categoria) {
                    "pantalon" -> Filtro.HOMBRES
                    "chaqueta" -> Filtro.MUJERES
                    else -> Filtro.TODOS
                }
            )
        }
    }
}
