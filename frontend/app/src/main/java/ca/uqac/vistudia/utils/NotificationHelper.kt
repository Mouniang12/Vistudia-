package ca.uqac.vistudia.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ca.uqac.vistudia.MainActivity

object NotificationHelper {

    const val CHANNEL_ID = "documents_expiration"

    fun creerCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Expiration Documents",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Rappels d'expiration de vos documents"
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    fun envoyerNotification(
        context: Context,
        titreDocument: String,
        dateExpiration: String,
        notifId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("naviguer_vers", "documents")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Document expirant demain !")
            .setContentText("\"$titreDocument\" expire le $dateExpiration")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Votre document \"$titreDocument\" expire le $dateExpiration.\n" +
                            "Pensez à le renouveler !"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFFF58220.toInt())
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(notifId, notification)
    }
}