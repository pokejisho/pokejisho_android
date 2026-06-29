package es.michaelcharl.pokejisho.data

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val dictionaryJson = Json { ignoreUnknownKeys = true }

fun parseDictionary(jsonText: String): List<DictionaryEntry> =
    dictionaryJson.decodeFromString(ListSerializer(DictionaryEntry.serializer()), jsonText)
