package com.piledrive.brainhelper.mvi.auth

import com.piledrive.brainhelper.mvi.MviContract

/**
 * MVI Contract for Authentication Screen
 */
object AuthContract {

	/**
	 * User intents for authentication
	 */
	sealed interface Intent : MviContract.Intent {
		data object LoadAuthState : Intent
		data class UpdateEmail(val email: String) : Intent
		data class UpdatePassword(val password: String) : Intent
		data object ToggleAuthMode : Intent
		data class AttemptAuth(val email: String, val password: String) : Intent
	}

	/**
	 * Authentication screen state
	 */
	data class State(
		val isLoading: Boolean = false,
		val isLoginMode: Boolean = true,
		val email: String = "",
		val password: String = "",
		val errorMessage: String? = null,
		val isAuthenticated: Boolean = false
	) : MviContract.State

	/**
	 * One-time effects for authentication
	 */
	sealed interface Effect : MviContract.Effect {
		data object NavigateToHome : Effect
		data class ShowError(val message: String) : Effect
		data object ClearFields : Effect
	}
}