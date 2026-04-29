package ca.uqac.vistudia.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.uqac.vistudia.ui.theme.*

@Composable
fun ConditionsScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fond_gris)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // HEADER
        Text(
            text = "Conditions d’utilisation",
            style = MaterialTheme.typography.headlineMedium,
            color = blue_foncee
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Vistudia – Your study journey, simplified.",
            style = MaterialTheme.typography.bodyMedium,
            color = gris_neutre
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 1
        SectionTitle("1. Objet de l’application")

        SectionText(
            "Vistudia est une plateforme destinée à simplifier le parcours des étudiants internationaux en centralisant leurs démarches administratives, éducatives et organisationnelles."
        )

        // SECTION 2
        SectionTitle("2. Mission")

        SectionText(
            "Notre mission est de réduire la complexité des démarches liées à l’immigration et aux études à l’étranger en proposant une solution intuitive, centralisée et sécurisée."
        )

        // SECTION 3
        SectionTitle("3. Utilisation de la plateforme")

        SectionText(
            "L’utilisateur s’engage à utiliser Vistudia de manière responsable et conforme aux lois en vigueur. Toute utilisation abusive ou frauduleuse est interdite."
        )

        // SECTION 4
        SectionTitle("4. Données utilisateur")

        SectionText(
            "Les données saisies (checklist, documents, rappels) sont utilisées uniquement pour améliorer l’expérience utilisateur et personnaliser le parcours."
        )

        // SECTION 5
        SectionTitle("5. Responsabilité")

        SectionText(
            "Vistudia agit comme un assistant d’organisation. Les informations administratives doivent être vérifiées auprès des autorités officielles."
        )

        // SECTION 6
        SectionTitle("6. Évolution du service")

        SectionText(
            "L’application peut évoluer (checklist intelligente, rappels, forum, guides pays). Certaines fonctionnalités peuvent être ajoutées ou modifiées."
        )

        Spacer(modifier = Modifier.height(32.dp))

        // FOOTER BUTTON
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = orange)
        ) {
            Text("Retour à l’inscription")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = blue_foncee
    )
}

@Composable
fun SectionText(text: String) {
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = gris_fonce
    )
}