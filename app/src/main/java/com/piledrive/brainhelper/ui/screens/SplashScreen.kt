package com.piledrive.brainhelper.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.brainhelper.R
import com.piledrive.brainhelper.data.powersync.enums.SplashState
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.brainhelper.ui.nav.TopLevelRoutes
import com.piledrive.brainhelper.ui.util.previewMainContentFlow
import com.piledrive.brainhelper.viewmodel.SplashViewModel
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SplashScreen : NavRoute {
	override val routeValue: String = TopLevelRoutes.SPLASH.routeValue

	@Composable
	fun draw(
		viewModel: SplashViewModel,
	) {
		val stateFlow = viewModel.contentState
		drawContent(
			stateFlow,
		)
	}

	@Composable
	internal fun drawContent(
		stateFlow: StateFlow<SplashState>,
	) {
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				BodyContent(Modifier.padding(innerPadding), stateFlow)
			}
		)
	}

	@Composable
	private fun BodyContent(modifier: Modifier, stateFlow: StateFlow<SplashState>) {
		val contentState = stateFlow.collectAsState().value
		Column(
			modifier = modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Icon(
				ImageVector.vectorResource(R.drawable.baseline_self_improvement_24),
				contentDescription = "app icon",
				modifier = Modifier.size(80.dp)
			)
			when (contentState) {
				SplashState.LOADING ->
					Text(modifier = Modifier, text = "Loading...")

				SplashState.AUTHORIZED ->
					Text(modifier = Modifier, text = "Logged in!")

				SplashState.UNAUTHORIZED ->
					Text(modifier = Modifier, text = "Need to log in")
			}
		}
	}
}

@Preview
@Composable
fun SplashScreenPreview() {
	AppTheme {
		SplashScreen.drawContent(
			MutableStateFlow(SplashState.LOADING)
		)
	}
}