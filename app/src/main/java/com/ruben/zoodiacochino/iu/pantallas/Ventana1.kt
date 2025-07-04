package com.ruben.zoodiacochino.iu.pantallas

// Importaciones necesarias para UI, estado, navegación y Firebase
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Composable que representa la pantalla del formulario de registro (Ventana1).
 * Permite al usuario ingresar sus datos personales y los guarda en Firebase Firestore.
 *
 * @param navController Controlador para gestionar la navegación a otras pantallas.
 */
@Composable
fun Ventana1(navController: NavController) {
    // Instancia de FirebaseFirestore para operaciones de base de datos.
    val db = FirebaseFirestore.getInstance()

    // Estados para almacenar los valores de los campos del formulario.
    var nombre by remember { mutableStateOf("") }
    var apellidoP by remember { mutableStateOf("") }
    var apellidoM by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Masculino") }

    /**
     * Lambda para limpiar todos los campos del formulario a sus valores por defecto.
     */
    val limpiarCampos = {
        nombre = ""
        apellidoP = ""
        apellidoM = ""
        dia = ""
        mes = ""
        anio = ""
        sexo = "Masculino"
    }

    // Estructura principal de la UI usando una Columna.
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Formulario de Registro", style = MaterialTheme.typography.titleLarge)

        // Campos de texto para la entrada de datos del usuario.
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = apellidoP, onValueChange = { apellidoP = it }, label = { Text("Apellido Paterno") })
        // ... (otros OutlinedTextFields para apellidoM, dia, mes, anio)

        Text("Sexo:")
        // Selección de sexo mediante RadioButtons.
        Row {
            RadioButton(selected = sexo == "Masculino", onClick = { sexo = "Masculino" })
            Text("Masculino")
            // ... (RadioButton para Femenino)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción: Limpiar y Siguiente.
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            // Botón para ejecutar la función limpiarCampos.
            Button (onClick = limpiarCampos, modifier = Modifier.padding(8.dp)) {
                Text("Limpiar")
            }

            // Botón para guardar los datos en Firestore y navegar a la siguiente pantalla.
            Button(onClick = {
                // Prepara los datos del usuario para Firestore.
                val usuario = hashMapOf(
                    "nombre" to nombre,
                    "apellidoP" to apellidoP,
                    "apellidoM" to apellidoM,
                    "dia" to dia.toIntOrNull(),
                    "mes" to mes.toIntOrNull(),
                    "anio" to anio.toIntOrNull(),
                    "sexo" to sexo
                )

                // Guarda el usuario en la colección "usuarios" de Firestore.
                db.collection("usuarios")
                    .add(usuario)
                    .addOnSuccessListener { document ->
                        Log.d("Ventana1", "Usuario guardado con ID: ${document.id}")
                        // Navega a Ventana2 pasando el ID del nuevo usuario.
                        navController.navigate("ventana2/${document.id}")
                    }
                    .addOnFailureListener {
                        Log.e("Ventana1", "Error al guardar: ${it.message}")
                    }
            }) {
                Text("Siguiente")
            }
        }
    }
}