package ca.uqac.vistudia.Models


data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)