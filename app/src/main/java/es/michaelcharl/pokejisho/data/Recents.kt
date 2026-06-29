package es.michaelcharl.pokejisho.data

/** Mirrors iOS `UserData.addRecent`: trim, require length ≥ 2, dedupe, newest-first, cap at 15. */
fun updatedRecents(current: List<String>, term: String): List<String> {
    val t = term.trim()
    if (t.length < 2) return current
    return (listOf(t) + current.filterNot { it == t }).take(15)
}
