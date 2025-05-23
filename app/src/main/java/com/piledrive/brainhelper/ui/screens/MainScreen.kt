package com.piledrive.brainhelper.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.brainhelper.ui.util.previewMainContentFlow
import com.piledrive.brainhelper.viewmodel.SampleViewModel
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.StateFlow

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		viewModel: SampleViewModel,
	) {
		drawContent(
			viewModel.contentState,
		)
	}

	@Composable
	fun drawContent(
		contentState: StateFlow<Int>,
	) {
		val homeState = contentState.collectAsState().value
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				Box(modifier = Modifier.padding(innerPadding))
			}
		)
	}
}

@Preview
@Composable
fun MainPreview() {
	AppTheme {
		val contentState = previewMainContentFlow()
		MainScreen.drawContent(
			contentState
		)
	}
}