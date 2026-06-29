package es.michaelcharl.pokejisho.network

import es.michaelcharl.pokejisho.data.EntryType

enum class PokeApiResource(val path: String) {
    POKEMON("pokemon"),
    MOVE("move"),
    ITEM("item"),
    ABILITY("ability"),
    NATURE("nature"),
}

fun pokeApiResource(type: EntryType): PokeApiResource? = when (type) {
    EntryType.POKEMON -> PokeApiResource.POKEMON
    EntryType.MOVE -> PokeApiResource.MOVE
    EntryType.ITEM -> PokeApiResource.ITEM
    EntryType.ABILITY -> PokeApiResource.ABILITY
    EntryType.NATURE -> PokeApiResource.NATURE
    EntryType.CHARACTER, EntryType.LOCATION -> null
}

fun pokeApiSlug(english: String): String {
    var s = english.lowercase()
    for (ch in listOf("'", ".", ":", ",", "(", ")")) s = s.replace(ch, "")
    s = s.replace(" ", "-").replace("--", "-")
    return s
}
