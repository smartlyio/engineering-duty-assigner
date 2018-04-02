package flowdock

import flowdock.model.Activity

interface IFlowdockAPI {
    fun createActivity(activity: Activity)
}
