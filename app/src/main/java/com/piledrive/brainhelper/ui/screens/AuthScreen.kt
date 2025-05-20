package com.piledrive.brainhelper.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import com.piledrive.brainhelper.data.powersync.enums.SplashState
import com.piledrive.brainhelper.ui.nav.NavRoute
import com.piledrive.brainhelper.ui.nav.TopLevelRoutes
import com.piledrive.brainhelper.viewmodel.AuthViewModel
import com.piledrive.lib_compose_components.ui.forms.state.TextFormFieldState
import com.piledrive.lib_compose_components.ui.forms.validators.Validators
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AuthScreen : NavRoute {
	override val routeValue: String = TopLevelRoutes.AUTH.routeValue

	@Composable
	fun draw(
		viewModel: AuthViewModel,
	) {
		val stateFlow = viewModel.contentState
		val errorStateFlow = viewModel.errorState
		drawContent(
			stateFlow,
			errorStateFlow,
			onLoginAttempt = { e, p ->
				viewModel.attemptLogin(e, p)
			}
		)
	}

	@Composable
	internal fun drawContent(
		stateFlow: StateFlow<SplashState>,
		errorStateFlow: StateFlow<String?>,
		onLoginAttempt: (String, String) -> Unit
	) {
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				BodyContent(Modifier.padding(innerPadding), stateFlow, errorStateFlow, onLoginAttempt)
			}
		)
	}

	@Composable
	private fun BodyContent(
		modifier: Modifier,
		stateFlow: StateFlow<SplashState>,
		errorStateFlow: StateFlow<String?>,
		onLoginAttempt: (String, String) -> Unit
	) {
		val focusManager = LocalFocusManager.current
		val emailFormState = remember {
			TextFormFieldState(
				initialValue = "m.rubright.dev@gmail.com",
				mainValidator = Validators.IsEmail(errMsg = "Email required"),
			)
		}

		val passwordFormState = remember {
			TextFormFieldState(
				initialValue = "test-6-chars",
				mainValidator = Validators.Required(errMsg = "Password required"),
			)
		}

		val contentState = stateFlow.collectAsState().value
		val errMsg = errorStateFlow.collectAsState().value

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
			)

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
					onLoginAttempt(emailFormState.currentValue, passwordFormState.currentValue)
				},
				onValueChange = {
					passwordFormState.check(it)
				},
			)
		}
	}
}

@Preview
@Composable
fun AuthScreenPreview() {
	AppTheme {
		AuthScreen.drawContent(
			MutableStateFlow(SplashState.LOADING),
			MutableStateFlow(null),
			{ _, _ -> }
		)
	}
}