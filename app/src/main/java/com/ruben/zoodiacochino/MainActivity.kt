package com.ruben.zoodiacochino

// Importaciones necesarias para la actividad principal, Jetpack Compose y Navegación.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding // Para aplicar padding.
import androidx.compose.material3.Scaffold // Componente Scaffold para la estructura básica de la app Material3.
import androidx.compose.ui.Modifier
import androidx.navigation.NavType // Para definir tipos de argumentos de navegación.
import androidx.navigation.compose.* // Funciones de Navegación para Compose (NavHost, composable, rememberNavController).
import androidx.navigation.navArgument // Para definir argumentos de navegación.
// Importaciones de las diferentes pantallas (Composables) de la aplicación.
import com.ruben.zoodiacochino.iu.pantallas.Ventana1
import com.ruben.zoodiacochino.iu.pantallas.Ventana2
import com.ruben.zoodiacochino.iu.pantallas.Ventana3

/**
 * Actividad principal de la aplicación.
 * Configura el contenido de la UI usando Jetpack Compose y establece el sistema de navegación.
 */
class MainActivity : ComponentActivity() {
    /**
     * Método llamado cuando la actividad es creada por primera vez.
     * Aquí se inicializa la UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llama a la implementación de la superclase.

        // Establece el contenido de la actividad usando Jetpack Compose.
        setContent {
            // Crea y recuerda un NavController. Este controlador es responsable de
            // gestionar la pila de backstack de la aplicación y las operaciones de navegación.
            val navController = rememberNavController()

            // Scaffold proporciona una estructura básica para pantallas Material Design.
            // `innerPadding` se usa para asegurar que el contenido no se superponga con
            // elementos de Scaffold como barras de aplicación o botones flotantes (si se usaran).
            Scaffold { innerPadding ->
                // NavHost es el contenedor donde se muestran los diferentes destinos (pantallas) de navegación.
                NavHost(
                    navController = navController, // El controlador de navegación a usar.
                    startDestination = "ventana1", // La ruta del destino inicial que se mostrará al iniciar.
                    modifier = Modifier.padding(innerPadding) // Aplica el padding interno de Scaffold.
                ) {
                    // Define la primera pantalla (ruta "ventana1").
                    composable("ventana1") {
                        // Muestra el Composable Ventana1, pasándole el navController.
                        Ventana1(navController)
                    }

                    // Define la segunda pantalla (ruta "ventana2/{userId}").
                    // "{userId}" define un argumento de ruta que se pasará a esta pantalla.
                    composable(
                        route = "ventana2/{userId}", // La plantilla de la ruta.
                        arguments = listOf(navArgument("userId") { type = NavType.StringType }) // Define el tipo del argumento "userId".
                    ) { backStackEntry -> // `backStackEntry` contiene los argumentos pasados a esta ruta.
                        // Extrae el argumento "userId" de la pila de backstack.
                        // Proporciona un valor por defecto ("") si el argumento no se encuentra.
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        // Muestra el Composable Ventana2, pasándole el navController y el userId.
                        Ventana2(navController, userId)
                    }

                    // Define la tercera pantalla (ruta "ventana3/{userId}/{calificacion}").
                    // Esta ruta tiene dos argumentos: "userId" y "calificacion".
                    composable(
                        route = "ventana3/{userId}/{calificacion}", // Plantilla de la ruta.
                        arguments = listOf( // Define los tipos de los argumentos.
                            navArgument("userId") { type = NavType.StringType },
                            navArgument("calificacion") { type = NavType.StringType } // Nota: La calificación se pasa como String y luego se convierte.
                        )
                    ) { backStackEntry ->
                        // Extrae el argumento "userId".
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        // Extrae el argumento "calificacion", lo convierte a Int,
                        // o usa 0 si la conversión falla o el argumento no está presente.
                        val calificacion =
                            backStackEntry.arguments?.getString("calificacion")?.toIntOrNull() ?: 0
                        // Muestra el Composable Ventana3, pasándole el navController, userId y calificacion.
                        Ventana3(navController, userId, calificacion)
                    }
                }
            }
        }
    }
}