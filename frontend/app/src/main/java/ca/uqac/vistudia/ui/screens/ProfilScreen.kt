package ca.uqac.vistudia.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.Models.UserProfile
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

@Composable
fun ProfilScreen(navController: NavController) {
    val context = LocalContext.current
    var profil by remember { mutableStateOf<UserProfile?>(null) }
    var loading by remember { mutableStateOf(true) }
    var modeEdition by remember { mutableStateOf(false) }
    var showChangerPassword by remember { mutableStateOf(false) }
    var messageSucces by remember { mutableStateOf("") }

    // Champs éditables
    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var nationalite by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var paysOrigine by remember { mutableStateOf("") }
    var paysDestination by remember { mutableStateOf("") }

    fun chargerProfil() {
        RetrofitClient.api.getProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(
                call: Call<UserProfile>,
                response: Response<UserProfile>
            ) {
                loading = false
                if (response.isSuccessful) {
                    val p = response.body() ?: return
                    profil = p
                    prenom = p.prenom
                    nom = p.nom
                    telephone = p.telephone ?: ""
                    nationalite = p.nationalite ?: ""
                    dateNaissance = p.dateNaissance?.take(10) ?: ""
                    bio = p.bio ?: ""
                    paysOrigine = p.paysOrigine ?: ""
                    paysDestination = p.paysDestination ?: ""
                }
            }
            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                loading = false
            }
        })
    }

    LaunchedEffect(Unit) { chargerProfil() }

    // Dialog changer mot de passe
    if (showChangerPassword) {
        ChangerPasswordDialog(
            onSauvegarder = { ancien, nouveau ->
                RetrofitClient.api.changePassword(
                    mapOf("ancienPassword" to ancien, "nouveauPassword" to nouveau)
                ).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>
                    ) {
                        showChangerPassword = false
                        messageSucces = if (response.isSuccessful)
                            "✅ Mot de passe modifié !"
                        else "❌ Ancien mot de passe incorrect"
                    }
                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        showChangerPassword = false
                        messageSucces = "❌ Erreur réseau"
                    }
                })
            },
            onDismiss = { showChangerPassword = false }
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
                .height(200.dp)
                .background(blue_foncee)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Bouton retour
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = blanc)
                    }
                    Text(
                        "Mon Profil",
                        color = blanc,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            if (modeEdition) {
                                // Sauvegarder
                                val body = mapOf(
                                    "prenom" to prenom,
                                    "nom" to nom,
                                    "telephone" to telephone,
                                    "nationalite" to nationalite,
                                    "dateNaissance" to dateNaissance,
                                    "bio" to bio,
                                    "paysOrigine" to paysOrigine,
                                    "paysDestination" to paysDestination
                                )
                                RetrofitClient.api.updateProfile(body)
                                    .enqueue(object : Callback<Map<String, Any>> {
                                        override fun onResponse(
                                            call: Call<Map<String, Any>>,
                                            response: Response<Map<String, Any>>
                                        ) {
                                            modeEdition = false
                                            if (response.isSuccessful) {
                                                messageSucces = "✅ Profil mis à jour !"
                                                chargerProfil()
                                            }
                                        }
                                        override fun onFailure(
                                            call: Call<Map<String, Any>>,
                                            t: Throwable
                                        ) { modeEdition = false }
                                    })
                            } else {
                                modeEdition = true
                            }
                        }
                    ) {
                        Icon(
                            if (modeEdition) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null,
                            tint = blanc
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(orange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (prenom.isNotEmpty()) prenom.first().uppercaseChar().toString() else "?",
                        color = blanc,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!loading) {
                    Text(
                        "$prenom $nom",
                        color = blanc,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        profil?.email ?: "",
                        color = blanc.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = orange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Message succès
                if (messageSucces.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        )
                    ) {
                        Text(
                            messageSucces,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Mode édition banner
                if (modeEdition) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = orange.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = orange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Mode édition — Appuyez sur ✓ pour sauvegarder",
                                color = orange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Section Informations personnelles
                SectionProfil(titre = "👤 Informations personnelles") {
                    if (modeEdition) {
                        ChampEdition(
                            label = "Prénom",
                            value = prenom,
                            onValueChange = { prenom = it },
                            icon = Icons.Default.Person
                        )
                        ChampEdition(
                            label = "Nom",
                            value = nom,
                            onValueChange = { nom = it },
                            icon = Icons.Default.Person
                        )
                        ChampEdition(
                            label = "Téléphone",
                            value = telephone,
                            onValueChange = { telephone = it },
                            icon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone
                        )
                        ChampEdition(
                            label = "Nationalité",
                            value = nationalite,
                            onValueChange = { nationalite = it },
                            icon = Icons.Default.Flag
                        )

                        // Date de naissance avec DatePicker
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        dateNaissance = String.format(
                                            "%04d-%02d-%02d", year, month + 1, day
                                        )
                                    },
                                    cal.get(Calendar.YEAR) - 20,
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
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
                                if (dateNaissance.isEmpty()) "Date de naissance"
                                else "Né(e) le : $dateNaissance"
                            )
                        }
                    } else {
                        InfoLigne(Icons.Default.Person, "Prénom", prenom)
                        InfoLigne(Icons.Default.Person, "Nom", nom)
                        InfoLigne(
                            Icons.Default.Phone, "Téléphone",
                            telephone.ifEmpty { "Non renseigné" }
                        )
                        InfoLigne(
                            Icons.Default.Flag, "Nationalité",
                            nationalite.ifEmpty { "Non renseignée" }
                        )
                        InfoLigne(
                            Icons.Default.CalendarToday, "Date de naissance",
                            if (dateNaissance.isEmpty()) "Non renseignée"
                            else formatDateProfil(dateNaissance)
                        )
                    }
                }

                // Section Parcours
                SectionProfil(titre = "✈️ Mon parcours") {
                    if (modeEdition) {
                        ChampEdition(
                            label = "Pays d'origine",
                            value = paysOrigine,
                            onValueChange = { paysOrigine = it },
                            icon = Icons.Default.Home
                        )
                        ChampEdition(
                            label = "Pays de destination",
                            value = paysDestination,
                            onValueChange = { paysDestination = it },
                            icon = Icons.Default.Flight
                        )
                    } else {
                        InfoLigne(
                            Icons.Default.Home, "Pays d'origine",
                            paysOrigine.ifEmpty { "Non renseigné" }
                        )
                        InfoLigne(
                            Icons.Default.Flight, "Destination",
                            paysDestination.ifEmpty { "Non renseignée" }
                        )
                    }
                }

                // Section Bio
                SectionProfil(titre = "📝 À propos de moi") {
                    if (modeEdition) {
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Bio") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Text(
                            bio.ifEmpty { "Aucune bio renseignée." },
                            color = if (bio.isEmpty()) gris_neutre else gris_fonce,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                // Section Compte
                SectionProfil(titre = "🔐 Sécurité") {
                    InfoLigne(Icons.Default.Email, "Email", profil?.email ?: "")

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { showChangerPassword = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blue_foncee
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Changer le mot de passe")
                    }
                }

                // Section Stats
                SectionProfil(titre = "📊 Mon activité") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            valeur = profil?.dateCreation?.take(10)?.let {
                                formatDateProfil(it)
                            } ?: "-",
                            label = "Membre depuis"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            valeur = if (profil?.isVerified == true) "✅" else "❌",
                            label = "Email vérifié"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SectionProfil(titre: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = blanc),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                titre,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = blue_foncee,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            HorizontalDivider(color = fond_gris)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoLigne(icon: ImageVector, label: String, valeur: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(blue_foncee.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = blue_foncee, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = gris_neutre)
            Text(valeur, fontSize = 14.sp, color = gris_fonce, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ChampEdition(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = blue_foncee) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun StatCard(modifier: Modifier = Modifier, valeur: String, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = fond_gris)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valeur, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = blue_foncee,
                textAlign = TextAlign.Center)
            Text(label, fontSize = 11.sp, color = gris_neutre, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ChangerPasswordDialog(
    onSauvegarder: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var ancien by remember { mutableStateOf("") }
    var nouveau by remember { mutableStateOf("") }
    var confirmer by remember { mutableStateOf("") }
    var ancienVisible by remember { mutableStateOf(false) }
    var nouveauVisible by remember { mutableStateOf(false) }
    var erreur by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = blanc)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "🔐 Changer le mot de passe",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = blue_foncee
                )

                OutlinedTextField(
                    value = ancien,
                    onValueChange = { ancien = it },
                    label = { Text("Ancien mot de passe") },
                    visualTransformation = if (ancienVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { ancienVisible = !ancienVisible }) {
                            Icon(
                                if (ancienVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = nouveau,
                    onValueChange = { nouveau = it },
                    label = { Text("Nouveau mot de passe") },
                    visualTransformation = if (nouveauVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { nouveauVisible = !nouveauVisible }) {
                            Icon(
                                if (nouveauVisible) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = confirmer,
                    onValueChange = { confirmer = it },
                    label = { Text("Confirmer le nouveau mot de passe") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (erreur.isNotEmpty()) {
                    Text(erreur, color = Color.Red, fontSize = 13.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Annuler", color = gris_neutre) }

                    Button(
                        onClick = {
                            when {
                                ancien.isEmpty() || nouveau.isEmpty() ->
                                    erreur = "Remplissez tous les champs"
                                nouveau != confirmer ->
                                    erreur = "Les mots de passe ne correspondent pas"
                                nouveau.length < 6 ->
                                    erreur = "Minimum 6 caractères"
                                else -> onSauvegarder(ancien, nouveau)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = orange),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Modifier", color = blanc) }
                }
            }
        }
    }
}

fun formatDateProfil(dateStr: String): String {
    return try {
        val input = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val output = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        output.format(input.parse(dateStr.take(10))!!)
    } catch (e: Exception) { dateStr }
}