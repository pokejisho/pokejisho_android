package es.michaelcharl.pokejisho.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import es.michaelcharl.pokejisho.data.AppAppearance
import es.michaelcharl.pokejisho.data.UserDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserDataViewModel(private val repo: UserDataRepository) : ViewModel() {
    val favorites: StateFlow<Set<String>> =
        repo.favoritesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val recents: StateFlow<List<String>> =
        repo.recentsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appearance: StateFlow<AppAppearance> =
        repo.appearanceFlow.stateIn(viewModelScope, SharingStarted.Eagerly, AppAppearance.SYSTEM)

    fun toggleFavorite(id: String) = launchEdit { repo.toggleFavorite(id) }
    fun removeFavorites(ids: Collection<String>) = launchEdit { repo.removeFavorites(ids) }
    fun addRecent(term: String) = launchEdit { repo.addRecent(term) }
    fun removeRecent(term: String) = launchEdit { repo.removeRecent(term) }
    fun setAppearance(appearance: AppAppearance) = launchEdit { repo.setAppearance(appearance) }

    private fun launchEdit(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    companion object {
        fun factory(repo: UserDataRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { UserDataViewModel(repo) }
        }
    }
}
