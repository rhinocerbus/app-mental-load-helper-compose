package com.piledrive.brainhelper.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.piledrive.brainhelper.R
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.state.FamilyContentState
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.brainhelper.ui.nav.TopLevelRoutes
import com.piledrive.brainhelper.ui.screens.main.views.MainBar
import com.piledrive.brainhelper.ui.screens.main.views.MainBarCoordinator
import com.piledrive.brainhelper.ui.screens.main.views.stubMainBarCoordinator
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.StateFlow

object MainScreen : NavRoute {
	override val routeValue: String = TopLevelRoutes.HOME.routeValue

	interface MainScreenNavCallbacks {
		val onLaunchScratchPad: () -> Unit
		val onLaunchNote: (Note?) -> Unit
	}

	@Composable
	fun draw(
		barCoordinator: MainBarCoordinator,
		mainCoordinator: MainScreenCoordinator,
		navCallbacks: MainScreenNavCallbacks
	) {
		Scaffold(
			topBar = {
				MainBar(
					Modifier,
					barCoordinator
				)
			},
			content = { innerPadding ->
				BodyContent(modifier = Modifier.padding(innerPadding), mainCoordinator, navCallbacks)
			},
			floatingActionButton = {
				FabsSection(navCallbacks)
			}
		)
	}

	@Composable
	private fun BodyContent(
		modifier: Modifier = Modifier,
		mainCoordinator: MainScreenCoordinator,
		navCallbacks: MainScreenNavCallbacks,
	) {
		Column(modifier = modifier) {
			SelfSection(mainCoordinator.selfProfileSourceFlow)
			Gap(20)
			FamilySection(mainCoordinator.familiesSourceFlow)
			Gap(20)
			FamilyMembersSection(mainCoordinator.familyMembersSourceFlow)
			Gap(20)
			NotesSection(mainCoordinator.notesSourceFlow, navCallbacks)
		}
	}

	@Composable
	private fun SelfSection(selfStateFlow: StateFlow<Profile?>) {
		val self = selfStateFlow.collectAsState().value
		if (self != null) {
			Text(text = "Self: ${self.fullName}")
		} else {
			CircularProgressIndicator(
				modifier = Modifier
					.padding(8.dp, 16.dp)
					.zIndex(1f),
			)
		}
	}

	@Composable
	private fun FamilySection(familiesSourceFlow: StateFlow<FamilyContentState>) {
		val families = familiesSourceFlow.collectAsState().value.families
		Text(text = "Families:")
		LazyColumn(
			modifier = Modifier.fillMaxWidth(),
			rememberLazyListState(),
		) {
			itemsIndexed(
				items = families,
				key = { _, fam -> fam.id }
			) { _, fam ->
				Text(text = fam.name)
			}
		}
	}

	@Composable
	private fun FamilyMembersSection(familiesSourceFlow: StateFlow<List<Profile>>) {
		val members = familiesSourceFlow.collectAsState().value
		Text(text = "Family members:")
		LazyColumn(
			modifier = Modifier.fillMaxWidth(),
			rememberLazyListState(),
		) {
			itemsIndexed(
				items = members,
				key = { _, fam -> fam.id }
			) { _, fam ->
				Text(text = fam.fullName)
			}
		}
	}

	@Composable
	private fun NotesSection(notesSourceFlow: StateFlow<List<Note>>, navCallbacks: MainScreenNavCallbacks) {
		val notes = notesSourceFlow.collectAsState().value
		Text(text = "Family notes:")
		LazyColumn(
			modifier = Modifier.fillMaxWidth(),
			rememberLazyListState(),
		) {
			itemsIndexed(
				items = notes,
				key = { _, note -> note.id }
			) { _, note ->
				Surface(onClick = { navCallbacks.onLaunchNote(note) }) {
					Text(text = note.content)
				}
			}
		}
	}

	@Composable
	private fun FabsSection(navCallbacks: MainScreenNavCallbacks) {
		var showMenu by remember { mutableStateOf(false) }
		Column {
			FloatingActionButton(
				onClick = { showMenu = true }
			) {
				Icon(Icons.Default.Add, "add content")
			}
			DropdownMenu(
				expanded = showMenu,
				onDismissRequest = { showMenu = false }
			) {
				DropdownMenuItem(
					text = { Text("Add note") }, onClick = {
						navCallbacks.onLaunchNote(null)
						showMenu = false
					}
				)
			}

			Gap(16)

			FloatingActionButton(
				onClick = { navCallbacks.onLaunchScratchPad() }
			) {
				Icon(ImageVector.vectorResource(R.drawable.baseline_edit_note_24), "show scratch pad")
			}
		}
	}
}

@Preview
@Composable
private fun MainPreview() {
	AppTheme {
		MainScreen.draw(
			stubMainBarCoordinator,
			stubMainScreenCoordinator,
			object : MainScreen.MainScreenNavCallbacks {
				override val onLaunchScratchPad: () -> Unit = {}
				override val onLaunchNote: (Note?) -> Unit = {}
			}
		)
	}
}