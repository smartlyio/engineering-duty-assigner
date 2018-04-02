package flowdock

interface IFlowdockAPI {
    fun execute(events: List<Action>)
}
