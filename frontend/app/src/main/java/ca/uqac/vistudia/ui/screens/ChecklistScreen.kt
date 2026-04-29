package ca.uqac.vistudia.ui.screens

import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import ca.uqac.vistudia.Models.*
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(navController: NavController) {
    val context = LocalContext.current
    var mesDestinations by remember { mutableStateOf<List<Destination>>(emptyList()) }
    var toutesDestinations by remember { mutableStateOf<List<Destination>>(emptyList()) }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    var demarches by remember { mutableStateOf<List<Demarche>>(emptyList()) }
    var historique by remember { mutableStateOf<List<Historique>>(emptyList()) }
    var loadingDemarches by remember { mutableStateOf(false) }
    var showHistorique by remember { mutableStateOf(false) }
    var showAjouterDestination by remember { mutableStateOf(false) }
    var showPartageDialog by remember { mutableStateOf(false) }
    var partageResponse by remember { mutableStateOf<PartageResponse?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        chargerMesDestinations { mesDestinations = it }
        chargerToutesDestinations { toutesDestinations = it }
        chargerHistorique { historique = it }
    }

    LaunchedEffect(selectedDestination) {
        selectedDestination?.let { dest ->
            loadingDemarches = true
            RetrofitClient.api.getChecklist(dest._id)
                .enqueue(object : Callback<ChecklistResponse> {
                    override fun onResponse(
                        call: Call<ChecklistResponse>,
                        response: Response<ChecklistResponse>
                    ) {
                        loadingDemarches = false
                        if (response.isSuccessful) {
                            demarches = response.body()?.demarches ?: emptyList()
                        }
                    }
                    override fun onFailure(call: Call<ChecklistResponse>, t: Throwable) {
                        loadingDemarches = false
                    }
                })
        }
    }

    // Dialog partage
    if (showPartageDialog && selectedDestination != null) {
        PartageDialog(
            destination = selectedDestination!!,
            partageResponse = partageResponse,
            onGenererPartage = { mode ->
                val body = mapOf(
                    "destinationId" to selectedDestination!!._id,
                    "mode" to mode
                )
                RetrofitClient.api.genererPartage(body)
                    .enqueue(object : Callback<PartageResponse> {
                        override fun onResponse(
                            call: Call<PartageResponse>,
                            response: Response<PartageResponse>
                        ) {
                            if (response.isSuccessful) partageResponse = response.body()
                        }
                        override fun onFailure(call: Call<PartageResponse>, t: Throwable) {}
                    })
            },
            onCopierLien = { lien ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Lien checklist", lien)
                clipboard.setPrimaryClip(clip)
            },
            onDismiss = {
                showPartageDialog = false
                partageResponse = null
            }
        )
    }

    // Dialog ajouter destination
    if (showAjouterDestination) {
        AjouterDestinationDialog(
            toutesDestinations = toutesDestinations,
            mesDestinations = mesDestinations,
            onAjouter = { dest ->
                RetrofitClient.api.ajouterDestination(mapOf("destinationId" to dest._id))
                    .enqueue(object : Callback<Map<String, Any>> {
                        override fun onResponse(
                            call: Call<Map<String, Any>>,
                            response: Response<Map<String, Any>>
                        ) {
                            if (response.isSuccessful) {
                                chargerMesDestinations { mesDestinations = it }
                            }
                        }
                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {}
                    })
            },
            onSupprimer = { dest ->
                RetrofitClient.api.supprimerDestination(dest._id)
                    .enqueue(object : Callback<Map<String, Any>> {
                        override fun onResponse(
                            call: Call<Map<String, Any>>,
                            response: Response<Map<String, Any>>
                        ) {
                            if (response.isSuccessful) {
                                chargerMesDestinations { mesDestinations = it }

                                // Si c'est la destination actuellement sélectionnée
                                // → vider la checklist et désélectionner
                                if (selectedDestination?._id == dest._id) {
                                    selectedDestination = null
                                    demarches = emptyList()
                                    historique = emptyList()
                                }
                            }
                        }
                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {}
                    })
            },
            onDismiss = { showAjouterDestination = false }
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
                        "Ma Checklist",
                        color = blanc,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    // Bouton partage
                    if (selectedDestination != null) {
                        IconButton(onClick = { showPartageDialog = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Partager", tint = blanc)
                        }
                    }
                    // Bouton ajouter destination
                    IconButton(onClick = { showAjouterDestination = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter", tint = blanc)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Sélecteur destination
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = blanc),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Mes destinations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = gris_fonce
                        )

                        if (mesDestinations.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Aucune destination ajoutée. Appuyez sur + pour en ajouter.",
                                color = gris_neutre,
                                fontSize = 13.sp
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedDestination?.nom ?: "Sélectionner...",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    mesDestinations.forEach { dest ->
                                        DropdownMenuItem(
                                            text = { Text(dest.nom) },
                                            onClick = {
                                                selectedDestination = dest
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Titre démarches
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📋 Démarches à effectuer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = gris_fonce
                    )
                    if (demarches.isNotEmpty()) {
                        Text(
                            "${demarches.size} restante(s)",
                            fontSize = 13.sp,
                            color = gris_neutre
                        )
                    }
                }
            }

            // Contenu démarches
            if (loadingDemarches) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = orange)
                    }
                }
            } else if (selectedDestination == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = blanc)
                    ) {
                        Text(
                            "Sélectionnez une destination pour voir les démarches.",
                            color = gris_neutre,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else if (demarches.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎉", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Toutes les démarches sont effectuées !",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(demarches) { demarche ->
                    DemarcheCard(
                        demarche = demarche,
                        onCocher = { dateExpiration ->
                            val body = if (dateExpiration != null) {
                                mapOf("dateExpiration" to dateExpiration)
                            } else {
                                emptyMap()
                            }
                            selectedDestination?.let { dest ->
                                RetrofitClient.api.cocherDemarche(dest._id, demarche.id, body)
                                    .enqueue(object : Callback<Map<String, Any>> {
                                        override fun onResponse(
                                            call: Call<Map<String, Any>>,
                                            response: Response<Map<String, Any>>
                                        ) {
                                            if (response.isSuccessful) {
                                                demarches = demarches.filter { it.id != demarche.id }
                                                chargerHistorique { historique = it }
                                            }
                                        }
                                        override fun onFailure(
                                            call: Call<Map<String, Any>>,
                                            t: Throwable
                                        ) {}
                                    })
                            }
                        },
                        onShowDatePicker = { onDateSelected ->
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                                    onDateSelected(date)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).apply {
                                setTitle("Date d'expiration du document")
                                datePicker.minDate = calendar.timeInMillis
                                show()
                            }
                        }
                    )
                }
            }

            // Historique
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📜 Historique",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = gris_fonce
                    )
                    TextButton(onClick = { showHistorique = !showHistorique }) {
                        Text(
                            if (showHistorique) "Masquer" else "Afficher (${historique.size})",
                            color = blue_clair,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (showHistorique) {
                if (historique.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = blanc)
                        ) {
                            Text(
                                "Aucune démarche effectuée pour l'instant.",
                                color = gris_neutre,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(historique) { item ->
                        HistoriqueCard(item)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// Dialog partage
@Composable
fun PartageDialog(
    destination: Destination,
    partageResponse: PartageResponse?,
    onGenererPartage: (String) -> Unit,
    onCopierLien: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = blanc)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Partager la checklist",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = blue_foncee
                )
                Text(
                    destination.nom,
                    color = gris_neutre,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (partageResponse == null) {
                    Text(
                        "Choisissez le mode de partage :",
                        color = gris_fonce,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mode lecture
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = blue_clair.copy(alpha = 0.1f)
                        ),
                        onClick = { onGenererPartage("lecture") }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.RemoveRedEye,
                                contentDescription = null,
                                tint = blue_clair,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Mode Lecture",
                                    fontWeight = FontWeight.Bold,
                                    color = blue_clair
                                )
                                Text(
                                    "Le destinataire peut voir la checklist",
                                    color = gris_neutre,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mode édition
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = orange.copy(alpha = 0.1f)
                        ),
                        onClick = { onGenererPartage("edition") }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = orange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Mode Édition",
                                    fontWeight = FontWeight.Bold,
                                    color = orange
                                )
                                Text(
                                    "Le destinataire peut cocher les démarches",
                                    color = gris_neutre,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    // Lien généré
                    Text(
                        "Lien généré en mode ${partageResponse.mode} :",
                        color = gris_fonce,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = fond_gris)
                    ) {
                        Text(
                            partageResponse.lien,
                            color = blue_foncee,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onCopierLien(partageResponse.lien) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = orange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = blanc,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copier le lien", color = blanc)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fermer", color = gris_neutre)
                }
            }
        }
    }
}

// Dialog ajouter destination
@Composable
fun AjouterDestinationDialog(
    toutesDestinations: List<Destination>,
    mesDestinations: List<Destination>,
    onAjouter: (Destination) -> Unit,
    onSupprimer: (Destination) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = blanc)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Gérer mes destinations",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = blue_foncee
                )

                Spacer(modifier = Modifier.height(16.dp))

                toutesDestinations.forEach { dest ->
                    val estAjoutee = mesDestinations.any { it._id == dest._id }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            dest.nom,
                            fontSize = 15.sp,
                            color = gris_fonce,
                            fontWeight = if (estAjoutee) FontWeight.Bold else FontWeight.Normal
                        )

                        if (estAjoutee) {
                            IconButton(
                                onClick = { onSupprimer(dest) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { onAjouter(dest) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Ajouter",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = fond_gris)
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fermer", color = gris_neutre)
                }
            }
        }
    }
}

private fun chargerMesDestinations(onResult: (List<Destination>) -> Unit) {
    RetrofitClient.api.getMesDestinations().enqueue(object : Callback<List<Destination>> {
        override fun onResponse(
            call: Call<List<Destination>>,
            response: Response<List<Destination>>
        ) {
            if (response.isSuccessful) onResult(response.body() ?: emptyList())
        }
        override fun onFailure(call: Call<List<Destination>>, t: Throwable) {}
    })
}

private fun chargerToutesDestinations(onResult: (List<Destination>) -> Unit) {
    RetrofitClient.api.getDestinations().enqueue(object : Callback<List<Destination>> {
        override fun onResponse(
            call: Call<List<Destination>>,
            response: Response<List<Destination>>
        ) {
            if (response.isSuccessful) onResult(response.body() ?: emptyList())
        }
        override fun onFailure(call: Call<List<Destination>>, t: Throwable) {}
    })
}

private fun chargerHistorique(onResult: (List<Historique>) -> Unit) {
    RetrofitClient.api.getHistorique().enqueue(object : Callback<List<Historique>> {
        override fun onResponse(
            call: Call<List<Historique>>,
            response: Response<List<Historique>>
        ) {
            if (response.isSuccessful) onResult(response.body() ?: emptyList())
        }
        override fun onFailure(call: Call<List<Historique>>, t: Throwable) {}
    })
}

@Composable
fun DemarcheCard(
    demarche: Demarche,
    onCocher: (String?) -> Unit,
    onShowDatePicker: (onDateSelected: (String) -> Unit) -> Unit
) {
    var checked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = blanc),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        if (demarche.type == "document") {
                            onShowDatePicker { date ->
                                checked = true
                                onCocher(date)
                            }
                        } else {
                            checked = true
                            onCocher(null)
                        }
                    }
                },
                colors = CheckboxDefaults.colors(checkedColor = orange)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    demarche.titre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = gris_fonce
                )
                Text(
                    demarche.description,
                    fontSize = 13.sp,
                    color = gris_neutre
                )
            }

            if (demarche.type == "document") {
                Icon(
                    Icons.Default.Description,
                    contentDescription = "Document",
                    tint = blue_clair,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun HistoriqueCard(item: Historique) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = blanc),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.titreDemarche,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = gris_fonce
                )
                Text(
                    "📍 ${item.destinationNom}",
                    fontSize = 13.sp,
                    color = blue_clair
                )
                Text(
                    "✅ ${item.dateEffectuee.take(10)}",
                    fontSize = 12.sp,
                    color = gris_neutre
                )
            }
        }
    }
}