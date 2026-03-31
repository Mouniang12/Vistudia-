package ca.uqac.vistudia.Models

data class ChecklistResponse(
    val destination: String,
    val demarches: List<Demarche>
)