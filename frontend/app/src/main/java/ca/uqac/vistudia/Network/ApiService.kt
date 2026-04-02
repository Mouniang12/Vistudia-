package ca.uqac.vistudia.Network

import ca.uqac.vistudia.Models.ChecklistPartage
import ca.uqac.vistudia.Models.ChecklistResponse
import ca.uqac.vistudia.Models.Destination
import ca.uqac.vistudia.Models.DocumentItem
import ca.uqac.vistudia.Models.ForumMessage
import ca.uqac.vistudia.Models.ForumSalon
import ca.uqac.vistudia.Models.GuideImmigrationDetail
import ca.uqac.vistudia.Models.GuideImmigrationItem
import ca.uqac.vistudia.Models.Historique
import ca.uqac.vistudia.Models.LoginRequest
import ca.uqac.vistudia.Models.PartageResponse
import ca.uqac.vistudia.Models.User
import ca.uqac.vistudia.Models.UserProfile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {
    // User
    @POST("api/users/register")
    fun register(@Body user: User): Call<Map<String, Any>>

    @POST("api/users/login")
    fun login(@Body login: LoginRequest): Call<Map<String, Any>>

    @GET("api/users/profile")
    fun profile(): Call<Map<String, Any>>

    @POST("api/users/logout")
    fun logout(): Call<Map<String, Any>>

    @POST("api/users/resend-verification")
    fun resendVerification(@Body body: Map<String, String>): Call<Map<String, Any>>

    @POST("api/users/forgot-password")
    fun forgotPassword(@Body body: Map<String, String>): Call<Map<String, Any>>

    @GET("api/users/profile")
    fun getProfile(): Call<UserProfile>

    @PUT("api/users/profile")
    fun updateProfile(@Body body: Map<String, String>): Call<Map<String, Any>>

    @PUT("api/users/change-password")
    fun changePassword(@Body body: Map<String, String>): Call<Map<String, Any>>

    // Checklist
    @GET("api/checklist/destinations")
    fun getDestinations(): Call<List<Destination>>

    @GET("api/checklist/historique/all")
    fun getHistorique(): Call<List<Historique>>

    @GET("api/checklist/mes-destinations")
    fun getMesDestinations(): Call<List<Destination>>

    @POST("api/checklist/mes-destinations")
    fun ajouterDestination(@Body body: Map<String, String>): Call<Map<String, Any>>

    @DELETE("api/checklist/mes-destinations/{destinationId}")
    fun supprimerDestination(
        @Path("destinationId") destinationId: String
    ): Call<Map<String, Any>>

    @GET("api/checklist/{destinationId}")
    fun getChecklist(@Path("destinationId") destinationId: String): Call<ChecklistResponse>

    @POST("api/checklist/{destinationId}/{demarcheId}")
    fun cocherDemarche(
        @Path("destinationId") destinationId: String,
        @Path("demarcheId") demarcheId: String,
        @Body body: Map<String, String>
    ): Call<Map<String, Any>>

    // Partage
    @POST("api/checklist/partage")
    fun genererPartage(@Body body: Map<String, String>): Call<PartageResponse>

    @GET("api/checklist/partage-data/{token}")
    fun getChecklistPartage(@Path("token") token: String): Call<ChecklistPartage>

    @POST("api/checklist/partage/{token}/{demarcheId}")
    fun cocherDemarchePartage(
        @Path("token") token: String,
        @Path("demarcheId") demarcheId: String,
        @Body body: Map<String, String>
    ): Call<Map<String, Any>>

    // Documents
    @GET("api/documents/")
    fun getMesDocuments(): Call<List<DocumentItem>>

    @POST("api/documents/")
    fun ajouterDocument(@Body body: Map<String, String>): Call<Map<String, Any>>

    @PUT("api/documents/{docId}")
    fun modifierDocument(
        @Path("docId") docId: String,
        @Body body: Map<String, String>
    ): Call<Map<String, Any>>

    @DELETE("api/documents/{docId}")
    fun supprimerDocument(@Path("docId") docId: String): Call<Map<String, Any>>

    // Guide Immigration
    @GET("api/immigration/pays-origine")
    fun getPaysOrigine(): Call<List<String>>

    @GET("api/immigration/destinations/{paysOrigine}")
    fun getDestinationsImmigration(
        @Path("paysOrigine") paysOrigine: String
    ): Call<List<GuideImmigrationItem>>

    @GET("api/immigration/guide/{guideId}")
    fun getGuideImmigration(
        @Path("guideId") guideId: String
    ): Call<GuideImmigrationDetail>

    // Forum
    @GET("api/forum/salons")
    fun getSalons(): Call<List<ForumSalon>>

    @POST("api/forum/salons")
    fun createSalon(@Body body: Map<String, String>): Call<ForumSalon>

    @GET("api/forum/salons/{salonId}/messages")
    fun getMessages(@Path("salonId") salonId: String): Call<List<ForumMessage>>

    @POST("api/forum/salons/{salonId}/messages")
    fun postMessage(@Path("salonId") salonId: String, @Body message: ForumMessage): Call<ForumMessage>
}
