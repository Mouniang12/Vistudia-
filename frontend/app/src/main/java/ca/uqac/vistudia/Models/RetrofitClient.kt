package ca.uqac.vistudia.Models

import android.content.Context
import ca.uqac.vistudia.BuildConfig
import ca.uqac.vistudia.Network.ApiService
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // CookieJar qui sauvegarde le token dans SharedPreferences
    private val persistentCookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val prefs = appContext?.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
            cookies.find { it.name == "token" }?.let { cookie ->
                prefs?.edit()?.putString("auth_token", cookie.value)?.apply()
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val prefs = appContext?.getSharedPreferences("vistudia_prefs", Context.MODE_PRIVATE)
            val token = prefs?.getString("auth_token", "") ?: ""
            if (token.isEmpty()) return emptyList()
            return listOf(
                Cookie.Builder()
                    .name("token")
                    .value(token)
                    .domain(url.host)
                    .build()
            )
        }
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(persistentCookieJar)
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}