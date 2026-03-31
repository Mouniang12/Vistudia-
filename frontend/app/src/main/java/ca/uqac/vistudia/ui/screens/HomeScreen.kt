package ca.uqac.vistudia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ca.uqac.vistudia.R
import ca.uqac.vistudia.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().background(fond_gris)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(blue_foncee),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Vistudia",
                    color = blanc,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Your study journey, simplified",
                    color = blanc.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // Card boutons
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Bienvenue !",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = blue_foncee
                )
                Text(
                    "Connectez-vous ou créez un compte pour commencer.",
                    fontSize = 14.sp,
                    color = gris_neutre
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Se connecter", color = blanc, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate("signup") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = blue_clair),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("S'inscrire", color = blanc, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Bouton Invité
                OutlinedButton(
                    onClick = { navController.navigate("dashboard") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, blue_foncee)
                ) {
                    Text(
                        "Continuer en tant qu'invité",
                        color = blue_foncee,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    VistudiaTheme {
        HomeScreen(navController = rememberNavController())
    }
}