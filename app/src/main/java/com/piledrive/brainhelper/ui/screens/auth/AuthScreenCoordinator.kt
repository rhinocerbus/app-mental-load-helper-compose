package com.piledrive.brainhelper.ui.screens.auth

import com.piledrive.brainhelper.data.powersync.enums.SplashState
import com.piledrive.brainhelper.ui.abstracts.BaseCoordinator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthScreenCoordinatorImpl : BaseCoordinator {
	val events: ReceiveChannel<Boolean>
	val contentStateFlow: StateFlow<SplashState>
	val registerToggleStateFlow: StateFlow<Boolean>
	val errorStateFlow: StateFlow<String?>
	val onLoginAttempt: (String, String) -> Unit
}

class AuthScreenCoordinator(
	override val onLoginAttempt: (String, String) -> Unit,
) : AuthScreenCoordinatorImpl {
	val _events: Channel<Boolean> = Channel()
	override val events: ReceiveChannel<Boolean> = _events

	val _contentStateFlow = MutableStateFlow<SplashState>(SplashState.LOADING)
	override val contentStateFlow: StateFlow<SplashState> = _contentStateFlow

	val _registerToggleStateFlow = MutableStateFlow<Boolean>(false)
	override val registerToggleStateFlow: StateFlow<Boolean> = _registerToggleStateFlow

	val _errorStateFlow = MutableStateFlow<String?>(null)
	override val errorStateFlow: StateFlow<String?> = _errorStateFlow
}