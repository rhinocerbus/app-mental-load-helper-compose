package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.piledrive.brainhelper.data.powersync.enums.SplashState
import com.piledrive.brainhelper.repo.SampleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
	private val repo: SampleRepo
) : ViewModel() {

	init {
		viewModelScope.launch {
			checkAuthenticated()
		}
	}

	private val _contentState = MutableStateFlow<SplashState>(SplashState.LOADING)
	val contentState: StateFlow<SplashState> = _contentState

	private suspend fun checkAuthenticated() {

	}
}
