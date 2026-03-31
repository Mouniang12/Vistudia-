package ca.uqac.vistudia.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
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
import ca.uqac.vistudia.Models.ChecklistPartage
import ca.uqac.vistudia.Models.Demarche
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

@Composable
fun ChecklistPartageScreen(navController: NavController, token: String) {
    val context = LocalContext.current
    var checklistPartage by remember { mutableStateOf<ChecklistPartage?>(null) }
    var demarches by remember { mutableStateOf<List<Demarche>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var erreur by remember { mutableStateOf("") }

    LaunchedEffect(token) {
        RetrofitClient.api.getChecklistPartage(token)
            .enqueue(object : Callback<ChecklistPartage> {
                override fun onResponse(
                    call: Call<ChecklistPartage>,
                    response: Response<ChecklistPartage>
                ) {
                    loading = false
                    if (response.isSuccessful) {
                        checklistPartage = response.body()
                        demarches = response.body()?.demarches ?: emptyList()
                    } else {
                        erreur = "Lien invalide ou expiré"
                    }
                }
                override fun onFailure(call: Call<ChecklistPartage>, t: Throwable) {
                    loading = false
                    erreur = "Erreur réseau : ${t.message}"
                }
            })
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = blanc)
                }
                Column {
                    Text(
                        checklistPartage?.destination ?: "Checklist partagée",
                        color = blanc,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    checklistPartage?.let {
                        Text(
                            "Partagée par ${it.proprietaire} • Mode ${it.mode}",
                            color = blanc.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = orange)
            }
        } else if (erreur.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(erreur, color = gris_fonce, fontSize = 16.sp)
                }
            }
        } else {
            checklistPartage?.let { partage ->
                val modeEdition = partage.mode == "edition"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Badge mode
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (modeEdition)
                                    orange.copy(alpha = 0.1f)
                                else
                                    blue_clair.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (modeEdition) "✏️" else "👁️",
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (modeEdition)
                                        "Mode édition — Vous pouvez cocher les démarches"
                                    else
                                        "Mode lecture — Consultation uniquement",
                                    color = if (modeEdition) orange else blue_clair,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "📋 Démarches (${demarches.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = gris_fonce
                        )
                    }

                    items(demarches) { demarche ->
                        if (modeEdition) {
                            DemarcheCardPartage(
                                demarche = demarche,
                                onCocher = { dateExpiration ->
                                    val body = if (dateExpiration != null) {
                                        mapOf("dateExpiration" to dateExpiration)
                                    } else {
                                        emptyMap()
                                    }
                                    RetrofitClient.api.cocherDemarchePartage(
                                        token, demarche.id, body
                                    ).enqueue(object : Callback<Map<String, Any>> {
                                        override fun onResponse(
                                            call: Call<Map<String, Any>>,
                                            response: Response<Map<String, Any>>
                                        ) {
                                            if (response.isSuccessful) {
                                                demarches = demarches.filter { it.id != demarche.id }
                                            }
                                        }
                                        override fun onFailure(
                                            call: Call<Map<String, Any>>,
                                            t: Throwable
                                        ) {}
                                    })
                                },
                                onShowDatePicker = { onDateSelected ->
                                    val calendar = Calendar.getInstance()
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            val date = String.format(
                                                "%04d-%02d-%02d",
                                                year, month + 1, day
                                            )
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
                        } else {
                            // Mode lecture — pas de checkbox
                            DemarcheCardLecture(demarche = demarche)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun DemarcheCardPartage(
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
                Text(demarche.description, fontSize = 13.sp, color = gris_neutre)
            }
            if (demarche.type == "document") {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = blue_clair,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun DemarcheCardLecture(demarche: Demarche) {
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
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (demarche.faite) Color(0xFF4CAF50) else gris_neutre.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (demarche.faite) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = blanc,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    demarche.titre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (demarche.faite) gris_neutre else gris_fonce
                )
                Text(demarche.description, fontSize = 13.sp, color = gris_neutre)
            }
            if (demarche.type == "document") {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = blue_clair,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}