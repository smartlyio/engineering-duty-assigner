package koin.ext


import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.koin.dsl.module.applicationContext
import org.koin.standalone.inject
import KoinModule
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin

interface ISomeMockedInterface {
    fun foo(a: String, b: String)
}

val testKoinModule = applicationContext {
    bean { mock<ISomeMockedInterface>() }
}

class KoinSpekSpec : KoinSpek({
    beforeEachTest {
        closeKoin()
        startKoin(listOf(testKoinModule))
    }

    describe("koinspek test") {
        it("first calendar") {
            val mockedInterface: ISomeMockedInterface by inject()
            mockedInterface.foo("foo", "bar")

            verify(mockedInterface).foo(eq("foo"), eq("bar"))
        }

        it("second calendar") {
            val mockedInterface: ISomeMockedInterface by inject()
            verify(mockedInterface, never()).foo(eq("foo"), eq("bar"))
        }

        it("prints dryRun") {
            this.dryRun()
        }
    }
})