package ca.uqac.vistudia.Models

data class Etape(
    val ordre: Int,
    val emoji: String,
    val titre: String,
    val description: String,
    val duree: String?,
    val cout: String?,
    val documents: List<String>
)

data class GuideImmigrationItem(
    val _id: String,
    val paysOrigine: String,
    val paysDestination: String,
    val titre: String,
    val description: String,
    val dureeTotal: String,
    val coutTotal: String
)

data class GuideImmigrationDetail(
    val _id: String,
    val paysOrigine: String,
    val paysDestination: String,
    val titre: String,
    val description: String,
    val dureeTotal: String,
    val coutTotal: String,
    val etapes: List<Etape>
)