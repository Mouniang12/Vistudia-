package ca.uqac.vistudia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ca.uqac.vistudia.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    var prenom by rememberSaveable { mutableStateOf("") }
    var nom by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var savedPrenom by rememberSaveable { mutableStateOf("") }
    var savedNom by rememberSaveable { mutableStateOf("") }
    var savedEmail by rememberSaveable { mutableStateOf("") }

    val hasChanges = prenom != savedPrenom || nom != savedNom || email != savedEmail

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fond_gris)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(blue_foncee),
            color = blue_foncee
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = blanc
                    )
                }
                Text(
                    text = "Mon profil",
                    color = blanc,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Informations personnelles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = gris_fonce
            )

            OutlinedTextField(
                value = prenom,
                onValueChange = { prenom = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Prénom") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nom") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mail") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (hasChanges) {
                Button(
                    onClick = {
                        savedPrenom = prenom
                        savedNom = nom
                        savedEmail = email
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Sauvegarder",
                        color = blanc,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = { logout(navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
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

private fun logout(navController: NavController) {
    navController.navigate("home") {
        popUpTo("profile") { inclusive = true }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    VistudiaTheme {
        ProfileScreen(
            navController = rememberNavController()
        )
    }
}