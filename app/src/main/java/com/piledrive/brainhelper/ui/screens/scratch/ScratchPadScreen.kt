package com.piledrive.brainhelper.ui.screens.scratch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleStartEffect
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.brainhelper.ui.nav.TopLevelRoutes
import com.piledrive.lib_compose_components.ui.textfield.TextFieldDebounced
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object ScratchPadScreen : NavRoute {
	override val routeValue: String = TopLevelRoutes.SCRATCH.routeValue

	@Composable
	fun draw(
		scratchCoordinator: ScratchPadScreenCoordinator
	) {
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				BodyContent(Modifier.padding(innerPadding), scratchCoordinator)
			}
		)
	}

	@Composable
	private fun BodyContent(
		modifier: Modifier,
		scratchCoordinator: ScratchPadScreenCoordinator
	) {
		val scratchContent = scratchCoordinator.scratchesSourceFlow.collectAsState().value
		var scratchNotesText by remember { mutableStateOf<String>(scratchContent?.content ?: "") }

		LifecycleStartEffect(Unit) {
			onStopOrDispose {
				scratchCoordinator.onTextChanged(scratchNotesText)
			}
		}

		Column(
			modifier = modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			TextFieldDebounced(
				modifier = Modifier.fillMaxSize(),
				singleLine = false,
				value = scratchContent?.content ?: "",
				onValueChange = {
					scratchNotesText = it
					scratchCoordinator.onTextChanged(it)
				})
		}
	}
}

@Preview
@Composable
private fun ScratchPadScreenPreview() {
	AppTheme {
		ScratchPadScreen.draw(
			stubScratchPadScreenCoordinator
		)
	}
}