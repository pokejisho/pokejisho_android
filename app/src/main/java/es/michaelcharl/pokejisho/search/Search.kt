package es.michaelcharl.pokejisho.search

import es.michaelcharl.pokejisho.data.DictionaryEntry
import es.michaelcharl.pokejisho.data.DictionaryStore
import es.michaelcharl.pokejisho.data.EntryType

private fun String.canonical(): String =
    replace("é", "e").lowercase().replace(" ", "")

fun DictionaryStore.search(query: String, filter: EntryType? = null): SearchResults {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return SearchResults.EMPTY

    val normalized = trimmed.replace("é", "e")
    val cTerm = normalized.lowercase().replace(" ", "")
    val katakanaTerm = hiraganaToKatakana(normalized)

    val priority = mutableListOf<DictionaryEntry>()
    val results = mutableListOf<DictionaryEntry>()

    for (entry in entries) {
        if (filter != null && entry.type != filter) continue
        val cEnglish = entry.english.canonical()
        val cRomaji = entry.romaji.canonical()
        val matches = cEnglish.contains(cTerm) ||
            entry.katakana.contains(katakanaTerm) ||
            cRomaji.contains(cTerm)
        if (!matches) continue
        val isExact = cEnglish == cTerm || entry.katakana == katakanaTerm || cRomaji == cTerm
        if (isExact) priority.add(entry) else results.add(entry)
    }
    return SearchResults(priority, results)
}
