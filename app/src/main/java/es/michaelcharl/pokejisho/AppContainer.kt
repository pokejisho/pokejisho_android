package es.michaelcharl.pokejisho

import android.content.Context
import es.michaelcharl.pokejisho.data.DictionaryStore
import es.michaelcharl.pokejisho.data.UserDataRepository
import es.michaelcharl.pokejisho.data.loadBundledDictionary
import es.michaelcharl.pokejisho.network.PokeApiService

/** Manual dependency container; mirrors the iOS app constructing the store once at launch. */
class AppContainer(context: Context) {
    val dictionaryStore: DictionaryStore = loadBundledDictionary(context)
    val userData: UserDataRepository = UserDataRepository(context)
    val pokeApiService: PokeApiService = PokeApiService()
}
