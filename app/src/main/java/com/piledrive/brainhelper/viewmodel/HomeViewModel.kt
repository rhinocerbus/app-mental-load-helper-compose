package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.repo.ProfilesRepo
import com.piledrive.brainhelper.repo.SampleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val repo: ProfilesRepo
) : ViewModel() {

	init {
		initDataSync()
	}

	private fun initDataSync() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				repo.initialize().collect {
					Timber.d("locations repo init status: $it")
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

	private fun initWatches() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				val profilesSource = repo.watchTags()
				profilesSource.collect {
					Timber.d("PROFILES || $it")
				}
			}
		}
	}



	private val _contentState = MutableStateFlow<Int>(0)
	val contentState: StateFlow<Int> = _contentState

	suspend fun reloadContent() {

	}

}
