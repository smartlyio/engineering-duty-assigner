package koin.ext

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.lifecycle.GroupScope
import org.jetbrains.spek.api.lifecycle.LifecycleListener
import org.jetbrains.spek.api.lifecycle.TestScope
import org.koin.KoinContext
import org.koin.core.parameter.Parameters
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext

inline fun <reified T> SpecBody.inject(name: String = "", noinline parameters: Parameters = { emptyMap()}) =
    lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, parameters)}

abstract class KoinSpek(modules: List<Module>, val koinSpec: Spec.() -> Unit): Spek({
    registerListener(object: LifecycleListener {
        override fun beforeExecuteTest(test: TestScope) {
            StandAloneContext.closeKoin()
            StandAloneContext.startKoin(modules)
        }

        override fun afterExecuteGroup(group: GroupScope) {
            StandAloneContext.closeKoin()
        }
    })

    koinSpec.invoke(this)
})