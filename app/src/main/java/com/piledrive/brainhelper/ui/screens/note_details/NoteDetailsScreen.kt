@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.brainhelper.ui.screens.note_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.piledrive.brainhelper.ui.nav.ChildRoutes
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.textfield.TextFieldDebounced
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import timber.log.Timber

object NoteDetailsScreen : NavRoute {
	override val routeValue: String = ChildRoutes.NOTE_DETAILS.routeValue

	interface NavCallbacks {
		val onLaunchCreateTag: () -> Unit
		val onBack: () -> Unit
	}

	@Composable
	fun draw(
		notesCoordinator: NoteDetailsScreenCoordinator,
		navCallbacks: NavCallbacks
	) {
		Scaffold(
			topBar = {
				TopBarContent(
					notesCoordinator, navCallbacks
				)
			},
			content = { innerPadding ->
				BodyContent(Modifier.padding(innerPadding), notesCoordinator, navCallbacks)
			}
		)
	}

	@Composable
	private fun TopBarContent(
		notesCoordinator: NoteDetailsScreenCoordinator,
		navCallbacks: NavCallbacks
	) {
		val activeTag = notesCoordinator.activeNoteSourceFlow.collectAsState().value

		TopAppBar(
			title = {
				if (activeTag != null) {
					Text(activeTag.noteTitle)
				} else {
					Text("Add New Note")
				}
			},
			navigationIcon = {
				IconButton(onClick = navCallbacks.onBack) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Localized description"
					)
				}
			}
		)
	}

	@Composable
	private fun BodyContent(
		modifier: Modifier,
		notesCoordinator: NoteDetailsScreenCoordinator,
		navCallbacks: NavCallbacks
	) {
		val noteContent = notesCoordinator.activeNoteSourceFlow.collectAsState().value
		Timber.d("detail note: $noteContent")
		var noteTitleText = remember { mutableStateOf<String>(noteContent?.noteTitle ?: "") }
		var noteContentText = remember { mutableStateOf<String>(noteContent?.noteCotnent ?: "") }
		var selectedTags = remember { mutableStateOf(noteContent?.tags ?: listOf()) }

		val allTags = notesCoordinator.allTagsSourceFlow.collectAsState().value

		LaunchedEffect(noteContent?.id) {
			noteTitleText.value = noteContent?.noteTitle ?: ""
			noteContentText.value = noteContent?.noteCotnent ?: ""
			selectedTags.value = noteContent?.tags ?: listOf()
		}

		LifecycleStartEffect(Unit) {
			onStopOrDispose {
				notesCoordinator.onSaveNoteState(true, noteTitleText.value, noteContentText.value, selectedTags.value.map { it.id })
			}
		}

		Column(
			modifier = modifier
				.fillMaxSize()
				.padding(8.dp),
			verticalArrangement = Arrangement.Center,
		) {
			TextFieldDebounced(
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
				value = noteTitleText.value,
				label = { Text("Note title") },
				onValueChange = {
					noteTitleText.value = it
					notesCoordinator.onSaveNoteState(false, noteTitleText.value, noteContentText.value, selectedTags.value.map { it.id })
				}
			)

			Gap(8)

			Text("Note tags:")
			ChipGroup {
				SuggestionChip(
					onClick = {
						navCallbacks.onLaunchCreateTag()
					},
					label = { Text("Add") },
					icon = { Icon(Icons.Default.Add, "add new tag") }
				)

				allTags.forEach {
					val selected = selectedTags.value.contains(it)
					FilterChip(
						selected = selected,
						onClick = {
							if (selected) {
								selectedTags.value -= it
							} else {
								selectedTags.value += it
							}
						},
						label = { Text(it.tagText) },
						leadingIcon = {
							if (selected) {
								Icon(
									Icons.Default.Check,
									"${it.tagText} applied",
									Modifier.size(FilterChipDefaults.IconSize),
								)
							} else {
								null
							}
						}
					)
				}
			}

			Gap(8)

			TextFieldDebounced(
				modifier = Modifier.fillMaxSize(),
				singleLine = false,
				value = noteContentText.value,
				label = { Text("Note details") },
				onValueChange = { text ->
					noteContentText.value = text
					notesCoordinator.onSaveNoteState(false, noteTitleText.value, noteContentText.value, selectedTags.value.map { it.id })
				}
			)
		}
	}
}

@Preview
@Composable
private fun NoteDetailsPreview() {
	AppTheme {
		NoteDetailsScreen.draw(
			stubNoteDetailsScreenCoordinator,
			navCallbacks = object : NoteDetailsScreen.NavCallbacks {
				override val onLaunchCreateTag: () -> Unit = {}
				override val onBack: () -> Unit = {}
			}
		)
	}
}