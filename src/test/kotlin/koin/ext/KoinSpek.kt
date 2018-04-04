package koin.ext

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.lifecycle.GroupScope
import org.jetbrains.spek.api.lifecycle.LifecycleListener
import org.jetbrains.spek.api.lifecycle.TestScope
import org.koin.dsl.module.Module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

class KoinSpec(val root: Spec) : KoinComponent, Spec by root

abstract class KoinSpek(modules: List<Module>, koinSpec: KoinSpec.() -> Unit): Spek({
    registerListener(object: LifecycleListener {
        override fun beforeExecuteTest(test: TestScope) {
            StandAloneContext.closeKoin()
            StandAloneContext.startKoin(modules)
        }

        override fun afterExecuteGroup(group: GroupScope) {
            StandAloneContext.closeKoin()
        }
    })

    koinSpec.invoke(KoinSpec(this))
})