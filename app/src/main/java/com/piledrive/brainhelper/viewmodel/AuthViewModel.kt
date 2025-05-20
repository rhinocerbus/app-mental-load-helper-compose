package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.data.api.GenericErrorResponse
import com.piledrive.brainhelper.data.api.SuccessResponse
import com.piledrive.brainhelper.repo.AuthRepo
import com.piledrive.brainhelper.ui.screens.auth.AuthScreenCoordinator
import com.piledrive.brainhelper.ui.screens.auth.AuthScreenCoordinatorImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
	private val repo: AuthRepo
) : ViewModel() {

	init {
		viewModelScope.launch {
			repo.grabAuthStatusFlow().collect { status ->
				when (status) {
					is SessionStatus.Initializing -> {}
					is SessionStatus.Authenticated -> {
						_coordinator._events.send(true)
					}

					is SessionStatus.RefreshFailure -> {
						_coordinator._events.send(false)
					}

					is SessionStatus.NotAuthenticated -> {
						_coordinator._events.send(false)
					}
				}
			}
		}
	}

	private val _coordinator = AuthScreenCoordinator(
		onAuthAttempt = { email, pw ->
			attemptLogin(email, pw)
		},
	)
	val coordinator: AuthScreenCoordinatorImpl = _coordinator

	fun attemptLogin(email: String, password: String) {
		viewModelScope.launch {
			_coordinator._networkBusyStateFlow.value = true
			withContext(Dispatchers.Default) {
				val response = if (_coordinator._registerToggleStateFlow.value) {
					repo.register(email, password)
				} else {
					repo.login(email, password)
				}
				when (response) {
					is SuccessResponse -> {
						_coordinator._errorStateFlow.value = null
					}

					is GenericErrorResponse -> {
						_coordinator._errorStateFlow.value = response.errMsg
					}
				}
			}
			_coordinator._networkBusyStateFlow.value = false
		}
	}
}
