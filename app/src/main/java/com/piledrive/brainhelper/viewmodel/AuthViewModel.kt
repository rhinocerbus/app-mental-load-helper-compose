package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.data.api.GenericErrorResponse
import com.piledrive.brainhelper.data.api.SuccessResponse
import com.piledrive.brainhelper.data.powersync.enums.SplashState
import com.piledrive.brainhelper.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
						_events.send(true)
					}

					is SessionStatus.RefreshFailure -> {
						_events.send(false)
					}

					is SessionStatus.NotAuthenticated -> {
						_events.send(false)
					}
				}
			}
		}
	}

	private val _events: Channel<Boolean> = Channel()
	val events: ReceiveChannel<Boolean> = _events

	private val _contentState = MutableStateFlow<SplashState>(SplashState.LOADING)
	val contentState: StateFlow<SplashState> = _contentState

	private val _errorState = MutableStateFlow<String?>(null)
	val errorState: StateFlow<String?> = _errorState

	fun attemptLogin(email: String, password: String) {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				val response = repo.register(email, password)
				when (response) {
					is SuccessResponse -> {
						_errorState.value = null
					}

					is GenericErrorResponse -> {
						_errorState.value = response.errMsg
					}
				}
			}
		}
	}
}
