package es.michaelcharl.pokejisho

import android.app.Application

class PokeJishoApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
