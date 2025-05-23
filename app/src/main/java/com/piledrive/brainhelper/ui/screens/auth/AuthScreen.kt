package com.piledrive.brainhelper.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.brainhelper.R
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.brainhelper.ui.nav.TopLevelRoutes
import com.piledrive.lib_compose_components.ui.forms.state.TextFormFieldState
import com.piledrive.lib_compose_components.ui.forms.validators.Validators
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object AuthScreen : NavRoute {
	override val routeValue: String = TopLevelRoutes.AUTH.routeValue

	@Composable
	fun draw(
		coordinator: AuthScreenCoordinatorImpl,
	) {
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				BodyContent(Modifier.padding(innerPadding), coordinator)
			}
		)
	}

	@Composable
	private fun BodyContent(
		modifier: Modifier,
		coordinator: AuthScreenCoordinatorImpl
	) {
		val focusManager = LocalFocusManager.current
		val emailFormState = remember {
			TextFormFieldState(
				initialValue = "charlesclarkerubright@gmail.com",
				mainValidator = Validators.IsEmail(errMsg = "Email required"),
			)
		}

		val passwordFormState = remember {
			TextFormFieldState(
				initialValue = "test-6-chars",
				mainValidator = Validators.Required(errMsg = "Password required"),
			)
		}

		val errMsg = coordinator.errorStateFlow.collectAsState().value
		val isBusy = coordinator.networkBusyStateFlow.collectAsState().value

		Column(
			modifier = modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Box() {
				if (isBusy) {
					CircularProgressIndicator(
						modifier = Modifier.size(84.dp),
						)
				}

				Icon(
					ImageVector.vectorResource(R.drawable.baseline_self_improvement_24),
					contentDescription = "app icon",
					modifier = Modifier.size(80.dp)
				)
			}

			Gap(24)

			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(0.8f),
				value = emailFormState.currentValue,
				isError = emailFormState.hasError || errMsg != null,
				supportingText = {
					(emailFormState.errorMsg)?.apply {
						Text(this)
					}
				},
				label = { Text(text = "Email") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email,
					imeAction = ImeAction.Next,
					showKeyboardOnFocus = true
				),
				keyboardActions = KeyboardActions {
					focusManager.moveFocus(FocusDirection.Next)
				},
				onValueChange = {
					emailFormState.check(it)
				},
				enabled = !isBusy
			)

			Gap(8)

			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(0.8f),
				value = passwordFormState.currentValue,
				isError = passwordFormState.hasError || errMsg != null,
				supportingText = {
					(passwordFormState.errorMsg ?: errMsg)?.apply {
						Text(this)
					}
				},
				label = { Text(text = "Password") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email,
					imeAction = ImeAction.Next,
					showKeyboardOnFocus = true
				),
				keyboardActions = KeyboardActions {
					coordinator.onAuthAttempt(emailFormState.currentValue, passwordFormState.currentValue)
				},
				onValueChange = {
					passwordFormState.check(it)
				},
				enabled = !isBusy
			)

			Gap(4)

			val isSignUpMode = coordinator.registerToggleStateFlow.collectAsState().value
			Row(verticalAlignment = Alignment.CenterVertically) {
				Checkbox(
					checked = isSignUpMode,
					onCheckedChange = { coordinator.onToggleRegisterMode() },
					enabled = !isBusy
				)
				Text("New user?")
			}

			Gap(8)

			Button(
				onClick = {
					coordinator.onAuthAttempt(emailFormState.currentValue, passwordFormState.currentValue)
				},
				enabled = !isBusy
			) {
				if (isSignUpMode) {
					Text("Sign up")
				} else {
					Text("Log in")
				}
			}
		}
	}
}

@Preview
@Composable
fun AuthScreenPreview() {
	AppTheme {
		AuthScreen.draw(
			AuthScreenCoordinator(
				initialIsBusy = true
			)
		)
	}
}