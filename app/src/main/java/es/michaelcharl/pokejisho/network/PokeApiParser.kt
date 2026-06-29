package es.michaelcharl.pokejisho.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object PokeApiParser {
    fun parse(jsonText: String): PokeApiDetail {
        val root = Json.parseToJsonElement(jsonText).jsonObject
        val facts = mutableListOf<PokeApiDetail.Fact>()

        val sprite = root["sprites"]?.jsonObject?.get("front_default")?.jsonPrimitive?.contentOrNull

        root["types"]?.jsonArray?.let { arr ->
            val names = arr.mapNotNull {
                it.jsonObject["type"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull
            }
            if (names.isNotEmpty()) facts += PokeApiDetail.Fact("Type", names.joinToString(", "))
        }
        root["height"]?.jsonPrimitive?.intOrNull?.let { facts += PokeApiDetail.Fact("Height", "${it / 10.0} m") }
        root["weight"]?.jsonPrimitive?.intOrNull?.let { facts += PokeApiDetail.Fact("Weight", "${it / 10.0} kg") }
        root["power"]?.jsonPrimitive?.intOrNull?.let { facts += PokeApiDetail.Fact("Power", "$it") }
        root["pp"]?.jsonPrimitive?.intOrNull?.let { facts += PokeApiDetail.Fact("PP", "$it") }
        root["accuracy"]?.jsonPrimitive?.intOrNull?.let { facts += PokeApiDetail.Fact("Accuracy", "$it") }

        val flavor = root["flavor_text_entries"]?.jsonArray
            ?.firstOrNull {
                it.jsonObject["language"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull == "en"
            }
            ?.jsonObject?.get("flavor_text")?.jsonPrimitive?.contentOrNull
            ?.replace("\n", " ")
            ?.replace("\u000C", " ")

        return PokeApiDetail(spriteUrl = sprite, facts = facts, flavorText = flavor)
    }
}
