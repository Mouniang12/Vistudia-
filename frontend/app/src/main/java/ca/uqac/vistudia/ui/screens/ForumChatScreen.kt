package ca.uqac.vistudia.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
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
import ca.uqac.vistudia.Models.ForumMessage
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumChatScreen(navController: NavController, salonId: String, salonNom: String) {
    var messages by remember { mutableStateOf(listOf<ForumMessage>()) }
    var text by remember { mutableStateOf("") }
    var monNom by remember { mutableStateOf("Invité") }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("auth_token", null)

    // 1. Récupérer le nom du profil
    LaunchedEffect(Unit) {
        RetrofitClient.api.profile().enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val prenom = body?.get("prenom") as? String ?: ""
                    val nom = body?.get("nom") as? String ?: ""
                    if (prenom.isNotEmpty()) monNom = "$prenom $nom"
                }
            }
            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {}
        })

        // 2. Charger les messages du salon
        chargerMessages(salonId) { messages = it }
    }

    // Scroll automatique vers le bas lors de nouveaux messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(salonNom, color = blanc, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = blanc)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blue_foncee)
            )
        },
        containerColor = fond_gris
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Zone des messages
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(msg, monNom)
                }
            }

            // Zone de saisie
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = blanc) {
                Row(
                    modifier = Modifier.padding(12.dp).navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        enabled = !token.isNullOrEmpty(),
                        placeholder = { Text("Écrire un message...") },
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (text.isNotBlank()) {
                                envoyerMessage(salonId, monNom, text) { nouveau ->
                                    messages = messages + nouveau
                                    text = ""
                                }
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = orange)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = blanc)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ForumMessage, monNom: String) {
    val estMoi = msg.auteur == monNom
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (estMoi) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (estMoi) orange else Color(0xFFE0E0E0),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (estMoi) 16.dp else 0.dp,
                bottomEnd = if (estMoi) 0.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!estMoi) Text(msg.auteur, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = blue_foncee)
                Text(msg.contenu, color = if (estMoi) blanc else gris_fonce)
            }
        }
    }
}

private fun chargerMessages(salonId: String, onResult: (List<ForumMessage>) -> Unit) {
    RetrofitClient.api.getMessages(salonId).enqueue(object : Callback<List<ForumMessage>> {
        override fun onResponse(call: Call<List<ForumMessage>>, response: Response<List<ForumMessage>>) {
            if (response.isSuccessful) onResult(response.body() ?: emptyList())
        }
        override fun onFailure(call: Call<List<ForumMessage>>, t: Throwable) {}
    })
}

private fun envoyerMessage(salonId: String, auteur: String, contenu: String, onComplete: (ForumMessage) -> Unit) {
    val message = ForumMessage(salonId = salonId, auteur = auteur, contenu = contenu)
    RetrofitClient.api.postMessage(salonId, message).enqueue(object : Callback<ForumMessage> {
        override fun onResponse(call: Call<ForumMessage>, response: Response<ForumMessage>) {
            if (response.isSuccessful) response.body()?.let { onComplete(it) }
        }
        override fun onFailure(call: Call<ForumMessage>, t: Throwable) {}
    })
}