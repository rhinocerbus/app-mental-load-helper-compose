package com.piledrive.brainhelper.ui.screens.main

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface MainScreenCoordinatorImpl {
	val selfProfileSourceFlow: StateFlow<Profile?>
	val familiesSourceFlow: StateFlow<List<Family>>
	val familyMembersSourceFlow: StateFlow<List<Profile>>
	val notesSourceFlow: StateFlow<List<Note>>
}

class MainScreenCoordinator(
	override val selfProfileSourceFlow: StateFlow<Profile?>,
	override val familiesSourceFlow: StateFlow<List<Family>>,
	override val familyMembersSourceFlow: StateFlow<List<Profile>>,
	override val notesSourceFlow: StateFlow<List<Note>>,
) : MainScreenCoordinatorImpl {

}

val stubMainScreenCoordinator = MainScreenCoordinator(
	selfProfileSourceFlow = MutableStateFlow(null),
	familiesSourceFlow = MutableStateFlow(listOf()),
	familyMembersSourceFlow = MutableStateFlow(listOf()),
	notesSourceFlow = MutableStateFlow(listOf()),
)