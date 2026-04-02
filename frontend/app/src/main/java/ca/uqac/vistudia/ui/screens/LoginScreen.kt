package ca.uqac.vistudia.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uqac.vistudia.Models.LoginRequest
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.compose.rememberNavController
import androidx.core.content.edit


@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getString("saved_email", "") ?: ""
        if (saved.isNotEmpty()) {
            email = saved
            rememberMe = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fond_gris)
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(blue_foncee)
        ) {

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text("←", color = blanc, fontSize = 24.sp)
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Connexion",
                    color = blanc,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Bon retour parmi nous !",
                    color = blanc.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }


        // Card formulaire
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = blanc)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = blue_foncee) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Mot de passe
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = blue_foncee) },
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                if (passwordVisible) "Cacher" else "Voir",
                                color = blue_clair,
                                fontSize = 12.sp
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Se souvenir de moi
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = orange)
                    )
                    Text("Se souvenir de moi", color = gris_fonce, fontSize = 14.sp)
                }

                // Message erreur
                if (errorMsg.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            errorMsg,
                            color = Color(0xFFE53935),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Bouton connexion
                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            errorMsg = "Veuillez remplir tous les champs"
                            return@Button
                        }
                        loading = true
                        errorMsg = ""
                        login(context, email, password, rememberMe, navController) {
                            errorMsg = it
                            loading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orange),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = blanc,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Se connecter", color = blanc, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Mot de passe oublié
                TextButton(
                    onClick = { navController.navigate("recovery") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Mot de passe oublié ?", color = blue_clair, fontSize = 13.sp)
                }

                HorizontalDivider(color = fond_gris)

                // Lien inscription
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pas encore de compte ?", color = gris_neutre, fontSize = 14.sp)
                    TextButton(onClick = { navController.navigate("signup") }) {
                        Text("S'inscrire", color = orange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

private fun login(
    context: Context,
    email: String,
    password: String,
    rememberMe: Boolean,
    navController: NavController,
    onError: (String) -> Unit
) {
    RetrofitClient.api.login(LoginRequest(email, password, rememberMe))
        .enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                when (response.code()) {
                    200 -> {
                        val prefs = context.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
                        if (rememberMe) {
                            prefs.edit {
                                putString("saved_email", email)
                                    .putBoolean("remember_me", true)
                            }
                        } else {
                            prefs.edit {
                                remove("saved_email")
                                    .putBoolean("remember_me", false)
                            }
                        }
                        navController.navigate("dashboard") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                    400 -> onError("Email ou mot de passe incorrect")
                    403 -> onError("Veuillez confirmer votre email avant de vous connecter")
                    else -> onError("Erreur serveur (${response.code()})")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                onError("Connexion impossible : ${t.message}")
            }
        })


}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    VistudiaTheme {
        LoginScreen(navController = rememberNavController())
    }
}