package com.ruben.zoodiacochino.iu.pantallas

// Importaciones necesarias para UI, estado, navegación y Firebase
import android.util.Log // No se usa explícitamente, pero es común para debugging.
// import android.widget.Button // Importación de View System, no usada aquí.
import androidx.compose.foundation.layout.*
// import androidx.compose.material.* // Importación de Material (versión anterior), no usada si se usa Material3.
import androidx.compose.material3.Button // Botón de Material Design 3.
import androidx.compose.material3.MaterialTheme // Tema de Material Design 3.
// import androidx.compose.material3.OutlinedTextField // No se usa en este Composable.
import androidx.compose.material3.RadioButton // RadioButton de Material Design 3.
import androidx.compose.material3.Text // Componente Text de Material Design 3.
import androidx.compose.runtime.* // Para `remember` y `mutableStateListOf`.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // Para la navegación.
import com.google.firebase.firestore.FirebaseFirestore // Para interactuar con Firestore.

/**
 * Composable que representa la pantalla del examen (Ventana2).
 * Muestra una lista de preguntas con opciones de respuesta.
 * Guarda los resultados en Firebase Firestore y navega a la pantalla de resultados.
 *
 * @param navController Controlador para gestionar la navegación a otras pantallas.
 * @param userId ID del usuario que está realizando el examen, recibido de la pantalla anterior.
 */
@Composable
fun Ventana2(navController: NavController, userId: String) {
    // Instancia de FirebaseFirestore para operaciones de base de datos.
    val db = FirebaseFirestore.getInstance()

    // Lista de preguntas. Cada elemento es un Triple: (texto de la pregunta, lista de opciones, índice de la respuesta correcta).
    val preguntas = listOf(
        Triple("¿Cuál es la suma de 2 + 2?", listOf("8", "6", "4", "3"), 2),
        Triple("¿Cuál es la capital de Francia?", listOf("Londres", "Madrid", "París", "Roma"), 2),
        Triple("¿Qué color resulta de mezclar azul y amarillo?", listOf("Verde", "Naranja", "Rosa", "Rojo"), 0),
        Triple("¿Cuántos días tiene una semana?", listOf("5", "6", "7", "8"), 2),
        Triple("¿Quién escribió Don Quijote?", listOf("García Márquez", "Cervantes", "Shakespeare", "Neruda"), 1),
        Triple("¿Cuál es el planeta más grande?", listOf("Marte", "Venus", "Júpiter", "Tierra"), 2)
    )

    // Lista mutable para almacenar las respuestas seleccionadas por el usuario (índice de la opción).
    // Se inicializa con `null` para cada pregunta, indicando que no se ha seleccionado ninguna respuesta.
    val respuestasUsuario = remember { mutableStateListOf<Int?>(null, null, null, null, null, null) }

    // Estructura principal de la UI usando una Columna.
    Column(Modifier.padding(16.dp)) {
        Text("Examen", style = MaterialTheme.typography.titleLarge) // Título de la pantalla.

        // Itera sobre la lista de preguntas para mostrar cada una.
        preguntas.forEachIndexed { index, (pregunta, opciones, _) ->
            Text("${index + 1}. $pregunta") // Muestra el número y el texto de la pregunta.

            // Fila para mostrar las opciones de respuesta horizontalmente.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly // Distribuye el espacio entre las opciones.
            ) {
                // Itera sobre las opciones de la pregunta actual.
                opciones.forEachIndexed { i, opcion ->
                    // Fila para cada RadioButton y su texto.
                    Row(
                        modifier = Modifier.weight(1f), // Asigna el mismo espacio a cada opción.
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = respuestasUsuario[index] == i, // Marca como seleccionado si es la respuesta actual del usuario.
                            onClick = { respuestasUsuario[index] = i } // Actualiza la respuesta del usuario al hacer clic.
                        )
                        Text(opcion) // Muestra el texto de la opción.
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre preguntas.
        }


        // Botón para finalizar el examen.
        Button(onClick = {
            // Calcula el número de respuestas correctas.
            val aciertos = preguntas.indices.count {
                respuestasUsuario[it] == preguntas[it].third // Compara la respuesta del usuario con la respuesta correcta.
            }

            // Guarda los resultados (ID del usuario y número de aciertos) en la colección "resultados" de Firestore.
            db.collection("resultados")
                .add(mapOf("userId" to userId, "aciertos" to aciertos))
                .addOnSuccessListener {
                    // Si se guarda con éxito, navega a Ventana3 pasando el userId y el número de aciertos.
                    navController.navigate("ventana3/$userId/$aciertos")
                }
            // .addOnFailureListener { /* Opcional: Manejar error al guardar */ }
        }) {
            Text("Terminar")
        }
    }
}