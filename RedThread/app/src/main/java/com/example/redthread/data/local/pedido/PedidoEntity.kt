package com.example.redthread.data.local.pedido

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Pedido almacenada en la base de datos local (Room)
 * Compatible con el flujo de Cliente → Checkout → Despachador.
 */
@Entity(tableName = "pedido")
data class PedidoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // --- DATOS GENERALES ---
    val usuario: String,                 // nombre o identificador del cliente
    val direccion: String,               // dirección de entrega
    val total: Long,                     // total del pedido
    val productos: String,               // lista de productos (en formato JSON o texto simple)
    val fecha: Long = System.currentTimeMillis(), // timestamp del pedido

    // --- DATOS DE DESPACHO ---
    val estado: String = "pendiente",    // pendiente / por_entregar / retorno
    val fotoEvidencia: String? = null,   // ruta o URI de la foto tomada por el despachador
    val devuelto: Boolean = false,       // si el pedido fue devuelto
    val motivoDevolucion: String = "",   // texto con la razón de devolución
    val entregado: Boolean = false       // true si ya fue entregado
)
