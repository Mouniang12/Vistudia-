package ca.uqac.vistudia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RecoveryScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var emailSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fond_gris)
    ) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(blue_foncee)
        ) {
            IconButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = blanc
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("🔒", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Mot de passe oublié",
                    color = blanc,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Réinitialisez votre mot de passe",
                    color = blanc.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // Card
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

                if (!emailSent) {

                    Text(
                        "Entrez votre email pour recevoir un lien de réinitialisation.",
                        color = gris_neutre,
                        fontSize = 14.sp
                    )

                    // Champ email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = blue_foncee)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        enabled = !loading
                    )

                    // Message erreur
                    if (message.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                message,
                                color = if (isError) Color(0xFFE53935) else Color(0xFF4CAF50),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Bouton envoyer
                    Button(
                        onClick = {
                            if (email.isEmpty()) {
                                message = "Veuillez entrer votre email"
                                isError = true
                                return@Button
                            }
                            loading = true
                            message = ""
                            RetrofitClient.api.forgotPassword(mapOf("email" to email))
                                .enqueue(object : Callback<Map<String, Any>> {
                                    override fun onResponse(
                                        call: Call<Map<String, Any>>,
                                        response: Response<Map<String, Any>>
                                    ) {
                                        loading = false
                                        if (response.isSuccessful) {
                                            emailSent = true
                                        } else {
                                            message = when (response.code()) {
                                                404 -> "Aucun compte associé à cet email"
                                                else -> "Erreur serveur (${response.code()})"
                                            }
                                            isError = true
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<Map<String, Any>>,
                                        t: Throwable
                                    ) {
                                        loading = false
                                        message = "Connexion impossible : ${t.message}"
                                        isError = true
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
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
                            Text(
                                "Envoyer le lien",
                                color = blanc,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                } else {

                    // Email envoyé avec succès
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("✅", fontSize = 48.sp)
                        Text(
                            "Email envoyé !",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = blue_foncee
                        )
                        Text(
                            "Un lien de réinitialisation a été envoyé à :",
                            color = gris_neutre,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = fond_gris),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                email,
                                fontWeight = FontWeight.Bold,
                                color = blue_foncee,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }
                        Text(
                            "Vérifiez votre boîte mail et cliquez sur le lien pour réinitialiser votre mot de passe.",
                            color = gris_neutre,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Divider(color = fond_gris)

                // Retour login
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("← Retour à la connexion", color = blue_clair, fontSize = 13.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecoveryScreenPreview() {
    VistudiaTheme {
        RecoveryScreen(navController = rememberNavController())
    }
}