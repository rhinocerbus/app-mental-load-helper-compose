package com.piledrive.brainhelper.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base ViewModel implementation for MVI architecture
 */
abstract class BaseMviViewModel<I : MviContract.Intent, S : MviContract.State, E : MviContract.Effect>(
	initialState: S
) : ViewModel(), MviViewModel<I, S, E> {

	private val _state = MutableStateFlow(initialState)
	override val state: StateFlow<S> = _state.asStateFlow()

	private val _effect = Channel<E>(Channel.BUFFERED)
	override val effect: Flow<E> = _effect.receiveAsFlow()

	/**
	 * Current state value
	 */
	protected val currentState: S
		get() = _state.value

	/**
	 * Update the current state
	 */
	protected fun setState(newState: S) {
		_state.value = newState
	}

	/**
	 * Update state using a reducer function
	 */
	protected fun setState(reducer: S.() -> S) {
		_state.value = currentState.reducer()
	}

	/**
	 * Send a one-time effect
	 */
	protected fun sendEffect(effect: E) {
		viewModelScope.launch {
			_effect.send(effect)
		}
	}

	/**
	 * Handle user intents - to be implemented by concrete ViewModels
	 */
	abstract override fun handleIntent(intent: I)

	/**
	 * Log intent for debugging
	 */
	protected fun logIntent(intent: I) {
		Timber.d("Intent: ${intent::class.simpleName}")
	}

	override fun onCleared() {
		super.onCleared()
		_effect.close()
	}
}