package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.repo.AuthRepo
import com.piledrive.brainhelper.repo.FamiliesRepo
import com.piledrive.brainhelper.repo.NotesRepo
import com.piledrive.brainhelper.repo.ProfilesRepo
import com.piledrive.brainhelper.ui.screens.main.MainScreenCoordinator
import com.piledrive.brainhelper.ui.screens.main.views.MainBar
import com.piledrive.brainhelper.ui.screens.main.views.MainBarCoordinator
import com.piledrive.brainhelper.viewmodel.collectors.FamiliesCollector
import com.piledrive.brainhelper.viewmodel.collectors.NotesCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val profilesRepo: ProfilesRepo,
	private val familiesRepo: FamiliesRepo,
	private val notesRepo: NotesRepo,
	private val authRepo: AuthRepo
) : ViewModel() {

	init {
		initAuthWatch()
		initDataSync()
	}

	private fun initAuthWatch() {
		viewModelScope.launch {
			authRepo.grabAuthStatusFlow().collect { status ->
				when (status) {
					is SessionStatus.NotAuthenticated -> {
						_loggedOutEvent.send(true)
					}
					else -> {}
				}
			}
		}
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

	val barCoordinator = MainBarCoordinator(
		onLogout = {
			logout()
		}
	)

	suspend fun reloadContent() {
	}


	//  region auth
	/////////////////////////////////////////////////

	private val _loggedOutEvent: Channel<Boolean> = Channel()
	val loggedOutEvent: ReceiveChannel<Boolean> = _loggedOutEvent

	private fun logout() {
		viewModelScope.launch {
			authRepo.logout()
		}
	}

	/////////////////////////////////////////////////
	//  endregion
}
