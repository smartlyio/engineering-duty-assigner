package google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

object Factories {
    public val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    public val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
}