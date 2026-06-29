package es.michaelcharl.pokejisho.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.michaelcharl.pokejisho.R
import es.michaelcharl.pokejisho.data.DictionaryEntry
import es.michaelcharl.pokejisho.data.DictionaryStore
import es.michaelcharl.pokejisho.data.EntryType
import es.michaelcharl.pokejisho.ui.components.EntryRow
import es.michaelcharl.pokejisho.ui.components.typeLabel
import es.michaelcharl.pokejisho.ui.state.SearchViewModel
import es.michaelcharl.pokejisho.ui.state.UserDataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    userDataViewModel: UserDataViewModel,
    store: DictionaryStore,
    onOpenEntry: (DictionaryEntry) -> Unit,
    onOpenSettings: () -> Unit,
    onScan: () -> Unit,
) {
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    val filter by searchViewModel.filter.collectAsStateWithLifecycle()
    val results by searchViewModel.results.collectAsStateWithLifecycle()
    val hasQuery by searchViewModel.hasQuery.collectAsStateWithLifecycle()
    val favoriteIds by userDataViewModel.favorites.collectAsStateWithLifecycle()
    val recents by userDataViewModel.recents.collectAsStateWithLifecycle()

    val favorites = remember(favoriteIds) {
        store.entries(favoriteIds).sortedBy { it.english }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    FilterMenu(
                        selected = filter,
                        onSelect = { searchViewModel.setFilter(it) },
                    )
                },
                actions = {
                    IconButton(onClick = onScan) {
                        Icon(Icons.Filled.PhotoCamera, stringResource(R.string.scan_button))
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, stringResource(R.string.settings_title))
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { searchViewModel.setQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true,
            )

            when {
                !hasQuery -> LandingContent(
                    favorites = favorites,
                    recents = recents,
                    onOpenEntry = onOpenEntry,
                    onSelectRecent = { searchViewModel.setQuery(it) },
                    onRemoveFavorite = { userDataViewModel.removeFavorites(listOf(it)) },
                    onRemoveRecent = { userDataViewModel.removeRecent(it) },
                )

                results.isEmpty -> NoResults()

                else -> ResultsList(
                    priority = results.priority,
                    results = results.results,
                    onOpenEntry = onOpenEntry,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterMenu(selected: EntryType?, onSelect: (EntryType?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.AutoMirrored.Filled.List, stringResource(R.string.filter_title))
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.filter_all)) },
            trailingIcon = { if (selected == null) Icon(Icons.Filled.Check, null) },
            onClick = { onSelect(null); expanded = false },
        )
        EntryType.entries.forEach { type ->
            DropdownMenuItem(
                text = { Text(typeLabel(type)) },
                trailingIcon = { if (selected == type) Icon(Icons.Filled.Check, null) },
                onClick = { onSelect(type); expanded = false },
            )
        }
    }
}

@Composable
private fun ResultsList(
    priority: List<DictionaryEntry>,
    results: List<DictionaryEntry>,
    onOpenEntry: (DictionaryEntry) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(priority, key = { "p:${it.id}" }) { entry ->
            EntryRow(entry, Modifier.fillMaxWidth().clickable { onOpenEntry(entry) }.padding(horizontal = 16.dp))
            HorizontalDivider()
        }
        if (priority.isNotEmpty() && results.isNotEmpty()) {
            item { HorizontalDivider(thickness = 6.dp, color = MaterialTheme.colorScheme.surfaceVariant) }
        }
        items(results, key = { "r:${it.id}" }) { entry ->
            EntryRow(entry, Modifier.fillMaxWidth().clickable { onOpenEntry(entry) }.padding(horizontal = 16.dp))
            HorizontalDivider()
        }
    }
}

@Composable
private fun NoResults() {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.Search, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.results_none_title), style = MaterialTheme.typography.titleMedium)
        Text(
            stringResource(R.string.results_none),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LandingContent(
    favorites: List<DictionaryEntry>,
    recents: List<String>,
    onOpenEntry: (DictionaryEntry) -> Unit,
    onSelectRecent: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onRemoveRecent: (String) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize()) {
        item { MascotBubble() }

        if (favorites.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.favorites_title)) }
            items(favorites, key = { "fav:${it.id}" }) { entry ->
                SwipeToDelete(onDelete = { onRemoveFavorite(entry.id) }) {
                    EntryRow(
                        entry,
                        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                            .clickable { onOpenEntry(entry) }.padding(horizontal = 16.dp),
                    )
                }
                HorizontalDivider()
            }
        }

        if (recents.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.recent_title)) }
            items(recents, key = { "rec:$it" }) { term ->
                SwipeToDelete(onDelete = { onRemoveRecent(term) }) {
                    Row(
                        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                            .clickable { onSelectRecent(term) }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(Icons.Filled.History, null)
                        Text(term)
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun MascotBubble() {
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            Modifier.weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(12.dp),
        ) {
            Text(stringResource(R.string.help_welcome), fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.help_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Image(painterResource(R.drawable.mascot), null, Modifier.size(96.dp))
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDelete(onDelete: () -> Unit, content: @Composable () -> Unit) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        },
    )
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer))
        },
        content = { content() },
    )
}
