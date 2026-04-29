package ca.uqac.vistudia

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.uqac.vistudia.ui.screens.CheckEmailScreen
import ca.uqac.vistudia.ui.screens.ChecklistPartageScreen
import ca.uqac.vistudia.ui.screens.ChecklistScreen
import ca.uqac.vistudia.ui.screens.ConditionsScreen
import ca.uqac.vistudia.ui.screens.DashboardScreen
import ca.uqac.vistudia.ui.screens.DocumentsScreen
import ca.uqac.vistudia.ui.screens.GuideImmigrationScreen
import ca.uqac.vistudia.ui.screens.HomeScreen
import ca.uqac.vistudia.ui.screens.LoginScreen
import ca.uqac.vistudia.ui.screens.ProfilScreen
import ca.uqac.vistudia.ui.screens.RecoveryScreen
import ca.uqac.vistudia.ui.screens.SignUpScreen
import ca.uqac.vistudia.ui.theme.VistudiaTheme
import ca.uqac.vistudia.ui.screens.ForumSalonsScreen
import ca.uqac.vistudia.ui.screens.ForumChatScreen
import ca.uqac.vistudia.ui.screens.MiseEnRelationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        val prefs = getSharedPreferences("vistudia_prefs", MODE_PRIVATE)
        val isRemembered = prefs.getBoolean("remember_me", false)
        val deepLinkToken = intent?.data?.lastPathSegment
        val naviguerVers = intent?.getStringExtra("naviguer_vers")

        val startDestination = when {
            naviguerVers == "documents" -> "documents"
            deepLinkToken != null -> "partage/$deepLinkToken"
            isRemembered -> "dashboard"
            else -> "home"
        }

        setContent {
            VistudiaTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("signup") { SignUpScreen(navController) }
                    composable("dashboard") { DashboardScreen(navController) }
                    composable("profile") { ProfilScreen(navController) }
                    composable("checklist") { ChecklistScreen(navController) }
                    composable("recovery") { RecoveryScreen(navController) }
                    composable("documents") { DocumentsScreen(navController) }
                    composable("guidePays") { GuideImmigrationScreen(navController) }
                    composable("immigration") { GuideImmigrationScreen(navController) }
                    composable("checkEmail/{email}") { backStack ->
                        CheckEmailScreen(
                            navController,
                            backStack.arguments?.getString("email") ?: ""
                        )
                    }
                    composable("partage/{token}") { backStack ->
                        ChecklistPartageScreen(
                            navController,
                            backStack.arguments?.getString("token") ?: ""
                        )
                    }
                    composable("forum") { ForumSalonsScreen(navController) }
                    composable("forum/chat/{salonId}/{nom}") { backStack ->
                        ForumChatScreen(
                            navController,
                            backStack.arguments?.getString("salonId") ?: "",
                            backStack.arguments?.getString("nom") ?: ""
                        )
                    }
                    composable("miseEnRelation") { MiseEnRelationScreen(navController) }
                    composable("conditions") { ConditionsScreen(navController)}
                }
            }
        }
    }
}
