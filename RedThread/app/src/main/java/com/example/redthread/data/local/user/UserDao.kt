package com.example.redthread.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// @Dao define las operaciones permitidas sobre la tabla de usuarios.
@Dao
interface UserDao {

    // Inserta un usuario. Si el ID ya existe, aborta la inserción.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Devuelve un usuario por su email, o null si no existe.
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    // Devuelve la cantidad total de usuarios registrados.
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // Devuelve la lista completa de usuarios (para administración o debug).
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

}
