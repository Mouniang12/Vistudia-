package ca.uqac.vistudia.Models

data class ForumSalon(
    val _id: String,
    val nom: String,
    val description: String
)

data class ForumMessage(
    val _id: String? = null,
    val salonId: String,
    val auteur: String,
    val contenu: String,
    val dateEnvoi: String? = null
)