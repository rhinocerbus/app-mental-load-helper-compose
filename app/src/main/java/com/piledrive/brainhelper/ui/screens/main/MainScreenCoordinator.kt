package com.piledrive.brainhelper.ui.screens.main

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.state.FamilyContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface MainScreenCoordinatorImpl {
	val selfProfileSourceFlow: StateFlow<Profile?>
	val familiesSourceFlow: StateFlow<FamilyContentState>
	val familyMembersSourceFlow: StateFlow<List<Profile>>
	val notesSourceFlow: StateFlow<List<Note>>
}

class MainScreenCoordinator(
	override val selfProfileSourceFlow: StateFlow<Profile?>,
	override val familiesSourceFlow: StateFlow<FamilyContentState>,
	override val familyMembersSourceFlow: StateFlow<List<Profile>>,
	override val notesSourceFlow: StateFlow<List<Note>>,
) : MainScreenCoordinatorImpl {

}

val stubMainScreenCoordinator = MainScreenCoordinator(
	selfProfileSourceFlow = MutableStateFlow(null),
	familiesSourceFlow = MutableStateFlow(FamilyContentState()),
	familyMembersSourceFlow = MutableStateFlow(listOf()),
	notesSourceFlow = MutableStateFlow(listOf()),
)