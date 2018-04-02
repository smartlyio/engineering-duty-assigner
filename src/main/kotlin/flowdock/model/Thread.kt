package flowdock.model

sealed class ThreadAction;

data class ViewAction(
    val type: String = "ViewAction",
    val url: String,
    val name: String
) : ThreadAction()

data class UpdateAction(
    val type: String = "UpdateAction",
    val name: String,
    val target: Target
): ThreadAction() {
    data class Target(
        val type: String = "EntryPoint",
        val urlTemplate: String,
        val httpMethod: String
    )
}

data class Thread(
    val title: String,
    val body: String? = null,
    val externalUrl: String? = null,
    val status: Status? = null,
    val actions: List<ThreadAction>? = null,
    val fields: Map<String, String>? = null
) {
    data class Status(val value: String, val color: String)
}