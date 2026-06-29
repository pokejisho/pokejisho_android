package es.michaelcharl.pokejisho.data

import android.content.Context

class DictionaryStore(val entries: List<DictionaryEntry>) {
    private val byId: Map<String, DictionaryEntry> = run {
        val map = mutableMapOf<String, DictionaryEntry>()
        for (entry in entries) {
            map.putIfAbsent(entry.id, entry)
        }
        map
    }

    /** Resolves stored ids (e.g. favorites) back to entries, skipping unknown ids. */
    fun entries(ids: Iterable<String>): List<DictionaryEntry> = ids.mapNotNull { byId[it] }
}

fun loadBundledDictionary(context: Context): DictionaryStore {
    val text = context.assets.open("jisho.json").bufferedReader().use { it.readText() }
    return DictionaryStore(parseDictionary(text))
}
