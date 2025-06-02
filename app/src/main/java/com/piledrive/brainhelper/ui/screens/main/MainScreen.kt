package com.piledrive.brainhelper.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.lib_compose_components.ui.appbar.SearchBarWithSpinner
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.StateFlow

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
		Column(modifier = modifier) {
			SelfSection(mainCoordinator.selfProfileSourceFlow)
			Gap(20)
			FamilySection(mainCoordinator.familiesSourceFlow)
			Gap(20)
			FamilyMembersSection(mainCoordinator.familyMembersSourceFlow)
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
	private fun FamilySection(familiesSourceFlow: StateFlow<List<Family>>) {
		val families = familiesSourceFlow.collectAsState().value
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