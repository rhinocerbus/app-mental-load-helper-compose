package com.piledrive.brainhelper.ui.screens.auth

import com.piledrive.brainhelper.ui.abstracts.BaseCoordinator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthScreenCoordinatorImpl : BaseCoordinator {
	val loginSuccessEvent: ReceiveChannel<Boolean>
	val networkBusyStateFlow: StateFlow<Boolean>
	val registerToggleStateFlow: StateFlow<Boolean>
	val errorStateFlow: StateFlow<String?>
	// combined register/login, action based on toggle
	val onAuthAttempt: (String, String) -> Unit
	val onToggleRegisterMode: () -> Unit
}

class AuthScreenCoordinator(
	initialIsBusy: Boolean = false,
	initialRegisterToggle: Boolean = false,
	initialError: String? = null,
	override val onAuthAttempt: (String, String) -> Unit = { _, _ -> },
) : AuthScreenCoordinatorImpl {

	override val onToggleRegisterMode: () -> Unit = {
		_registerToggleStateFlow.value = !_registerToggleStateFlow.value
	}

	val _events: Channel<Boolean> = Channel()
	override val loginSuccessEvent: ReceiveChannel<Boolean> = _events

	val _networkBusyStateFlow = MutableStateFlow<Boolean>(initialIsBusy)
	override val networkBusyStateFlow: StateFlow<Boolean> = _networkBusyStateFlow

	val _registerToggleStateFlow = MutableStateFlow<Boolean>(initialRegisterToggle)
	override val registerToggleStateFlow: StateFlow<Boolean> = _registerToggleStateFlow

	val _errorStateFlow = MutableStateFlow<String?>(initialError)
	override val errorStateFlow: StateFlow<String?> = _errorStateFlow
}