package ca.uqac.vistudia.Models

data class Demarche(
    val id: String,
    val titre: String,
    val description: String,
    val type: String = "demarche",
    val faite: Boolean = false
)