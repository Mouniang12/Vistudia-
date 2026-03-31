package ca.uqac.vistudia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun CheckEmailScreen(navController: NavController, email: String) {
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

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
                .background(blue_foncee),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📧", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Vérifiez votre email",
                    color = blanc,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Un email vous a été envoyé",
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Un email de confirmation a été envoyé à :",
                    color = gris_neutre,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                // Email affiché
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
                    "Cliquez sur le lien dans l'email pour activer votre compte avant de vous connecter.",
                    color = gris_neutre,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                // Message retour
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
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Bouton J'ai vérifié
                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("checkEmail/$email") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "J'ai vérifié mon email",
                        color = blanc,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bouton Renvoyer
                OutlinedButton(
                    onClick = {
                        loading = true
                        message = ""
                        RetrofitClient.api.resendVerification(mapOf("email" to email))
                            .enqueue(object : Callback<Map<String, Any>> {
                                override fun onResponse(
                                    call: Call<Map<String, Any>>,
                                    response: Response<Map<String, Any>>
                                ) {
                                    loading = false
                                    if (response.isSuccessful) {
                                        message = "✅ Email renvoyé avec succès !"
                                        isError = false
                                    } else {
                                        message = "❌ Erreur lors du renvoi."
                                        isError = true
                                    }
                                }
                                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                    loading = false
                                    message = "❌ Erreur réseau : ${t.message}"
                                    isError = true
                                }
                            })
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, blue_foncee),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = blue_foncee,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Renvoyer l'email", color = blue_foncee, fontSize = 15.sp)
                    }
                }

                // Retour login
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("← Retour à la connexion", color = blue_clair, fontSize = 13.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckEmailScreenPreview() {
    VistudiaTheme {
        CheckEmailScreen(
            navController = rememberNavController(),
            email = "test@exemple.com"
        )
    }
}