package com.ruben.zoodiacochino.iu.pantallas

// Importaciones necesarias para UI, estado, Firebase, recursos y utilidades de fecha.
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Para acceder a recursos.
import androidx.compose.ui.res.painterResource // Para cargar imágenes.
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Composable que representa la pantalla de resultados y perfil del usuario (Ventana3).
 * Muestra el nombre, edad, signo zodiacal (con imagen) y la calificación obtenida en el examen.
 * Los datos del usuario se cargan desde Firebase Firestore.
 *
 * @param navController Controlador para gestionar la navegación (no se usa activamente para navegar desde aquí).
 * @param userId ID del usuario cuyos datos se van a mostrar.
 * @param calificacion Calificación obtenida por el usuario en el examen.
 */
@Composable
fun Ventana3(navController: NavController, userId: String, calificacion: Int) {
    // Instancia de FirebaseFirestore.
    val db = FirebaseFirestore.getInstance()

    // Estados para almacenar la información del usuario y el estado de carga/error.
    var nombreCompleto by remember { mutableStateOf<String?>(null) }
    var edad by remember { mutableStateOf<Int?>(null) }
    var signo by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Indica si los datos se están cargando.
    var errorMessage by remember { mutableStateOf<String?>(null) } // Almacena mensajes de error.

    // Contexto local para acceder a recursos como drawables.
    val contexto = LocalContext.current

    // Efecto lanzado cuando `userId` cambia, para cargar los datos del usuario.
    LaunchedEffect(userId) {
        isLoading = true
        errorMessage = null // Resetea el mensaje de error.
        // Resetea los datos del usuario.
        nombreCompleto = null
        edad = null
        signo = null

        if (userId.isNotEmpty()) {
            // Obtiene el documento del usuario desde la colección "usuarios" en Firestore.
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Extrae los datos del documento.
                        val nombre = documentSnapshot.getString("nombre") ?: ""
                        val apP = documentSnapshot.getString("apellidoP") ?: ""
                        val apM = documentSnapshot.getString("apellidoM") ?: ""
                        val anio = (documentSnapshot.getLong("anio")?.toInt())
                        val mes = (documentSnapshot.getLong("mes")?.toInt())
                        val dia = (documentSnapshot.getLong("dia")?.toInt())

                        if (anio != null && mes != null && dia != null) {
                            // Construye el nombre completo y calcula edad y signo.
                            nombreCompleto = "$nombre $apP $apM".trim().replace("  ", " ")
                            if (nombreCompleto!!.isBlank()) nombreCompleto = "Usuario Desconocido"

                            edad = calcularEdad(dia, mes, anio)
                            signo = calcularSigno(anio)
                        } else {
                            // Manejo de error si los datos de fecha son incompletos.
                            Log.e("Ventana3", "Datos de fecha incompletos para el usuario: $userId. Año: $anio, Mes: $mes, Día: $dia")
                            errorMessage = "No se pudieron cargar los datos de fecha del usuario."
                        }
                    } else {
                        // Manejo de error si el usuario no se encuentra.
                        Log.e("Ventana3", "No se encontró el documento del usuario con ID: $userId")
                        errorMessage = "Usuario no encontrado."
                    }
                    isLoading = false // Finaliza el estado de carga.
                }
                .addOnFailureListener { exception ->
                    // Manejo de error si falla la obtención de datos.
                    Log.e("Ventana3", "Error al obtener usuario con ID $userId: ${exception.message}", exception)
                    errorMessage = "Error al cargar los datos del usuario."
                    isLoading = false
                }
        } else {
            // Manejo de caso donde el userId está vacío.
            Log.w("Ventana3", "userId está vacío, no se puede cargar la información del usuario.")
            errorMessage = "No se proporcionó un ID de usuario válido."
            isLoading = false
        }
    }

    // Estructura principal de la UI usando una Columna.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Muestra diferentes UI según el estado (cargando, error, datos cargados).
        when {
            isLoading -> {
                CircularProgressIndicator() // Muestra un indicador de progreso.
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando datos del usuario...")
            }
            errorMessage != null -> {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error) // Muestra el mensaje de error.
            }
            // Si los datos se cargaron correctamente:
            nombreCompleto != null && edad != null && signo != null -> {
                Text("Hola ${nombreCompleto ?: "Usuario"}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tienes ${edad ?: "desconocida"} años y tu signo zodiacal es:")
                Spacer(modifier = Modifier.height(4.dp))
                Text(signo?.replaceFirstChar { it.uppercase() } ?: "Desconocido", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))
                // Prepara el nombre del signo para buscar el recurso drawable.
                val nombreSignoParaRecurso = signo?.lowercase()?.filter { it.isLetter() } ?: ""
                if (nombreSignoParaRecurso.isNotEmpty()) {
                    // Obtiene el ID del recurso drawable basado en el nombre del signo.
                    val imagenId = contexto.resources.getIdentifier(nombreSignoParaRecurso, "drawable", contexto.packageName)
                    if (imagenId != 0) {
                        Image( // Muestra la imagen del signo zodiacal.
                            painter = painterResource(id = imagenId),
                            contentDescription = "Imagen del signo $signo",
                            modifier = Modifier.size(120.dp)
                        )
                    } else {
                        Text("Imagen para '$signo' no encontrada (recurso: '$nombreSignoParaRecurso').")
                    }
                } else {
                    Text("Nombre de signo inválido para buscar imagen.")
                }

                Spacer(modifier = Modifier.height(24.dp))
                // Muestra la calificación obtenida.
                Text("Calificación: $calificacion / 6", style = MaterialTheme.typography.titleMedium)
            }
            // Caso por defecto si los datos no se pudieron cargar y no hay error específico.
            else -> {
                Text("No se pudo cargar la información del usuario. Verifica la conexión o el ID del usuario.")
            }
        }
    }
}

/**
 * Calcula la edad de una persona a partir de su fecha de nacimiento.
 *
 * @param dia Día de nacimiento.
 * @param mes Mes de nacimiento (1-12).
 * @param anio Año de nacimiento.
 * @return La edad calculada.
 */
fun calcularEdad(dia: Int, mes: Int, anio: Int): Int {
    val hoy = Calendar.getInstance()
    val nacimiento = GregorianCalendar(anio, mes - 1, dia) // Mes en Calendar es 0-indexado.

    var edadCalculada = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

    // Ajusta la edad si aún no ha cumplido años este año.
    if (hoy.get(Calendar.MONTH) < nacimiento.get(Calendar.MONTH) ||
        (hoy.get(Calendar.MONTH) == nacimiento.get(Calendar.MONTH) &&
                hoy.get(Calendar.DAY_OF_MONTH) < nacimiento.get(Calendar.DAY_OF_MONTH))
    ) {
        edadCalculada--
    }
    return edadCalculada
}

/**
 * Calcula el signo del zodiaco chino basado en el año de nacimiento.
 *
 * @param anioNacimiento Año de nacimiento.
 * @return El nombre del signo zodiacal chino en minúsculas.
 */
fun calcularSigno(anioNacimiento: Int): String {
    val signos = listOf(
        "rata", "buey", "tigre", "conejo", "dragon", "serpiente",
        "caballo", "cabra", "mono", "gallo", "perro", "cerdo"
    )
    // El ciclo del zodiaco chino se basa en un ciclo de 12 años, comenzando desde 1900 (Rata).
    var indice = (anioNacimiento - 1900) % 12
    if (indice < 0) { // Ajuste para años anteriores a 1900.
        indice = (indice + 12) % 12
    }
    return signos[indice]
}