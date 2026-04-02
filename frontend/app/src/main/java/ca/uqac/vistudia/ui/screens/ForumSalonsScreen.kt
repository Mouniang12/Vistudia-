package ca.uqac.vistudia.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uqac.vistudia.Models.ForumSalon
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumSalonsScreen(navController: NavController) {
    val context = LocalContext.current
    var salons by remember { mutableStateOf(listOf<ForumSalon>()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    val prefs = context.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("auth_token", null)

    // Charger les salons au démarrage
    LaunchedEffect(Unit) {
        chargerSalons { list ->
            salons = list
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forum Vistudia", color = blanc, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = blanc)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blue_foncee)
            )
        },

        floatingActionButton = {
            if (!token.isNullOrEmpty()){
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = orange,
                    contentColor = blanc
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Créer un salon")
                }
            }
        },
        containerColor = fond_gris
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = orange)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(salons) { salon ->
                    SalonCard(salon) {
                        navController.navigate("forum/chat/${salon._id}/${salon.nom}")
                    }
                }
            }
        }

        if (showDialog) {
            CreerSalonDialog(
                onDismiss = { showDialog = false },
                onConfirm = { nom, desc ->
                    showDialog = false
                    creerSalon(nom, desc) { nouveau ->
                        salons = salons + nouveau
                    }
                }
            )
        }
    }
}

@Composable
fun SalonCard(salon: ForumSalon, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = blanc),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Chat, contentDescription = null, tint = orange, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(salon.nom, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = gris_fonce)
                Text(salon.description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun CreerSalonDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var nom by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau Salon") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom du salon") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = { if(nom.isNotBlank()) onConfirm(nom, desc) }, colors = ButtonDefaults.buttonColors(orange)) {
                Text("Créer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

private fun chargerSalons(onResult: (List<ForumSalon>) -> Unit) {
    RetrofitClient.api.getSalons().enqueue(object : Callback<List<ForumSalon>> {
        override fun onResponse(call: Call<List<ForumSalon>>, response: Response<List<ForumSalon>>) {
            if (response.isSuccessful) onResult(response.body() ?: emptyList())
        }
        override fun onFailure(call: Call<List<ForumSalon>>, t: Throwable) {}
    })
}

private fun creerSalon(nom: String, desc: String, onComplete: (ForumSalon) -> Unit) {
    val body = mapOf("nom" to nom, "description" to desc)
    RetrofitClient.api.createSalon(body).enqueue(object : Callback<ForumSalon> {
        override fun onResponse(call: Call<ForumSalon>, response: Response<ForumSalon>) {
            if (response.isSuccessful) response.body()?.let { onComplete(it) }
        }
        override fun onFailure(call: Call<ForumSalon>, t: Throwable) {}
    })
}