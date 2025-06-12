package com.piledrive.brainhelper.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.data.model.composite.FullTag
import com.piledrive.brainhelper.datastore.SessionDataStore
import com.piledrive.brainhelper.repo.AuthRepo
import com.piledrive.brainhelper.repo.FamiliesRepo
import com.piledrive.brainhelper.repo.NotesRepo
import com.piledrive.brainhelper.repo.ProfilesRepo
import com.piledrive.brainhelper.repo.ScratchRepo
import com.piledrive.brainhelper.repo.TagsRepo
import com.piledrive.brainhelper.ui.screens.main.MainScreenCoordinator
import com.piledrive.brainhelper.ui.screens.main.views.MainBarCoordinator
import com.piledrive.brainhelper.ui.screens.scratch.ScratchPadScreenCoordinator
import com.piledrive.brainhelper.viewmodel.collectors.FamiliesCollector
import com.piledrive.brainhelper.viewmodel.collectors.NotesCollector
import com.piledrive.brainhelper.viewmodel.collectors.ScratchCollector
import com.piledrive.brainhelper.viewmodel.collectors.TagsCollector
import com.piledrive.lib_compose_components.ui.dropdown.readonly.multiselect.ReadOnlyMultiSelectDropdownCoordinatorGeneric
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
class ScratchPadViewModel @Inject constructor(
	private val dataStore: SessionDataStore,
	private val profilesRepo: ProfilesRepo,
	private val familiesRepo: FamiliesRepo,
	private val notesRepo: NotesRepo,
	private val authRepo: AuthRepo,
	private val scratchRepo: ScratchRepo
) : ViewModel() {

	init {
		initDataSync()
	}

	private fun initDataSync() {
		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				profilesRepo.initStateFlow.collect {
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

	private fun initWatches() {
	}

	private val familiesDataCollector = FamiliesCollector(
		viewModelScope,
		profilesRepo.watchSelfProfile(),
		familiesRepo.watchContent(),
		profilesRepo.watchContent()
	)

	private val notesCollector = NotesCollector(
		viewModelScope,
		notesRepo.watchContent()
	)


	private val scratchCollector = ScratchCollector(
		viewModelScope,
		scratchRepo.watchContent(),
		dataStore.watchActiveFamilyId()
	)

	val coordinator = ScratchPadScreenCoordinator(
		scratchCollector.scratchContentFlow
	)

	suspend fun reloadContent() {
	}


}
