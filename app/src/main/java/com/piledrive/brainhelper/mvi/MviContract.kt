package com.piledrive.brainhelper.mvi

/**
 * Base interface for MVI architecture components
 */
interface MviContract {
	/**
	 * Represents user intentions/actions in MVI
	 */
	interface Intent

	/**
	 * Represents the current state of the screen/feature
	 */
	interface State

	/**
	 * Represents one-time events/side effects (navigation, snackbars, etc.)
	 */
	interface Effect
}

/**
 * Base interface for MVI ViewModels
 */
interface MviViewModel<I : MviContract.Intent, S : MviContract.State, E : MviContract.Effect> {
	/**
	 * Current state of the ViewModel
	 */
	val state: kotlinx.coroutines.flow.StateFlow<S>

	/**
	 * One-time effects/events
	 */
	val effect: kotlinx.coroutines.flow.Flow<E>

	/**
	 * Handle user intents
	 */
	fun handleIntent(intent: I)
}