package ca.uqac.vistudia.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import ca.uqac.vistudia.Models.DocumentItem
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

@Composable
fun DocumentsScreen(navController: NavController) {
    val context = LocalContext.current
    var documents by remember { mutableStateOf<List<DocumentItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showAjouterDialog by remember { mutableStateOf(false) }
    var documentAModifier by remember { mutableStateOf<DocumentItem?>(null) }

    fun charger() {
        RetrofitClient.api.getMesDocuments()
            .enqueue(object : Callback<List<DocumentItem>> {
                override fun onResponse(
                    call: Call<List<DocumentItem>>,
                    response: Response<List<DocumentItem>>
                ) {
                    loading = false
                    if (response.isSuccessful) documents = response.body() ?: emptyList()
                }
                override fun onFailure(call: Call<List<DocumentItem>>, t: Throwable) {
                    loading = false
                }
            })
    }

    LaunchedEffect(Unit) { charger() }

    // Dialog ajouter/modifier
    if (showAjouterDialog || documentAModifier != null) {
        DocumentDialog(
            document = documentAModifier,
            onSauvegarder = { titre, description, dateExpiration ->
                val body = mapOf(
                    "titre" to titre,
                    "description" to description,
                    "dateExpiration" to dateExpiration
                )
                if (documentAModifier != null) {
                    RetrofitClient.api.modifierDocument(documentAModifier!!._id, body)
                        .enqueue(object : Callback<Map<String, Any>> {
                            override fun onResponse(
                                call: Call<Map<String, Any>>,
                                response: Response<Map<String, Any>>
                            ) {
                                if (response.isSuccessful) {
                                    documentAModifier = null
                                    charger()
                                }
                            }
                            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {}
                        })
                } else {
                    RetrofitClient.api.ajouterDocument(body)
                        .enqueue(object : Callback<Map<String, Any>> {
                            override fun onResponse(
                                call: Call<Map<String, Any>>,
                                response: Response<Map<String, Any>>
                            ) {
                                if (response.isSuccessful) {
                                    showAjouterDialog = false
                                    charger()
                                }
                            }
                            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {}
                        })
                }
            },
            onDismiss = {
                showAjouterDialog = false
                documentAModifier = null
            }
        )
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
                .height(100.dp)
                .background(blue_foncee),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = blanc)
                    }
                    Text(
                        "📄 Mes Documents",
                        color = blanc,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = { showAjouterDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter", tint = blanc)
                }
            }
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = orange)
            }
        } else if (documents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📄", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Aucun document ajouté",
                        fontSize = 18.sp,
                        color = gris_fonce,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Appuyez sur + pour ajouter un document",
                        fontSize = 14.sp,
                        color = gris_neutre
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Résumé
                item {
                    val expires = documents.count { doc ->
                        val expiration = parseDate(doc.dateExpiration)
                        val demain = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }.time
                        expiration != null && expiration.before(demain)
                    }

                    if (expires > 0) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFF3E0)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "$expires document(s) expirent bientôt !",
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Liste documents
                items(documents) { doc ->
                    DocumentCard(
                        document = doc,
                        onModifier = { documentAModifier = doc },
                        onSupprimer = {
                            RetrofitClient.api.supprimerDocument(doc._id)
                                .enqueue(object : Callback<Map<String, Any>> {
                                    override fun onResponse(
                                        call: Call<Map<String, Any>>,
                                        response: Response<Map<String, Any>>
                                    ) {
                                        if (response.isSuccessful) charger()
                                    }
                                    override fun onFailure(
                                        call: Call<Map<String, Any>>,
                                        t: Throwable
                                    ) {}
                                })
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun DocumentCard(
    document: DocumentItem,
    onModifier: () -> Unit,
    onSupprimer: () -> Unit
) {
    val expiration = parseDate(document.dateExpiration)
    val maintenant = Calendar.getInstance().time
    val demain = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
    }.time

    val estExpire = expiration != null && expiration.before(maintenant)
    val expireBientot = expiration != null && expiration.before(demain) && !estExpire
    val joursRestants = expiration?.let {
        val diff = it.time - maintenant.time
        (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    val couleurBordure = when {
        estExpire -> Color(0xFFE53935)
        expireBientot -> Color(0xFFF58220)
        joursRestants != null && joursRestants <= 7 -> Color(0xFFFFB300)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = blanc),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Barre couleur statut
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(couleurBordure, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📄", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            document.titre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = gris_fonce
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onModifier,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Modifier",
                                tint = blue_clair,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onSupprimer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (!document.description.isNullOrEmpty()) {
                    Text(
                        document.description,
                        color = gris_neutre,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Statut expiration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            couleurBordure.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        when {
                            estExpire -> "⛔"
                            expireBientot -> "⚠️"
                            joursRestants != null && joursRestants <= 7 -> "🔔"
                            else -> "✅"
                        },
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        when {
                            estExpire -> "Expiré le ${formatDate(document.dateExpiration)}"
                            joursRestants != null && joursRestants == 0 -> "Expire aujourd'hui !"
                            joursRestants != null && joursRestants == 1 -> "Expire demain !"
                            joursRestants != null && joursRestants <= 7 -> "Expire dans $joursRestants jours"
                            else -> "Expire le ${formatDate(document.dateExpiration)}"
                        },
                        color = couleurBordure,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentDialog(
    document: DocumentItem?,
    onSauvegarder: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var titre by remember { mutableStateOf(document?.titre ?: "") }
    var description by remember { mutableStateOf(document?.description ?: "") }
    var dateExpiration by remember {
        mutableStateOf(document?.dateExpiration?.take(10) ?: "")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = blanc)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    if (document != null) "Modifier le document" else "Ajouter un document",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = blue_foncee
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Nom du document") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sélecteur de date
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                dateExpiration = String.format(
                                    "%04d-%02d-%02d",
                                    year, month + 1, day
                                )
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (dateExpiration.isEmpty()) "Choisir la date d'expiration"
                        else "Expire le : $dateExpiration"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Annuler", color = gris_neutre)
                    }

                    Button(
                        onClick = {
                            if (titre.isNotEmpty() && dateExpiration.isNotEmpty()) {
                                onSauvegarder(titre, description, dateExpiration)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = orange),
                        shape = RoundedCornerShape(12.dp),
                        enabled = titre.isNotEmpty() && dateExpiration.isNotEmpty()
                    ) {
                        Text("Sauvegarder", color = blanc)
                    }
                }
            }
        }
    }
}

// Helpers
fun parseDate(dateStr: String): java.util.Date? {
    return try {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .parse(dateStr.take(10))
    } catch (e: Exception) { null }
}

fun formatDate(dateStr: String): String {
    return try {
        val input = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val output = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        output.format(input.parse(dateStr.take(10))!!)
    } catch (e: Exception) { dateStr }
}