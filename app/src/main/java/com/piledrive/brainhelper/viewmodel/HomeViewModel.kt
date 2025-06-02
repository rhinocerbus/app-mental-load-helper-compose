package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.repo.FamiliesRepo
import com.piledrive.brainhelper.repo.ProfilesRepo
import com.piledrive.brainhelper.ui.screens.main.MainScreenCoordinator
import com.piledrive.brainhelper.viewmodel.collectors.FamiliesCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val profilesRepo: ProfilesRepo,
	private val familiesRepo: FamiliesRepo
) : ViewModel() {

	init {
		initDataSync()
	}

	private fun initDataSync() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				profilesRepo.initialize().collect {
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

	private val familiesDataCollector = FamiliesCollector(
		viewModelScope,
		familiesRepo.watchFamilies(),
		profilesRepo.watchProfiles()
	)

	private fun initWatches() {
		viewModelScope.launch(Dispatchers.Default) {
			familiesDataCollector.fullFamiliesContentFlow.map {
				Timber.d("FULL FAMILY STATE UPDATE")
			}
			val profilesSource = profilesRepo.watchProfiles()
			profilesSource.collect {
				Timber.d("PROFILES || $it")
			}
		}
		viewModelScope.launch(Dispatchers.Default) {
			val familiesSource = familiesRepo.watchFamilies()
			familiesSource.collect {
				Timber.d("FAMILIES || $it")
			}
		}
	}

	val mainScreenCoordinator = MainScreenCoordinator(
		familiesSourceFlow = familiesDataCollector.fullFamiliesContentFlow
	)

	suspend fun reloadContent() {
	}
}
