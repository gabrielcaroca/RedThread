package com.example.redthread.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.redthread.data.local.user.UserDao
import com.example.redthread.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Define la base de datos local con Room
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true // Mantener true para inspeccionar el esquema
)
abstract class AppDatabase : RoomDatabase() {

    // Exponer el DAO de usuarios
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "redthread.db"

        // Singleton para obtener la instancia de la base de datos
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Callback de creación inicial
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()

                                // Precarga de usuarios iniciales
                                val seed = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "admin@redthread.cl",
                                        phone = "+56911111111",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Cliente Demo",
                                        email = "cliente@redthread.cl",
                                        phone = "+56922222222",
                                        password = "123456"
                                    )
                                )

                                // Inserta usuarios solo si la tabla está vacía
                                if (dao.count() == 0) {
                                    seed.forEach { dao.insert(it) }
                                }
                            }
                        }
                    })
                    // Recrea la DB si cambias la versión y no defines migraciones
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
