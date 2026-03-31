package ca.uqac.vistudia.Models

data class DocumentItem(
    val _id: String,
    val titre: String,
    val description: String?,
    val dateExpiration: String,
    val notificationEnvoyee: Boolean = false
)

data class AjouterDocumentRequest(
    val titre: String,
    val description: String,
    val dateExpiration: String
)