package es.michaelcharl.pokejisho.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

class UserDataRepository(private val context: Context) {
    private val favoritesKey = stringSetPreferencesKey("favorites")
    private val recentsKey = stringPreferencesKey("recents")
    private val appearanceKey = stringPreferencesKey("appearance")
    private val json = Json

    val favoritesFlow: Flow<Set<String>> =
        context.dataStore.data.map { it[favoritesKey] ?: emptySet() }

    val recentsFlow: Flow<List<String>> =
        context.dataStore.data.map { prefs ->
            prefs[recentsKey]?.let { json.decodeFromString(ListSerializer(String.serializer()), it) }
                ?: emptyList()
        }

    val appearanceFlow: Flow<AppAppearance> =
        context.dataStore.data.map { appAppearanceFrom(it[appearanceKey]) }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey] ?: emptySet()
            prefs[favoritesKey] = if (id in current) current - id else current + id
        }
    }

    suspend fun removeFavorites(ids: Collection<String>) {
        context.dataStore.edit { prefs ->
            prefs[favoritesKey] = (prefs[favoritesKey] ?: emptySet()) - ids.toSet()
        }
    }

    suspend fun addRecent(term: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[recentsKey]
                ?.let { json.decodeFromString(ListSerializer(String.serializer()), it) }
                ?: emptyList()
            prefs[recentsKey] = json.encodeToString(
                ListSerializer(String.serializer()), updatedRecents(current, term)
            )
        }
    }

    suspend fun removeRecent(term: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[recentsKey]
                ?.let { json.decodeFromString(ListSerializer(String.serializer()), it) }
                ?: emptyList()
            prefs[recentsKey] = json.encodeToString(
                ListSerializer(String.serializer()), current.filterNot { it == term }
            )
        }
    }

    suspend fun setAppearance(appearance: AppAppearance) {
        context.dataStore.edit { it[appearanceKey] = appearance.raw }
    }
}
