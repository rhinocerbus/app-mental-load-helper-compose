package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.repo.FamiliesRepo
import com.piledrive.brainhelper.repo.NotesRepo
import com.piledrive.brainhelper.repo.ProfilesRepo
import com.piledrive.brainhelper.ui.screens.main.MainScreenCoordinator
import com.piledrive.brainhelper.viewmodel.collectors.FamiliesCollector
import com.piledrive.brainhelper.viewmodel.collectors.NotesCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val profilesRepo: ProfilesRepo,
	private val familiesRepo: FamiliesRepo,
	private val notesRepo: NotesRepo,
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

	private fun initWatches() {
		viewModelScope.launch(Dispatchers.Default) {
			profilesRepo.watchSelfProfile().collect {

			}
		}
	}

	private val familiesDataCollector = FamiliesCollector(
		viewModelScope,
		profilesRepo.watchSelfProfile(),
		familiesRepo.watchFamilies(),
		profilesRepo.watchProfiles()
	)

	private val notesCollector = NotesCollector(
		viewModelScope,
		notesRepo.watchNotes()
	)

	val mainScreenCoordinator = MainScreenCoordinator(
		selfProfileSourceFlow = familiesDataCollector.selfContentFlow,
		familiesSourceFlow = familiesDataCollector.familiesContentFlow,
		familyMembersSourceFlow = familiesDataCollector.profilesContentFlow,
		notesSourceFlow = notesCollector.notesContentFlow
	)

	suspend fun reloadContent() {
	}
}
