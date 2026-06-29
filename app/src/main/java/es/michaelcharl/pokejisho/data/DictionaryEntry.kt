package es.michaelcharl.pokejisho.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EntryTypeSerializer::class)
enum class EntryType(val raw: String) {
    POKEMON("pokemon"),
    ABILITY("ability"),
    ITEM("item"),
    MOVE("move"),
    NATURE("nature"),
    CHARACTER("character"),
    LOCATION("location"),
}

object EntryTypeSerializer : KSerializer<EntryType> {
    override val descriptor = PrimitiveSerialDescriptor("EntryType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EntryType) = encoder.encodeString(value.raw)

    override fun deserialize(decoder: Decoder): EntryType =
        when (val raw = decoder.decodeString().lowercase()) {
            "pokémon", "pokemon" -> EntryType.POKEMON
            "ability" -> EntryType.ABILITY
            "item" -> EntryType.ITEM
            "move" -> EntryType.MOVE
            "nature" -> EntryType.NATURE
            "character" -> EntryType.CHARACTER
            "location" -> EntryType.LOCATION
            else -> throw SerializationException("Unknown entry type: $raw")
        }
}

@Serializable
data class DictionaryEntry(
    val type: EntryType,
    val english: String,
    val japanese: String,
    val katakana: String,
    val romaji: String,
) {
    val id: String get() = "${type.raw}|$english|$japanese"
}
