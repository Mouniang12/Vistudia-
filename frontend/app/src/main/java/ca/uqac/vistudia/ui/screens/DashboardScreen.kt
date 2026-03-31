package ca.uqac.vistudia.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)

        if (token == null) {
            // Mode invité
            prenom = "Invité"
            nom = ""
            loading = false
        } else {
            RetrofitClient.api.profile().enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    loading = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        prenom = body?.get("prenom") as? String ?: ""
                        nom = body?.get("nom") as? String ?: ""
                    } else if (response.code() == 401) {
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    loading = false
                    // En cas d'erreur réseau, on reste en mode invité visuellement
                    prenom = "Mode Hors-ligne"
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fond_gris)
    ) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(blue_foncee),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Bonjour 👋",
                        color = blanc.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    if (loading) {
                        CircularProgressIndicator(
                            color = blanc,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "$prenom $nom",
                            color = blanc,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "Bienvenue sur Vistudia",
                        color = blanc.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(orange, RoundedCornerShape(25.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (prenom.isNotEmpty()) prenom.first().uppercaseChar().toString() else "?",
                        color = blanc,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                "Que souhaitez-vous faire ?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = gris_fonce,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    DashboardCard(
                        icon = Icons.Default.CheckCircle,
                        titre = "Checklist Personnalisée",
                        couleur = orange,
                        onClick = { navController.navigate("checklist") }
                    )
                }

                item {
                    DashboardCard(
                        icon = Icons.Default.Description,
                        titre = "Documents",
                        couleur = orange,
                        onClick = {navController.navigate("documents") }
                    )
                }

                item {
                    DashboardCard(
                        icon = Icons.Default.MyLocation,
                        titre = "Guide pays",
                        couleur = orange,
                        onClick = {navController.navigate("guidePays") }
                    )
                }

                item {
                    DashboardCard(
                        icon = Icons.Default.Send,
                        titre = "Forum",
                        couleur = orange,
                        onClick = {navController.navigate("forum") }
                    )
                }

                item {
                    DashboardCard(
                        icon = Icons.Default.Person,
                        titre = "Mise en relation",
                        couleur = orange,
                        onClick = { navController.navigate("miseEnRelation") }
                    )
                }
            }

           // Spacer(modifier = Modifier.weight(0.000002f))

            Button(
                onClick = { logout(context, navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = blanc,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Se déconnecter",
                    color = blanc,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    icon: ImageVector,
    titre: String,
    couleur: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = blanc)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Icône avec fond
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        couleur.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = titre,
                    tint = couleur,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = titre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = gris_fonce,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun logout(context: Context, navController: NavController) {
    val prefs = context.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .remove("remember_me")
        .remove("saved_email")
        .remove("auth_token")
        .apply()

    RetrofitClient.api.logout().enqueue(object : Callback<Map<String, Any>> {
        override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
            navController.navigate("home") {
                popUpTo("dashboard") { inclusive = true }
            }
        }
        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
            navController.navigate("home") {
                popUpTo("dashboard") { inclusive = true }
            }
        }
    })
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    VistudiaTheme {
        DashboardScreen(
            navController = rememberNavController()
        )
    }
}