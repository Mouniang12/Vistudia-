package ca.uqac.vistudia;

import android.app.Application
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ca.uqac.vistudia.Models.RetrofitClient
import ca.uqac.vistudia.utils.DocumentsWorker
import ca.uqac.vistudia.utils.NotificationHelper
import ca.uqac.vistudia.utils.planifierVerificationDocuments

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        NotificationHelper.creerCanal(this)
        planifierVerificationDocuments(this)

       // testerNotificationImmédiate(this)
    }
}

fun testerNotificationImmédiate(context: Context) {
    val requeteImmediate = OneTimeWorkRequestBuilder<DocumentsWorker>().build()
    WorkManager.getInstance(context).enqueue(requeteImmediate)
}