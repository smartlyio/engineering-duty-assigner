package koin.ext

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.TestBody
import org.jetbrains.spek.api.lifecycle.GroupScope
import org.jetbrains.spek.api.lifecycle.LifecycleListener
import org.jetbrains.spek.api.lifecycle.TestScope
import org.koin.KoinContext
import org.koin.core.parameter.Parameters
import org.koin.dsl.module.Module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin

class KoinSpec(val root: Spec) : KoinComponent, Spec by root

abstract class KoinSpek(koinSpec: KoinSpec.() -> Unit): Spek({
    registerListener(object: LifecycleListener {
        override fun afterExecuteGroup(group: GroupScope) {
            closeKoin()
        }
    })

    koinSpec.invoke(KoinSpec(this))
})

fun TestBody.dryRun(defaultParameters: Parameters = { emptyMap() }) {
    (StandAloneContext.koinContext as KoinContext).dryRun(defaultParameters)
}
