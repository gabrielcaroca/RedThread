// app/src/main/java/com/example/redthread/MainActivity.kt
package com.example.redthread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.redthread.ui.theme.RedThreadTheme
import com.example.redthread.navigation.Route
import com.example.redthread.navigation.AppNavGraph
import com.example.redthread.ui.rememberAuthViewModel
import com.example.redthread.data.local.database.AppDatabase
import com.example.redthread.data.repository.UserRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RedThreadTheme {
                // Instancia DB (SQLite/Room) y Repo para Auth
                val db = remember { AppDatabase.getInstance(applicationContext) }
                val userDao = remember { db.userDao() }
                val userRepository = remember { UserRepository(userDao) }
                val authVm = rememberAuthViewModel(userRepository)

                val navController = rememberNavController()

                // Monta el NavGraph (centraliza navegación y guards)
                AppNavGraph(
                    navController = navController,
                    authViewModel = authVm
                )
            }
        }
    }
}
