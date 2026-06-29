package es.michaelcharl.pokejisho.search

import es.michaelcharl.pokejisho.data.DictionaryEntry

data class SearchResults(
    val priority: List<DictionaryEntry>,
    val results: List<DictionaryEntry>,
) {
    val isEmpty: Boolean get() = priority.isEmpty() && results.isEmpty()

    companion object {
        val EMPTY = SearchResults(emptyList(), emptyList())
    }
}
