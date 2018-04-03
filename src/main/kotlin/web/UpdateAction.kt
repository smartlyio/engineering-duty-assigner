package web

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateAction(
    @JsonProperty("@type") val type: String,
    val name: String,
    val agent: Agent,
    val target: Target
) {
    data class Agent(
        @JsonProperty("@type") val type: String,
        val name: String,
        val url: String,
        val image: String
    )

    data class Target(
        @JsonProperty("@type") val type: String,
        val urlTemplate: String,
        val httpMethod: String
    )
}