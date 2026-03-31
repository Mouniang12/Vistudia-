package ca.uqac.vistudia.utils

import android.content.Context
import androidx.work.*
import ca.uqac.vistudia.Models.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DocumentsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Appel synchrone avec execute() au lieu de enqueue()
                val response = RetrofitClient.api.getMesDocuments().execute()

                if (!response.isSuccessful) return@withContext Result.retry()

                val documents = response.body() ?: return@withContext Result.success()

                val demain = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                documents.forEachIndexed { index, doc ->
                    val expiration = parseDateDoc(doc.dateExpiration)
                        ?: return@forEachIndexed

                    val calExp = Calendar.getInstance().apply { time = expiration }

                    val memeJour =
                        calExp.get(Calendar.YEAR) == demain.get(Calendar.YEAR) &&
                                calExp.get(Calendar.DAY_OF_YEAR) == demain.get(Calendar.DAY_OF_YEAR)

                    if (memeJour) {
                        NotificationHelper.envoyerNotification(
                            applicationContext,
                            doc.titre,
                            formatDateDoc(doc.dateExpiration),
                            index
                        )
                    }
                }

                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
}

fun parseDateDoc(dateStr: String): Date? {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr.take(10))
    } catch (e: Exception) { null }
}

fun formatDateDoc(dateStr: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        output.format(input.parse(dateStr.take(10))!!)
    } catch (e: Exception) { dateStr }
}

fun planifierVerificationDocuments(context: Context) {
    val maintenant = Calendar.getInstance()
    val prochaine8h = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(maintenant)) add(Calendar.DAY_OF_YEAR, 1)
    }

    val delaiInitial = prochaine8h.timeInMillis - maintenant.timeInMillis

    val requete = PeriodicWorkRequestBuilder<DocumentsWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(delaiInitial, TimeUnit.MILLISECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "verification_documents",
        ExistingPeriodicWorkPolicy.KEEP,
        requete
    )
}