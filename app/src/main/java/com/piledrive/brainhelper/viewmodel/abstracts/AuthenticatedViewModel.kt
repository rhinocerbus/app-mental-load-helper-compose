package com.piledrive.brainhelper.viewmodel.abstracts

import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.repo.AuthRepo
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class AuthenticatedViewModel(
	private val authRepo: AuthRepo,

	) : BaseViewModel() {

	init {
		initAuthWatch()
	}

	private fun initAuthWatch() {
		viewModelScope.launch {
			authRepo.grabAuthStatusFlow().collect { status ->
				when (status) {
					is SessionStatus.NotAuthenticated -> {
						_loggedOutEvent.send(true)
					}

					else -> {}
				}
			}
		}
	}

	//  region auth
	/////////////////////////////////////////////////

	protected val _loggedOutEvent: Channel<Boolean> = Channel()
	val loggedOutEvent: ReceiveChannel<Boolean> = _loggedOutEvent

	protected fun logout() {
		viewModelScope.launch {
			authRepo.logout()
		}
	}

	/////////////////////////////////////////////////
	//  endregion
}