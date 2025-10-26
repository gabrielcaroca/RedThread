package com.example.redthread.data.repository

import com.example.redthread.data.local.user.UserDao
import com.example.redthread.data.local.user.UserEntity
import com.example.redthread.domain.enums.UserRole

/**
 * Repositorio de usuarios.
 * Se encarga de la lógica de negocio para login y registro usando Room (UserDao).
 */
class UserRepository(
    private val userDao: UserDao
) {

    // =====================
    // LOGIN
    // =====================
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    // =====================
    // REGISTRO
    // =====================
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Long> {
        // Verificar si el correo ya está en uso
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }

        // Crear el nuevo usuario con rol por defecto "USUARIO"
        val newUser = UserEntity(
            name = name,
            email = email,
            phone = phone,
            password = password,
            role = UserRole.USUARIO // 👈 se asigna el rol por defecto
        )

        // Insertar el nuevo usuario y devolver el ID generado
        val id = userDao.insert(newUser)
        return Result.success(id)
    }
}
