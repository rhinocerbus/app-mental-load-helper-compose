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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.piledrive.brainhelper.R
import com.piledrive.brainhelper.mvi.auth.AuthContract
import com.piledrive.brainhelper.mvi.auth.AuthMviViewModel
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.brainhelper.ui.nav.TopLevelRoutes
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

object AuthMviScreen : NavRoute {
	override val routeValue: String = TopLevelRoutes.AUTH.routeValue

	@Composable
	fun draw(
		onNavigateToHome: () -> Unit,
		viewModel: AuthMviViewModel = hiltViewModel()
	) {
		val state by viewModel.state.collectAsState()
		val snackbarHostState = remember { SnackbarHostState() }

		// Handle effects
		LaunchedEffect(viewModel.effect) {
			viewModel.effect.collect { effect ->
				when (effect) {
					is AuthContract.Effect.NavigateToHome -> {
						onNavigateToHome()
					}

					is AuthContract.Effect.ShowError -> {
						snackbarHostState.showSnackbar(effect.message)
					}

					is AuthContract.Effect.ClearFields -> {
						// Fields are managed by state, so this is handled automatically
					}
				}
			}
		}

		Scaffold(
			snackbarHost = { SnackbarHost(snackbarHostState) },
			content = { innerPadding ->
				AuthContent(
					modifier = Modifier.padding(innerPadding),
					state = state,
					onIntent = viewModel::handleIntent
				)
			}
		)
	}

	@Composable
	internal fun AuthContent(
		modifier: Modifier = Modifier,
		state: AuthContract.State,
		onIntent: (AuthContract.Intent) -> Unit
	) {
		val focusManager = LocalFocusManager.current
		var localEmail by remember { mutableStateOf(state.email) }
		var localPassword by remember { mutableStateOf(state.password) }

		// Update local state when state changes
		LaunchedEffect(state.email) { localEmail = state.email }
		LaunchedEffect(state.password) { localPassword = state.password }

		Column(
			modifier = modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// App Icon with loading indicator
			Box(contentAlignment = Alignment.Center) {
				if (state.isLoading) {
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

			// Email Field
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(0.8f),
				value = localEmail,
				isError = state.errorMessage != null,
				supportingText = state.errorMessage?.let { { Text(it) } },
				label = { Text("Email") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email,
					imeAction = ImeAction.Next
				),
				keyboardActions = KeyboardActions {
					focusManager.moveFocus(FocusDirection.Next)
				},
				onValueChange = { newEmail ->
					localEmail = newEmail
					onIntent(AuthContract.Intent.UpdateEmail(newEmail))
				},
				enabled = !state.isLoading
			)

			Gap(8)

			// Password Field
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(0.8f),
				value = localPassword,
				isError = state.errorMessage != null,
				label = { Text("Password") },
				visualTransformation = PasswordVisualTransformation(),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Password,
					imeAction = ImeAction.Done
				),
				keyboardActions = KeyboardActions {
					onIntent(AuthContract.Intent.AttemptAuth(localEmail, localPassword))
				},
				onValueChange = { newPassword ->
					localPassword = newPassword
					onIntent(AuthContract.Intent.UpdatePassword(newPassword))
				},
				enabled = !state.isLoading
			)

			Gap(4)

			// Login/Register toggle
			Row(verticalAlignment = Alignment.CenterVertically) {
				Checkbox(
					checked = !state.isLoginMode,
					onCheckedChange = {
						onIntent(AuthContract.Intent.ToggleAuthMode)
					},
					enabled = !state.isLoading
				)
				Text("New user?")
			}

			Gap(8)

			// Submit Button
			Button(
				onClick = {
					onIntent(AuthContract.Intent.AttemptAuth(localEmail, localPassword))
				},
				enabled = !state.isLoading && localEmail.isNotBlank() && localPassword.isNotBlank()
			) {
				Text(
					if (state.isLoginMode) "Log in" else "Sign up"
				)
			}
		}
	}
}

@Preview
@Composable
fun AuthMviScreenPreview() {
	AppTheme {
		AuthMviScreen.AuthContent(
			state = AuthContract.State(
				isLoading = false,
				isLoginMode = true,
				email = "test@example.com",
				password = "",
				errorMessage = null
			),
			onIntent = {}
		)
	}
}