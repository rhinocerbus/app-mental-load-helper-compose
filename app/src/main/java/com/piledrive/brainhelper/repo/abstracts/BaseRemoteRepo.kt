package com.piledrive.brainhelper.repo.abstracts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

abstract class BaseRemoteRepo(
	private val scope: CoroutineScope
) {

	abstract fun initStateFlow(): StateFlow<Int>

	protected fun initWatch() {
		scope.launch(Dispatchers.Default) {
			initStateFlow().collect {
				if (it == 1) {
					setupSources()
				}
			}
		}
	}

	/*
	 * flow merging:
	 * all require either caching latest manually or recompiling latest into a passthrough model
	 * https://dev.to/vtsen/kotlin-flow-combine-merge-and-zip-4l75
	 * - marge: straight pass-through emit of input flows, no transform
	 * -- dealing with impl issue where the merge output is Unit, requires locally caching on each incoming flow to use in the final collect
	 * -- based on docs, could be due to only expecting one type
	 * - zip & combine: requires compiling an output per change of incoming flow via a transform, cannot defer/delay the transform
	 * -- combine: fires on any incoming flow change, with latest from all inputs
	 * -- zip: similar but seems to miss latest from some inputs
	 * merge seems more performant with being able to delay compilation
	 */

	protected abstract suspend fun setupSources()
}