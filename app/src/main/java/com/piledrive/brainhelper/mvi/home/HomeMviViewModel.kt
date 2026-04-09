package com.piledrive.brainhelper.mvi.home

import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.datastore.SessionDataStore
import com.piledrive.brainhelper.mvi.BaseMviViewModel
import com.piledrive.brainhelper.repo.AuthRepo
import com.piledrive.brainhelper.repo.FamiliesRepo
import com.piledrive.brainhelper.repo.FullNotesRepo
import com.piledrive.brainhelper.repo.ProfilesRepo
import com.piledrive.brainhelper.repo.TagsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeMviViewModel @Inject constructor(
	private val dataStore: SessionDataStore,
	private val profilesRepo: ProfilesRepo,
	private val familiesRepo: FamiliesRepo,
	private val notesRepo: FullNotesRepo,
	private val authRepo: AuthRepo,
	private val tagsRepo: TagsRepo
) : BaseMviViewModel<HomeContract.Intent, HomeContract.State, HomeContract.Effect>(
	initialState = HomeContract.State()
) {

	init {
		handleIntent(HomeContract.Intent.LoadData)
		observeAuthStatus()
		observeDataChanges()
	}

	override fun handleIntent(intent: HomeContract.Intent) {
		logIntent(intent)
		when (intent) {
			is HomeContract.Intent.LoadData -> loadData()
			is HomeContract.Intent.Refresh -> refresh()
			is HomeContract.Intent.Logout -> logout()
			is HomeContract.Intent.SelectFamily -> selectFamily(intent.familyId)
			is HomeContract.Intent.SelectTag -> selectTag(intent.tag)
			is HomeContract.Intent.DeselectTag -> deselectTag(intent.tag)
			is HomeContract.Intent.ClearTagSelection -> clearTagSelection()
			is HomeContract.Intent.OpenNote -> openNote(intent.note)
			is HomeContract.Intent.OpenScratchPad -> openScratchPad()
		}
	}

	private fun loadData() {
		setState { copy(isLoading = true, errorMessage = null) }
		// Data will be loaded through repository flows
	}

	private fun refresh() {
		setState { copy(isRefreshing = true, errorMessage = null) }
		viewModelScope.launch {
			try {
				// Trigger repository refreshes if needed
				// For now, just simulate refresh completion
				setState { copy(isRefreshing = false) }
				sendEffect(HomeContract.Effect.ShowSuccess("Data refreshed"))
			} catch (e: Exception) {
				setState {
					copy(
						isRefreshing = false,
						errorMessage = e.message ?: "Refresh failed"
					)
				}
				sendEffect(HomeContract.Effect.ShowError(e.message ?: "Refresh failed"))
			}
		}
	}

	private fun logout() {
		viewModelScope.launch {
			try {
				authRepo.logout()
				sendEffect(HomeContract.Effect.NavigateToAuth)
			} catch (e: Exception) {
				sendEffect(HomeContract.Effect.ShowError("Logout failed: ${e.message}"))
			}
		}
	}

	private fun selectFamily(familyId: String) {
		viewModelScope.launch {
			dataStore.updateActiveFamilyId(familyId)
			setState { copy(selectedFamilyId = familyId) }
		}
	}

	private fun selectTag(tag: HomeContract.FullTag) {
		setState {
			copy(
				selectedTags = if (selectedTags.contains(tag)) {
					selectedTags
				} else {
					selectedTags + tag
				}
			)
		}
	}

	private fun deselectTag(tag: HomeContract.FullTag) {
		setState {
			copy(selectedTags = selectedTags - tag)
		}
	}

	private fun clearTagSelection() {
		setState { copy(selectedTags = emptyList()) }
	}

	private fun openNote(note: HomeContract.Note?) {
		sendEffect(HomeContract.Effect.NavigateToNoteDetails(note?.id))
	}

	private fun openScratchPad() {
		sendEffect(HomeContract.Effect.NavigateToScratchPad)
	}

	private fun observeAuthStatus() {
		viewModelScope.launch {
			authRepo.grabAuthStatusFlow().collect { status ->
				when (status) {
					is SessionStatus.NotAuthenticated -> {
						sendEffect(HomeContract.Effect.NavigateToAuth)
					}

					else -> { /* Handle other auth states if needed */
					}
				}
			}
		}
	}

	private fun observeDataChanges() {
		// Observe self profile
		viewModelScope.launch {
			profilesRepo.watchSelfProfile().collect { profile ->
				setState { copy(selfProfile = profile) }
			}
		}

		// Observe families and auto-select first one if needed
		viewModelScope.launch {
			familiesRepo.watchContent().collect { families ->
				val currentFamilyId = dataStore.checkActiveFamilyId()
				val validFamilyId = if (families.isNotEmpty() &&
					(currentFamilyId == null || families.none { it.id == currentFamilyId })
				) {
					val firstFamilyId = families.firstOrNull()?.id
					firstFamilyId?.let { dataStore.updateActiveFamilyId(it) }
					firstFamilyId
				} else {
					currentFamilyId
				}

				setState {
					copy(
						families = families,
						selectedFamilyId = validFamilyId,
						isLoading = false
					)
				}
			}
		}

		// Observe family members
		viewModelScope.launch {
			profilesRepo.watchContent().collect { profiles ->
				setState { copy(familyMembers = profiles) }
			}
		}

		// Observe notes
		viewModelScope.launch {
			notesRepo.watchContent().collect { notes ->
				setState { copy(notes = notes) }
			}
		}

		// Observe tags
		viewModelScope.launch {
			combine(
				tagsRepo.watchContent(),
				profilesRepo.watchContent()
			) { tags, profiles ->
				// Transform tags with profile information if needed
				tags // For now, just pass through
			}.collect { tags ->
				setState { copy(availableTags = tags) }
			}
		}

		// Observe active family changes
		viewModelScope.launch {
			dataStore.watchActiveFamilyId().collect { familyId ->
				setState { copy(selectedFamilyId = familyId) }
			}
		}
	}
}