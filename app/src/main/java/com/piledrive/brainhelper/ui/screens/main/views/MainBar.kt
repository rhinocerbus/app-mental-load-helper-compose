@file:OptIn(ExperimentalMaterial3Api::class)

package com.piledrive.brainhelper.ui.screens.main.views

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.lib_compose_components.ui.appbar.TopAppBarWithOverflow
import com.piledrive.lib_compose_components.ui.dropdown.readonly.multiselect.ReadOnlyMultiSelectDropdownTextFieldGeneric
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

@Composable
fun MainBar(
	modifier: Modifier,
	coordinator: MainBarCoordinator
) {
	Surface(color = TopAppBarDefaults.topAppBarColors().containerColor) {
		TopAppBarWithOverflow.Draw(
			title = {
				Text("asdf")
			},
			otherActions = {
				ReadOnlyMultiSelectDropdownTextFieldGeneric(
					modifier = Modifier.widthIn(100.dp, 150.dp).wrapContentWidth(),
					innerTextFieldModifier = Modifier.wrapContentWidth(),
					coordinator = coordinator.tagsCoordinator,
					text = "Tags (10)"
				)
			},
			overflowActions = {
				DropdownMenuItem(
					text = {
						Text("Logout")
					},
					onClick = {
						coordinator.onLogout()
					}
				)
			}
		)
	}
}

@Preview
@Composable
private fun MainBarPreview() {
	AppTheme {
		MainBar(Modifier, stubMainBarCoordinator)
	}
}