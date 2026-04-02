package ca.uqac.vistudia.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.compose.foundation.lazy.LazyColumn
// import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import ca.uqac.vistudia.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiseEnRelationScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mise en relation", color = blanc) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = blanc
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blue_foncee)
            )
        },
        containerColor = fond_gris
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Text(
                    "Retrouvez notre communauté sur les réseaux sociaux :",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = gris_fonce,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                SocialLinkCard(
                    title = "Discord",
                    description = "Rejoignez notre serveur pour discuter avec d'autres étudiants et obtenir de l'aide.",
                    buttonText = "Ouvrir Discord",
                    color = Color(0xFF5865F2),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://discord.gg/your-link".toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                SocialLinkCard(
                    title = "Facebook",
                    description = "Suivez notre page pour ne rien manquer des événements et actualités.",
                    buttonText = "Voir la page Facebook",
                    color = Color(0xFF1877F2),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://www.facebook.com/your-page".toUri())
                        context.startActivity(intent)
                    }
                )
            }

            item {
                SocialLinkCard(
                    title = "Instagram",
                    description = "Découvrez la vie étudiante à travers nos photos et stories.",
                    buttonText = "Nous suivre sur Instagram",
                    color = Color(0xFFE4405F),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, "https://www.instagram.com/your-account".toUri())
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun SocialLinkCard(
    title: String,
    description: String,
    buttonText: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = blanc),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = gris_fonce
            )
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(buttonText, color = blanc)
            }
        }
    }
}
