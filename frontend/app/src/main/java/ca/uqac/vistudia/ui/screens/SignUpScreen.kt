package ca.uqac.vistudia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ca.uqac.vistudia.Models.Constants
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.Models.User
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var acceptConditions by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    var paysOrigine by remember { mutableStateOf("") }
    var paysDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fond_gris)
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(blue_foncee)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("←", color = blanc, fontSize = 24.sp)
                }
                Text(
                    "Créer un compte",
                    color = blanc,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = blanc)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                OutlinedTextField(
                    value = prenom,
                    onValueChange = { prenom = it },
                    label = { Text("Prénom") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = blue_foncee) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = blue_foncee) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = blue_foncee) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = paysDropdownExpanded,
                    onExpandedChange = { paysDropdownExpanded = !paysDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = paysOrigine,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pays d'origine") },
                        leadingIcon = { Icon(Icons.Default.Place, null, tint = blue_foncee) },
                        trailingIcon = {
                            Icon(
                                imageVector = if (paysDropdownExpanded)
                                    Icons.Default.KeyboardArrowUp
                                else
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = blue_foncee
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = paysDropdownExpanded,
                        onDismissRequest = { paysDropdownExpanded = false }
                    ) {
                        Constants.PAYS_LISTE.forEach { pays ->
                            DropdownMenuItem(
                                text = { Text(pays) },
                                onClick = {
                                    paysOrigine = pays
                                    paysDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = blue_foncee) },
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Cacher" else "Voir", color = blue_clair, fontSize = 12.sp)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmer le mot de passe") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = blue_foncee) },
                    trailingIcon = {
                        TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Text(if (confirmPasswordVisible) "Cacher" else "Voir", color = blue_clair, fontSize = 12.sp)
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                // CONDITIONS AVEC LIEN
                val annotatedText = buildAnnotatedString {
                    append("J'accepte les ")
                    pushStringAnnotation(tag = "URL", annotation = "conditions")
                    withStyle(style = SpanStyle(color = blue_clair, textDecoration = TextDecoration.Underline)) {
                        append("conditions d'utilisation")
                    }
                    pop()
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = acceptConditions,
                        onCheckedChange = { acceptConditions = it },
                        colors = CheckboxDefaults.colors(checkedColor = orange)
                    )
                    ClickableText(
                        text = annotatedText,
                        style = TextStyle(color = gris_fonce, fontSize = 13.sp),
                        onClick = { navController.navigate("conditions") }
                    )
                }

                if (errorMsg.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(errorMsg, color = Color(0xFFE53935), fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                Button(
                    onClick = {
                        when {
                            prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || password.isEmpty() ->
                                errorMsg = "Veuillez remplir tous les champs"

                            paysOrigine.isEmpty() ->
                                errorMsg = "Veuillez sélectionner votre pays d'origine"  // ✅ validation

                            !isValidEmail(email) ->
                                errorMsg = "Email invalide"

                            password != confirmPassword ->
                                errorMsg = "Les mots de passe ne correspondent pas"

                            !acceptConditions ->
                                errorMsg = "Veuillez accepter les conditions"

                            else -> {
                                loading = true
                                register(prenom, nom, email, password, paysOrigine, navController) { msg: String ->
                                    errorMsg = msg
                                    loading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) {
                    if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = blanc)
                    else Text("S'inscrire")
                }
            }
        }
    }
}

private fun register(
    prenom: String,
    nom: String,
    email: String,
    password: String,
    paysOrigine: String,
    navController: NavController,
    onError: (String) -> Unit
) {
    RetrofitClient.api.register(User(prenom, nom, email, password, paysOrigine))
        .enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    navController.navigate("checkEmail/$email")
                } else {
                    onError("Erreur serveur")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                onError("Connexion impossible")
            }
        })
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUp() {
    VistudiaTheme {
        SignUpScreen(navController = rememberNavController())
    }
}