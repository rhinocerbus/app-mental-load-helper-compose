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
import com.piledrive.lib_compose_components.ui.textfield.ValidatedTextField
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

		drawContent(
			stateFlow,
			onLoginAttempt = { e, p ->
				viewModel.attemptLogin(e, p)
			}
		)
	}

	@Composable
	internal fun drawContent(
		stateFlow: StateFlow<SplashState>,
		onLoginAttempt: (String, String) -> Unit
	) {
		Scaffold(
			topBar = {
			},
			content = { innerPadding ->
				BodyContent(Modifier.padding(innerPadding), stateFlow, onLoginAttempt)
			}
		)
	}

	@Composable
	private fun BodyContent(
		modifier: Modifier,
		stateFlow: StateFlow<SplashState>,
		onLoginAttempt: (String, String) -> Unit
	) {
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


			val emailFormState = remember {
				TextFormFieldState(
					initialValue = "m.rubright.dev@gmail.com",
					mainValidator = Validators.IsEmail(errMsg = "Email required"),
				)
			}
			val focusManager = LocalFocusManager.current
			ValidatedTextField(
				modifier = Modifier.fillMaxWidth(0.8f),
				formState = emailFormState,
				label = { Text(text = "Email") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email,
					imeAction = ImeAction.Next,
					showKeyboardOnFocus = true
				),
				keyboardActions = KeyboardActions {
					focusManager.moveFocus(FocusDirection.Next)
				}
			)


			val passwordFormState = remember {
				TextFormFieldState(
					initialValue = "test",
					mainValidator = Validators.Required(errMsg = "Password required"),
				)
			}
			ValidatedTextField(
				modifier = Modifier.fillMaxWidth(0.8f),
				formState = passwordFormState,
				label = { Text(text = "Password") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email,
					imeAction = ImeAction.Next,
					showKeyboardOnFocus = true
				),
				keyboardActions = KeyboardActions {
					onLoginAttempt(emailFormState.currentValue, passwordFormState.currentValue)
				}
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
			{ _, _ -> }
		)
	}
}