package ca.uqac.vistudia.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uqac.vistudia.Models.Etape
import ca.uqac.vistudia.Models.GuideImmigrationDetail
import ca.uqac.vistudia.Models.GuideImmigrationItem
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class EcranImmigration { SELECTION, DESTINATIONS, GUIDE }

@Composable
fun GuideImmigrationScreen(navController: NavController) {
    var ecran by remember { mutableStateOf(EcranImmigration.SELECTION) }
    var paysOrigineList by remember { mutableStateOf<List<String>>(emptyList()) }
    var paysOrigineSelectionne by remember { mutableStateOf("") }
    var destinations by remember { mutableStateOf<List<GuideImmigrationItem>>(emptyList()) }
    var guideDetail by remember { mutableStateOf<GuideImmigrationDetail?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        RetrofitClient.api.getPaysOrigine().enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                loading = false
                if (response.isSuccessful) paysOrigineList = response.body() ?: emptyList()
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                loading = false
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
                IconButton(onClick = {
                    when (ecran) {
                        EcranImmigration.GUIDE -> ecran = EcranImmigration.DESTINATIONS
                        EcranImmigration.DESTINATIONS -> ecran = EcranImmigration.SELECTION
                        EcranImmigration.SELECTION -> navController.popBackStack()
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = blanc)
                }
                Column {
                    Text(
                        when (ecran) {
                            EcranImmigration.SELECTION -> "🌍 Guide Immigration"
                            EcranImmigration.DESTINATIONS -> "Destinations depuis $paysOrigineSelectionne"
                            EcranImmigration.GUIDE -> guideDetail?.titre ?: "Guide"
                        },
                        color = blanc,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (ecran != EcranImmigration.SELECTION) {
                        Text(
                            when (ecran) {
                                EcranImmigration.DESTINATIONS -> "Choisissez votre destination"
                                EcranImmigration.GUIDE -> "Étapes de votre démarche"
                                else -> ""
                            },
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
        } else {
            when (ecran) {
                EcranImmigration.SELECTION -> {
                    SelectionPaysOrigine(
                        paysOrigineList = paysOrigineList,
                        onSelectPays = { pays ->
                            paysOrigineSelectionne = pays
                            loading = true
                            RetrofitClient.api.getDestinationsImmigration(pays)
                                .enqueue(object : Callback<List<GuideImmigrationItem>> {
                                    override fun onResponse(
                                        call: Call<List<GuideImmigrationItem>>,
                                        response: Response<List<GuideImmigrationItem>>
                                    ) {
                                        loading = false
                                        if (response.isSuccessful) {
                                            destinations = response.body() ?: emptyList()
                                            ecran = EcranImmigration.DESTINATIONS
                                        }
                                    }
                                    override fun onFailure(
                                        call: Call<List<GuideImmigrationItem>>,
                                        t: Throwable
                                    ) { loading = false }
                                })
                        }
                    )
                }

                EcranImmigration.DESTINATIONS -> {
                    ListeDestinations(
                        destinations = destinations,
                        onSelectDestination = { dest ->
                            loading = true
                            RetrofitClient.api.getGuideImmigration(dest._id)
                                .enqueue(object : Callback<GuideImmigrationDetail> {
                                    override fun onResponse(
                                        call: Call<GuideImmigrationDetail>,
                                        response: Response<GuideImmigrationDetail>
                                    ) {
                                        loading = false
                                        if (response.isSuccessful) {
                                            guideDetail = response.body()
                                            ecran = EcranImmigration.GUIDE
                                        }
                                    }
                                    override fun onFailure(
                                        call: Call<GuideImmigrationDetail>,
                                        t: Throwable
                                    ) { loading = false }
                                })
                        }
                    )
                }

                EcranImmigration.GUIDE -> {
                    guideDetail?.let { DetailGuideImmigration(guide = it) }
                }
            }
        }
    }
}

@Composable
fun SelectionPaysOrigine(
    paysOrigineList: List<String>,
    onSelectPays: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = blue_foncee.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Comment ça marche ?",
                        fontWeight = FontWeight.Bold,
                        color = blue_foncee
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "1. Sélectionnez votre pays d'origine\n" +
                                "2. Choisissez votre pays de destination\n" +
                                "3. Suivez le guide étape par étape",
                        color = gris_fonce,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Text(
                "Votre pays d'origine",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = gris_fonce
            )
        }

        items(paysOrigineList) { pays ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPays(pays) },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = blanc)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    orange.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌍", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            pays,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = gris_fonce
                        )
                    }
                    Text("→", fontSize = 20.sp, color = gris_neutre)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun ListeDestinations(
    destinations: List<GuideImmigrationItem>,
    onSelectDestination: (GuideImmigrationItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Choisissez votre destination",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = gris_fonce
            )
        }

        items(destinations) { dest ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDestination(dest) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = blanc)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            dest.paysDestination,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = blue_foncee
                        )
                        Text("→", fontSize = 20.sp, color = gris_neutre)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(dest.description, color = gris_neutre, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBadge(
                            "⏱️ ${dest.dureeTotal}",
                            blue_clair.copy(alpha = 0.1f),
                            blue_clair
                        )
                        InfoBadge(
                            "💰 ${dest.coutTotal}",
                            orange.copy(alpha = 0.1f),
                            orange
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun DetailGuideImmigration(guide: GuideImmigrationDetail) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Card info générale
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = blue_foncee)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "${guide.paysOrigine} → ${guide.paysDestination}",
                        color = blanc.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                    Text(
                        guide.titre,
                        color = blanc,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        guide.description,
                        color = blanc.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBadge(
                            "⏱️ ${guide.dureeTotal}",
                            blanc.copy(alpha = 0.2f),
                            blanc
                        )
                        InfoBadge(
                            "💰 ${guide.coutTotal}",
                            blanc.copy(alpha = 0.2f),
                            blanc
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Étapes à suivre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = gris_fonce
                )
                Text(
                    "${guide.etapes.size} étapes",
                    color = gris_neutre,
                    fontSize = 13.sp
                )
            }
        }

        itemsIndexed(guide.etapes) { index, etape ->
            EtapeCard(etape = etape, index = index, total = guide.etapes.size)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun EtapeCard(etape: Etape, index: Int, total: Int) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Numéro + ligne de progression
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(orange, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${index + 1}",
                    color = blanc,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            if (index < total - 1) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (expanded) 200.dp else 60.dp)
                        .background(orange.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Contenu
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { expanded = !expanded },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(3.dp),
            colors = CardDefaults.cardColors(containerColor = blanc)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(etape.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            etape.titre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = gris_fonce
                        )
                    }
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = gris_neutre,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (etape.duree != null || etape.cout != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        etape.duree?.let {
                            InfoBadge("⏱️ $it", blue_clair.copy(alpha = 0.1f), blue_clair)
                        }
                        etape.cout?.let {
                            InfoBadge("💰 $it", orange.copy(alpha = 0.1f), orange)
                        }
                    }
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = fond_gris
                        )

                        Text(
                            etape.description,
                            color = gris_fonce,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )

                        if (etape.documents.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "📄 Documents requis",
                                fontWeight = FontWeight.Bold,
                                color = blue_foncee,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            etape.documents.forEach { doc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(orange, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(doc, color = gris_fonce, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBadge(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}