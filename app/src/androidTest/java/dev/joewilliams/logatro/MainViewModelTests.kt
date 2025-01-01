package dev.joewilliams.logatro

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelTests {

    companion object {
        private lateinit var model: MainViewModel
        @BeforeClass
        @JvmStatic
        fun classStartup() {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            model = MainViewModel(appContext.resources)
            while (!model.ready) {}
        }
    }

    @Test
    fun loadDictionaryTest() {
        assert(model.checkWord("BAT"))
        assert(!model.checkWord("DA"))
    }
}