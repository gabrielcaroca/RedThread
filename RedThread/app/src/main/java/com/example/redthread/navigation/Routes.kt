package com.example.redthread.navigation
//Cada uno de los objetos va a representar una pantalla
//Si es necesario agregar mas.
sealed class Route(val path: String) {
    data object Home : Route("home") //1
    data object Login : Route("login") //2
    data object Register : Route("register")//3
    data object Carrito : Route("carro")//4
    data object DetalleProducto : Route("detalle_producto") //5
    data object Perfil : Route("perfil") // 6
    data object Checkout : Route("pago") // 7
    data object VistaModerador : Route("moderador") //8
}

/*
* Rutas principales de la aplicación
* 1. Home = Catálogo general de productos
* 2. Login = Pantalla de inicio de sesión
* 3. Register = Registro de nuevo usuario
* 4. Carrito = Muestra los productos agregados
* 5. DetalleProducto = Información de un producto seleccionado
* 6. Perfil = Datos y configuración del usuario
* 7. Checkout = Confirmación y pago de la compra
* 8. VistaModerador = Pantalla de administrador
*/