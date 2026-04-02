package ca.uqac.vistudia.Models

data class User(
    val prenom: String,
    val nom: String,
    val email: String,
    val password: String
)

data class UserProfile(
    val _id: String,
    val prenom: String,
    val nom: String,
    val email: String,
    val telephone: String?,
    val nationalite: String?,
    val dateNaissance: String?,
    val bio: String?,
    val paysOrigine: String?,
    val paysDestination: String?,
    val dateCreation: String?,
    val isVerified: Boolean?
)

data class UpdateProfileRequest(
    val prenom: String,
    val nom: String,
    val telephone: String,
    val nationalite: String,
    val dateNaissance: String,
    val bio: String,
    val paysOrigine: String,
    val paysDestination: String
)