package com.example.redthread.ui.screen

import androidx.compose.foundation.background                 // Fondo
import androidx.compose.foundation.layout.*                   // Box/Column/Row/Spacer
import androidx.compose.foundation.shape.RoundedCornerShape   // Bordes redondeados
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons                  // Íconos Material
import androidx.compose.material.icons.filled.Visibility      // Ícono mostrar contraseña
import androidx.compose.material.icons.filled.VisibilityOff   // Ícono ocultar contraseña
import androidx.compose.material.icons.outlined.Email         // Ícono email
import androidx.compose.material.icons.outlined.Lock          // Ícono candado
import androidx.compose.material3.*                           // Material 3
import androidx.compose.runtime.*                             // remember y Composable
import androidx.compose.ui.Alignment                          // Alineaciones
import androidx.compose.ui.Modifier                           // Modificador
import androidx.compose.ui.graphics.Brush                     // Degradados
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*                       // KeyboardOptions/Types/Transformations
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp                            // DPs
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Observa StateFlow con lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel         // Obtiene ViewModel
import com.example.redthread.ui.viewmodel.AuthViewModel       // Nuestro ViewModel

// color/accent sugerido para RedThread (rojo elegante)
private val RT_Red = Color(0xFFE11D2E)
// degradado oscuro para el fondo
private val RT_Gradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0F0F11), Color(0xFF1A1B20))
)
// contorno suave de la tarjeta
private val CardShape = RoundedCornerShape(18.dp)

// helper para logo opcional
private fun android.content.Context.safeDrawableId(name: String): Int =
    resources.getIdentifier(name, "drawable", packageName).let { if (it == 0) 0 else it }


//1 Lo primero que creamos en el archivo
@Composable                                                  // Pantalla Login conectada al VM
fun LoginScreenVm(
    vm: AuthViewModel,                            // MOD: recibimos el VM desde NavGraph
    onLoginOkNavigateHome: () -> Unit,                       // Navega a Home cuando el login es exitoso
    onGoRegister: () -> Unit                                 // Navega a Registro
) {

    val state by vm.login.collectAsStateWithLifecycle()      // Observa el StateFlow en tiempo real

    if (state.success) {                                     // Si login fue exitoso…
        vm.clearLoginResult()                                // Limpia banderas
        onLoginOkNavigateHome()                              // Navega a Home
    }

    LoginScreen(                                             // Delegamos a UI presentacional
        email = state.email,                                 // Valor de email
        pass = state.pass,                                   // Valor de password
        emailError = state.emailError,                       // Error de email
        passError = state.passError,                         // (Opcional) error de pass en login
        canSubmit = state.canSubmit,                         // Habilitar botón
        isSubmitting = state.isSubmitting,                   // Loading
        errorMsg = state.errorMsg,                           // Error global
        onEmailChange = vm::onLoginEmailChange,              // Handler email
        onPassChange = vm::onLoginPassChange,                // Handler pass
        onSubmit = vm::submitLogin,                          // Acción enviar
        onGoRegister = onGoRegister                          // Ir a Registro
    )
}


//2 modificamos la funcion principal haciendo private y agregando variable y elementos dle fiormulario
@Composable // Pantalla Login (solo navegación, sin formularios)
private fun LoginScreen(
    //3 Modificamos estos parametros
    email: String,                                           // Campo email
    pass: String,                                            // Campo contraseña
    emailError: String?,                                     // Error de email
    passError: String?,                                      // Error de password (opcional)
    canSubmit: Boolean,                                      // Habilitar botón
    isSubmitting: Boolean,                                   // Flag loading
    errorMsg: String?,                                       // Error global (credenciales)
    onEmailChange: (String) -> Unit,                         // Handler cambio email
    onPassChange: (String) -> Unit,                          // Handler cambio password
    onSubmit: () -> Unit,                                    // Acción enviar
    onGoRegister: () -> Unit                                 // Acción ir a registro
) {
    val ctx = LocalContext.current
    val logoId = remember { ctx.safeDrawableId("logo_redthread") } // Logo opcional (si no existe, no se muestra)

    val bg = RT_Gradient // Fondo degradado oscuro
    //4 Agregamos la siguiente linea
    var showPass by remember { mutableStateOf(false) }        // Estado local para mostrar/ocultar contraseña

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .background(bg) // Fondo degradado
            .padding(18.dp), // Margen externo
        contentAlignment = Alignment.Center // Centro
    ) {
        // ----- Card central con sombra suave -----
        Surface(
            shape = CardShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), // sutil translucidez
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                //5 Anexamos el modificador
                modifier = Modifier
                    .fillMaxWidth()              // Ancho completo
                    .padding(22.dp),             // Padding interno
                horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
            ) {
                // ----- Header con logo + título -----
                if (logoId != 0) {
                    Icon(
                        painter = painterResource(id = logoId),
                        contentDescription = "RedThread",
                        tint = Color.Unspecified,                        // mostramos el PNG tal cual
                        modifier = Modifier
                            .height(56.dp)
                            .padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Inicia sesión para continuar",
                    textAlign = TextAlign.Center, // Alineación centrada
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Spacer(Modifier.height(20.dp)) // Separación

                // ---------- EMAIL ----------
                OutlinedTextField(
                    value = email,                               // Valor actual
                    onValueChange = onEmailChange,               // Notifica VM (valida email)
                    label = { Text("Email") },                   // Etiqueta
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) }, // Ícono
                    singleLine = true,                           // Una línea
                    isError = emailError != null,                // Marca error si corresponde
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email        // Teclado de email
                    ),
                    modifier = Modifier.fillMaxWidth()           // Ancho completo
                )
                if (emailError != null) {                        // Muestra mensaje si hay error
                    Text(
                        emailError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(12.dp))                    // Espacio

                // ---------- PASSWORD (oculta por defecto) ----------
                OutlinedTextField(
                    value = pass,                                // Valor actual
                    onValueChange = onPassChange,                // Notifica VM
                    label = { Text("Contraseña") },              // Etiqueta
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) }, // Ícono
                    singleLine = true,                           // Una línea
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Toggle mostrar/ocultar
                    trailingIcon = {                             // Ícono para alternar visibilidad
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    isError = passError != null,                 // (Opcional) marcar error
                    modifier = Modifier.fillMaxWidth()           // Ancho completo
                )
                if (passError != null) {                         // (Opcional) mostrar error
                    Text(
                        passError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(18.dp))                   // Espacio

                // ---------- BOTÓN ENTRAR ----------
                Button(
                    onClick = onSubmit,                          // Envía login
                    enabled = canSubmit && !isSubmitting,        // Solo si válido y no cargando
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RT_Red,                 // Rojo RedThread
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()                          // Ancho completo
                        .height(48.dp)
                ) {
                    if (isSubmitting) {                          // UI de carga
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Validando...")
                    } else {
                        Text("Entrar", fontWeight = FontWeight.SemiBold)
                    }
                }

                if (errorMsg != null) {                          // Error global (credenciales)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(14.dp))                   // Espacio

                // ---------- BOTÓN IR A REGISTRO ----------
                OutlinedButton(
                    onClick = onGoRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    )
                ) {
                    Text("Crear cuenta", fontWeight = FontWeight.Medium)
                }

                // fin modificacion de formulario
            }
        }
    }
}
