package flowdock

import flowdock.model.Activity

sealed class Action;

data class CreateActivity(val activity: Activity): Action()
