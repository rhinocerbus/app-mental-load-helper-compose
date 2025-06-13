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
import com.piledrive.brainhelper.repo.TagsRepo
import com.piledrive.brainhelper.ui.screens.main.MainScreenCoordinator
import com.piledrive.brainhelper.ui.screens.main.views.MainBarCoordinator
import com.piledrive.brainhelper.viewmodel.collectors.FamiliesCollector
import com.piledrive.brainhelper.viewmodel.collectors.NotesCollector
import com.piledrive.brainhelper.viewmodel.collectors.ProfilesCollector
import com.piledrive.brainhelper.viewmodel.collectors.TagsCollector
import com.piledrive.lib_compose_components.ui.dropdown.readonly.multiselect.ReadOnlyMultiSelectDropdownCoordinatorGeneric
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val dataStore: SessionDataStore,
	private val profilesRepo: ProfilesRepo,
	private val familiesRepo: FamiliesRepo,
	private val notesRepo: NotesRepo,
	private val authRepo: AuthRepo,
	private val tagsRepo: TagsRepo
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
		viewModelScope.launch(Dispatchers.Default) {
			profilesRepo.watchSelfProfile().collect {

			}
		}


		viewModelScope.launch {
			withContext(Dispatchers.Default) {
				tagsCollector.tagsContentFlow.collect {
					barCoordinator.tagsCoordinator.updateOptionsPool(it)
				}
			}
		}
	}

	private val familiesDataCollector = FamiliesCollector(
		viewModelScope,
		familiesRepo.watchContent()
			.map { families ->
				val activeId = dataStore.checkActiveFamilyId()
				if (families.isNotEmpty() && activeId == null || families.firstOrNull { it.id == activeId } == null) {
					dataStore.updateActiveFamilyId(families.first().id)
				}
				families
			},
		dataStore.watchActiveFamilyId()
	)

	private val profilesDataCollector = ProfilesCollector(
		viewModelScope,
		profilesRepo.watchSelfProfile(),
		profilesRepo.watchContent()
	)

	private val notesCollector = NotesCollector(
		viewModelScope,
		notesRepo.watchContent()
	)

	private val tagsCollector = TagsCollector(
		viewModelScope,
		tagsRepo.watchContent(),
		profilesRepo.watchContent()
	)

	val mainScreenCoordinator = MainScreenCoordinator(
		selfProfileSourceFlow = profilesDataCollector.selfContentFlow,
		familiesSourceFlow = familiesDataCollector.familyContentFlow,
		familyMembersSourceFlow = profilesDataCollector.profilesContentFlow,
		notesSourceFlow = notesCollector.notesContentFlow,
	)

	val barCoordinator = MainBarCoordinator(
		tagsCoordinator = ReadOnlyMultiSelectDropdownCoordinatorGeneric<FullTag>(
			optionTextMutator = { it.tagText },
			optionBackgroundColor = { Color(it.tagColor.toColorInt()) /*Color.fromHex(it.tagColor)*/ },
			optionIdForSelectedCheck = { it.id }
		),
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

	private val _launchScratchPadEvent: Channel<Boolean> = Channel()
	val launchScratchPadEvent: ReceiveChannel<Boolean> = _launchScratchPadEvent

	/////////////////////////////////////////////////
	//  endregion
}
