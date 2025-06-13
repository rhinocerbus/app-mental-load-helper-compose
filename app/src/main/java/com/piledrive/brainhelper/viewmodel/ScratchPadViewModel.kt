package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.data.model.ScratchSlug
import com.piledrive.brainhelper.datastore.SessionDataStore
import com.piledrive.brainhelper.repo.AuthRepo
import com.piledrive.brainhelper.repo.FamiliesRepo
import com.piledrive.brainhelper.repo.NotesRepo
import com.piledrive.brainhelper.repo.ProfilesRepo
import com.piledrive.brainhelper.repo.ScratchRepo
import com.piledrive.brainhelper.ui.screens.scratch.ScratchPadScreenCoordinator
import com.piledrive.brainhelper.viewmodel.collectors.FamiliesCollector
import com.piledrive.brainhelper.viewmodel.collectors.NotesCollector
import com.piledrive.brainhelper.viewmodel.collectors.ScratchCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
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
		viewModelScope.launch {
			textInput
				.mapNotNull { it }
				.distinctUntilChanged { old, new -> old == new }
				.debounce(500L)
				.collect {
					if (scratchCollector.scratchContentFlow.value != null) {
						scratchRepo.watchContent()
					}
				}

		}
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
		scratchCollector.scratchContentFlow,
		// could do some debounced save, would need to add a saved status icon or something
		onTextChanged = { updatedText ->
			viewModelScope.launch {
				writeScratchNotes(updatedText)
			}
		}
	)

	private suspend fun writeScratchNotes(updatedText: String) {
		val activeScratch = coordinator.scratchesSourceFlow.value
		val activeFamily = dataStore.checkActiveFamilyId() ?: return

		if (activeScratch == null) {
			scratchRepo.addNewData(ScratchSlug(familyId = activeFamily, content = updatedText))
		} else {
			scratchRepo.updateData(activeScratch.copy(content = updatedText))
		}
	}

	private val textInput: MutableStateFlow<String?> = MutableStateFlow(null)


	suspend fun reloadContent() {
	}
}
