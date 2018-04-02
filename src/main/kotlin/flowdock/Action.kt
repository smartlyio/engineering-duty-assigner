package flowdock

sealed class Action;

data class UpdateThread(val data: Int) : Action();