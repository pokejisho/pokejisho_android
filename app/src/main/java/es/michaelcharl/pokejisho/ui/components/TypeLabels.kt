package es.michaelcharl.pokejisho.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.michaelcharl.pokejisho.R
import es.michaelcharl.pokejisho.data.EntryType

@StringRes
fun EntryType.labelRes(): Int = when (this) {
    EntryType.POKEMON -> R.string.type_pokemon
    EntryType.ABILITY -> R.string.type_ability
    EntryType.ITEM -> R.string.type_item
    EntryType.MOVE -> R.string.type_move
    EntryType.NATURE -> R.string.type_nature
    EntryType.CHARACTER -> R.string.type_character
    EntryType.LOCATION -> R.string.type_location
}

@Composable
fun typeLabel(type: EntryType): String = stringResource(type.labelRes())
