package com.piledrive.brainhelper.mvi.home

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.model.composite.FullNote
import com.piledrive.brainhelper.data.model.composite.FullTag
import com.piledrive.brainhelper.mvi.MviContract

/**
 * MVI Contract for Home Screen
 */
object HomeContract {

	/**
	 * User intents for home screen
	 */
	sealed interface Intent : MviContract.Intent {
		data object LoadData : Intent
		data object Refresh : Intent
		data object Logout : Intent
		data class SelectFamily(val familyId: String) : Intent
		data class SelectTag(val tag: FullTag) : Intent
		data class DeselectTag(val tag: FullTag) : Intent
		data object ClearTagSelection : Intent
		data class OpenNote(val note: Note?) : Intent
		data object OpenScratchPad : Intent
	}

	/**
	 * Home screen state
	 */
	data class State(
		val isLoading: Boolean = true,
		val selfProfile: Profile? = null,
		val families: List<Family> = emptyList(),
		val selectedFamilyId: String? = null,
		val familyMembers: List<Profile> = emptyList(),
		val notes: List<FullNote> = emptyList(),
		val availableTags: List<FullTag> = emptyList(),
		val selectedTags: List<FullTag> = emptyList(),
		val errorMessage: String? = null,
		val isRefreshing: Boolean = false
	) : MviContract.State {

		val selectedFamily: Family?
			get() = families.firstOrNull { it.id == selectedFamilyId }

		val filteredNotes: List<FullNote>
			get() = if (selectedTags.isEmpty()) {
				notes
			} else {
				notes.filter { note ->
					selectedTags.any { selectedTag ->
						note.tags.any { noteTag -> noteTag.id == selectedTag.id }
					}
				}
			}
	}

	/**
	 * One-time effects for home screen
	 */
	sealed interface Effect : MviContract.Effect {
		data object NavigateToAuth : Effect
		data object NavigateToScratchPad : Effect
		data class NavigateToNoteDetails(val noteId: String?) : Effect
		data class ShowError(val message: String) : Effect
		data class ShowSuccess(val message: String) : Effect
	}
}