package es.michaelcharl.pokejisho.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import es.michaelcharl.pokejisho.data.DictionaryStore
import es.michaelcharl.pokejisho.data.EntryType
import es.michaelcharl.pokejisho.search.SearchResults
import es.michaelcharl.pokejisho.search.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class SearchViewModel(private val store: DictionaryStore) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filter = MutableStateFlow<EntryType?>(null)
    val filter: StateFlow<EntryType?> = _filter.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: StateFlow<SearchResults> =
        combine(_query, _filter) { q, f -> q to f }
            .debounce(120)
            .mapLatest { (q, f) -> withContext(Dispatchers.Default) { store.search(q, f) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResults.EMPTY)

    val hasQuery: StateFlow<Boolean> =
        _query.map { it.trim().isNotEmpty() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setQuery(value: String) { _query.value = value }
    fun setFilter(value: EntryType?) { _filter.value = value }

    companion object {
        fun factory(store: DictionaryStore): ViewModelProvider.Factory = viewModelFactory {
            initializer { SearchViewModel(store) }
        }
    }
}
