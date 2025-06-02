package com.piledrive.brainhelper.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object MainScreen : NavRoute {
	override val routeValue: String = "home"

	@Composable
	fun draw(
		mainCoordinator: MainScreenCoordinator,
	) {
		drawContent(
			mainCoordinator,
		)
	}

	@Composable
	fun drawContent(
		mainCoordinator: MainScreenCoordinator,
	) {
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				BodyContent(modifier = Modifier.padding(innerPadding), mainCoordinator)
			}
		)
	}

	@Composable
	private fun BodyContent(
		modifier: Modifier = Modifier,
		mainCoordinator: MainScreenCoordinator,
	) {
		val families = mainCoordinator.familiesSourceFlow.collectAsState().value
		Column(modifier = modifier) {
			Text(text = "Families:")
			LazyColumn(
				modifier = Modifier.fillMaxWidth(),
				rememberLazyListState(),
			) {
				itemsIndexed(
					items = families.data,
					key = { _, fam -> fam.id }
				) { _, fam ->
					Text(text = fam.name)
				}
			}
		}
	}
}

@Preview
@Composable
fun MainPreview() {
	AppTheme {
		MainScreen.drawContent(
			stubMainScreenCoordinator
		)
	}
}