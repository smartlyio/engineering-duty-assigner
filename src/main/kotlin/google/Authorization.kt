package google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.CalendarScopes
import java.io.File
import java.io.InputStreamReader

object Authorization {
    private val SCOPES = listOf(CalendarScopes.CALENDAR)
    private val DATA_STORE_DIR = File(System.getProperty("user.home"), ".credentials/duty-assigner")
    private val DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)

    fun authorize(): Credential {
        val inputStream = this::class.java.getResourceAsStream("/client_secret.json")
        val clientSecrets = GoogleClientSecrets.load(Factories.JSON_FACTORY, InputStreamReader(inputStream))

        val flow = GoogleAuthorizationCodeFlow.Builder(
            Factories.HTTP_TRANSPORT, Factories.JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build()

        val credential = AuthorizationCodeInstalledApp(
            flow, LocalServerReceiver.Builder().setPort(54470).build()
        ).authorize("user")

        return credential
    }
}