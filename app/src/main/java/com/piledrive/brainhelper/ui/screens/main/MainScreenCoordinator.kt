package com.piledrive.brainhelper.ui.screens.main

import com.piledrive.brainhelper.viewmodel.collectors.FamiliesCollector
import com.piledrive.lib_compose_components.ui.dropdown.readonly.ReadOnlyDropdownCoordinatorGeneric
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface MainScreenCoordinatorImpl {
	val familiesSourceFlow: StateFlow<FamiliesCollector.FullFamiliesContentState>
}

class MainScreenCoordinator(
	override val familiesSourceFlow: StateFlow<FamiliesCollector.FullFamiliesContentState>
): MainScreenCoordinatorImpl {

}

val stubMainScreenCoordinator = MainScreenCoordinator(
	familiesSourceFlow = MutableStateFlow(FamiliesCollector.FullFamiliesContentState()),
)