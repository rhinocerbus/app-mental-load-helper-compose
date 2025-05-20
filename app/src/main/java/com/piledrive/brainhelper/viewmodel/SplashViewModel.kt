package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.data.powersync.enums.SplashState
import com.piledrive.brainhelper.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
	private val repo: AuthRepo
) : ViewModel() {

	init {
		viewModelScope.launch {
			// dont need to check local cache at all, apparently
			//checkAuthenticated()
			repo.grabAuthStatusFlow().collect { status ->
				when (status) {
					is SessionStatus.Initializing -> {}
					is SessionStatus.Authenticated -> {
						_contentState.value = SplashState.AUTHORIZED
						delay(1000L)
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

	private suspend fun checkAuthenticated() {
		if(repo.checkSessionAuthenticated()) {
			// filler - actually check session validity
			_contentState.value = SplashState.AUTHORIZED
			delay(1000L)
			_events.send(true)
		} else {
			_contentState.value = SplashState.UNAUTHORIZED
			delay(1000L)
			_events.send(false)
		}
	}
}
