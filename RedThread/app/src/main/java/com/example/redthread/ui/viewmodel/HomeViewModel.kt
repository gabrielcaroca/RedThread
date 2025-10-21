package com.example.redthread.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.redthread.ui.screen.Filtro
import com.example.redthread.ui.screen.ProductoUi

// --------------------------------------------------------
// home viewmodel con datos de ejemplo tipados como en la ui
// usa el mismo modelo de productoUi del homeScreen
// --------------------------------------------------------
class HomeViewModel : ViewModel() {

    // estado interno mutable con la lista de productos
    private val _productos = MutableStateFlow<List<ProductoUi>>(emptyList())

    // exposicion inmutable para la ui
    val productos: StateFlow<List<ProductoUi>> = _productos.asStateFlow()

    init {
        // carga de datos de ejemplo
        _productos.value = listOf(
            ProductoUi(
                id = 1,
                nombre = "polera basica",
                precio = "$20.000",
                categoria = "polera",
                target = Filtro.TODOS
            ),
            ProductoUi(
                id = 2,
                nombre = "hoodie arena",
                precio = "$50.000",
                categoria = "chaqueta",
                target = Filtro.MUJERES
            ),
            ProductoUi(
                id = 3,
                nombre = "jeans slim",
                precio = "$40.000",
                categoria = "pantalon",
                target = Filtro.HOMBRES
            ),
            ProductoUi(
                id = 4,
                nombre = "zapatillas urban",
                precio = "$60.000",
                categoria = "zapatillas",
                target = Filtro.TODOS
            )
        )
    }

    // si luego quieres filtrar desde aqui, puedes agregar una funcion:
    // fun setProductos(list: List<ProductoUi>) { _productos.value = list }
}
