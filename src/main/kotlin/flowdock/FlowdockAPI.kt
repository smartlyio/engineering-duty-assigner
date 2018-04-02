package flowdock

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import flowdock.model.Activity
import java.nio.charset.Charset

const val FLOWDOCK_URL = "https://api.flowdock.com"

class FlowdockAPI(val flowToken: String) : IFlowdockAPI {
    override fun createActivity(activity: Activity) {
        val payload = objectMapper(activity::class.java)
            .writerFor(activity::class.java)
            .withAttribute("flow_token", flowToken)
            .writeValueAsString(activity)

        Fuel.post("${FLOWDOCK_URL}/messages")
            .body(payload, Charset.forName("UTF-8"))
            .response { request, response, result -> println("GOT STATUS CODE: ${response.statusCode}") }
    }

    private fun <T> objectMapper(targetClass: Class<T>): ObjectMapper =
        ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .addMixIn(targetClass, FlowTokenMixin::class.java)

}