package com.example.redthread.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.redthread.data.local.address.AddressDao
import com.example.redthread.data.local.address.AddressEntity
import com.example.redthread.data.local.pedido.PedidoDao
import com.example.redthread.data.local.pedido.PedidoEntity
import com.example.redthread.data.local.producto.ProductoDao
import com.example.redthread.data.local.producto.ProductoEntity
import com.example.redthread.data.local.ruta.RutaDao
import com.example.redthread.data.local.ruta.RutaEntity
import com.example.redthread.data.local.user.UserDao
import com.example.redthread.data.local.user.UserEntity
import com.example.redthread.domain.enums.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos principal de la aplicación RedThread.
 * Contiene las tablas de usuarios, productos, pedidos, rutas y direcciones.
 * Gestiona automáticamente los datos iniciales (usuarios de ejemplo).
 */
@Database(
    entities = [
        UserEntity::class,
        ProductoEntity::class,
        PedidoEntity::class,
        RutaEntity::class,
        AddressEntity::class
    ],
    version = 7, // 🔹 subimos versión por cambios en PedidoEntity o estructura
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // --- DAOs disponibles ---
    abstract fun userDao(): UserDao
    abstract fun productoDao(): ProductoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun rutaDao(): RutaDao
    abstract fun addressDao(): AddressDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "redthread.db"

        /**
         * Obtiene la instancia única de la base de datos.
         * Si no existe, la crea y realiza una carga inicial con usuarios demo.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // 🔹 Permite recrear las tablas si cambia la estructura (solo durante desarrollo)
                    .fallbackToDestructiveMigration()
                    // 🔹 Crea usuarios de prueba al generar la base por primera vez
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val userDao = getInstance(context).userDao()

                                val seedUsers = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "admin@redthread.cl",
                                        phone = "+56911111111",
                                        password = "Admin123!",
                                        role = UserRole.ADMINISTRADOR
                                    ),
                                    UserEntity(
                                        name = "Cliente Demo",
                                        email = "cliente@redthread.cl",
                                        phone = "+56922222222",
                                        password = "123456",
                                        role = UserRole.USUARIO
                                    ),
                                    UserEntity(
                                        name = "Despachador",
                                        email = "despachador@redthread.cl",
                                        phone = "+569333333333",
                                        password = "Despachador123!",
                                        role = UserRole.DESPACHADOR
                                    )
                                )

                                // Solo inserta si no hay usuarios
                                if (userDao.count() == 0) seedUsers.forEach { userDao.insert(it) }
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
