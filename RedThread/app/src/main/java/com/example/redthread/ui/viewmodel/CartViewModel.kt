package com.example.redthread.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * VM simple en memoria para el carrito.
 * - items: lista de ítems agregados
 * - count: total de unidades (para el badge)
 */
class CartViewModel : ViewModel() {

    data class CartItem(
        val productId: Int,
        val nombre: String,
        val talla: String,
        val color: String,
        val precio: String,
        val cantidad: Int = 1
    )

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count

    fun addToCart(item: CartItem) {
        _items.update { it + item }
        recomputeCount()
    }

    fun clear() {
        _items.value = emptyList()
        _count.value = 0
    }

    private fun recomputeCount() {
        _count.value = _items.value.sumOf { it.cantidad }
    }
}
