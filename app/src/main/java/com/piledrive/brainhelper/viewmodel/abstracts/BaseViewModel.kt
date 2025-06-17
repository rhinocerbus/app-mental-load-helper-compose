package com.piledrive.brainhelper.viewmodel.abstracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class BaseViewModel() : ViewModel() {

	protected abstract val initStateFlow: StateFlow<Int>
	//protected abstract fun initStateFlow(): StateFlow<Int>
/*
	init {
		initDataSync()
	}*/

	protected fun initDataSync() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				initStateFlow.collect {
					Timber.d("repo init status: $it")
					when (it) {
						-1 -> {
							// init error
							// todo - add error ui state
						}

						0 -> {
							// started
						}

						1 -> {
							// done
							initWatches()
						}
					}
				}
			}
		}
	}

	protected abstract fun initWatches()
}