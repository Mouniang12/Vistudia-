package ca.uqac.vistudia.Models

data class ChecklistPartage(
    val destination: String,
    val proprietaire: String,
    val mode: String,
    val demarches: List<Demarche>,
    val demarchesEffectuees: List<Any>
)

data class PartageResponse(
    val token: String,
    val lien: String,
    val mode: String
)