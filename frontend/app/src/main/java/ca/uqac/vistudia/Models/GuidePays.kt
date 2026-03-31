package ca.uqac.vistudia.Models

data class Section(
    val titre: String,
    val contenu: String,
    val emoji: String = "📌"
)

data class GuidePaysItem(
    val _id: String,
    val nom: String,
    val emoji: String,
    val capitale: String,
    val langue: String,
    val monnaie: String,
    val description: String
)

data class GuidePaysDetail(
    val _id: String,
    val nom: String,
    val emoji: String,
    val capitale: String,
    val langue: String,
    val monnaie: String,
    val description: String,
    val sections: List<Section>
)