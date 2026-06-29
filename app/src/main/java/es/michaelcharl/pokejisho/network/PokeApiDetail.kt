package es.michaelcharl.pokejisho.network

data class PokeApiDetail(
    val spriteUrl: String?,
    val facts: List<Fact>,
    val flavorText: String?,
) {
    data class Fact(val label: String, val value: String)
}
