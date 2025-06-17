package com.piledrive.brainhelper.ui.screens.note_details

import com.piledrive.brainhelper.data.model.composite.FullNote
import com.piledrive.brainhelper.data.model.composite.FullTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface NoteDetailsScreenCoordinatorImpl {
	val activeNoteSourceFlow: StateFlow<FullNote?>
	val allTagsSourceFlow: StateFlow<List<FullTag>>
	val onSaveNoteState: (title: String?, content: String, tagIds: List<String>) -> Unit
}

class NoteDetailsScreenCoordinator(
	override val activeNoteSourceFlow: StateFlow<FullNote?>,
	override val allTagsSourceFlow: StateFlow<List<FullTag>>,
	override val onSaveNoteState: (title: String?, content: String, tagIds: List<String>) -> Unit
) : NoteDetailsScreenCoordinatorImpl

val stubNoteDetailsScreenCoordinator = NoteDetailsScreenCoordinator(
	activeNoteSourceFlow = MutableStateFlow(null),
	allTagsSourceFlow = MutableStateFlow(listOf()),
	onSaveNoteState = { _, _, _ -> }
)