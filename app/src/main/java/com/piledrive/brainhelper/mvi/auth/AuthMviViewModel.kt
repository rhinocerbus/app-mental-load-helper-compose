package com.piledrive.brainhelper.mvi.auth

import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.data.api.GenericErrorResponse
import com.piledrive.brainhelper.data.api.SuccessResponse
import com.piledrive.brainhelper.mvi.BaseMviViewModel
import com.piledrive.brainhelper.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthMviViewModel @Inject constructor(
	private val authRepo: AuthRepo
) : BaseMviViewModel<AuthContract.Intent, AuthContract.State, AuthContract.Effect>(
	initialState = AuthContract.State()
) {

	init {
		handleIntent(AuthContract.Intent.LoadAuthState)
		observeAuthStatus()
	}

	override fun handleIntent(intent: AuthContract.Intent) {
		logIntent(intent)
		when (intent) {
			is AuthContract.Intent.LoadAuthState -> loadAuthState()
			is AuthContract.Intent.UpdateEmail -> updateEmail(intent.email)
			is AuthContract.Intent.UpdatePassword -> updatePassword(intent.password)
			is AuthContract.Intent.ToggleAuthMode -> toggleAuthMode()
			is AuthContract.Intent.AttemptAuth -> attemptAuth(intent.email, intent.password)
		}
	}

	private fun loadAuthState() {
		// Initial state is already set in constructor
		setState { copy(isLoading = false) }
	}

	private fun updateEmail(email: String) {
		setState { copy(email = email, errorMessage = null) }
	}

	private fun updatePassword(password: String) {
		setState { copy(password = password, errorMessage = null) }
	}

	private fun toggleAuthMode() {
		setState {
			copy(
				isLoginMode = !isLoginMode,
				errorMessage = null
			)
		}
	}

	private fun attemptAuth(email: String, password: String) {
		viewModelScope.launch {
			setState { copy(isLoading = true, errorMessage = null) }

			try {
				withContext(Dispatchers.IO) {
					val response = if (currentState.isLoginMode) {
						authRepo.login(email, password)
					} else {
						authRepo.register(email, password)
					}

					when (response) {
						is SuccessResponse -> {
							setState { copy(isLoading = false, errorMessage = null) }
							// Navigation will be handled by auth status observer
						}

						is GenericErrorResponse -> {
							setState {
								copy(
									isLoading = false,
									errorMessage = response.errMsg
								)
							}
							sendEffect(AuthContract.Effect.ShowError(response.errMsg))
						}
					}
				}
			} catch (e: Exception) {
				setState {
					copy(
						isLoading = false,
						errorMessage = e.message ?: "Unknown error occurred"
					)
				}
				sendEffect(AuthContract.Effect.ShowError(e.message ?: "Unknown error occurred"))
			}
		}
	}

	private fun observeAuthStatus() {
		viewModelScope.launch {
			authRepo.grabAuthStatusFlow().collect { status ->
				when (status) {
					is SessionStatus.Initializing -> {
						setState { copy(isLoading = true) }
					}

					is SessionStatus.Authenticated -> {
						setState {
							copy(
								isLoading = false,
								isAuthenticated = true,
								errorMessage = null
							)
						}
						sendEffect(AuthContract.Effect.NavigateToHome)
					}

					is SessionStatus.RefreshFailure -> {
						setState {
							copy(
								isLoading = false,
								isAuthenticated = false,
								errorMessage = "Session refresh failed"
							)
						}
					}

					is SessionStatus.NotAuthenticated -> {
						setState {
							copy(
								isLoading = false,
								isAuthenticated = false
							)
						}
					}
				}
			}
		}
	}
}