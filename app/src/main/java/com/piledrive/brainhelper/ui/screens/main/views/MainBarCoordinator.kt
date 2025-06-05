package com.piledrive.brainhelper.ui.screens.main.views

import com.piledrive.lib_compose_components.ui.dropdown.readonly.multiselect.ReadOnlyMultiSelectDropdownCoordinatorGeneric

interface MainBarCoordinatorImpl {
	val tagsCoordinator: ReadOnlyMultiSelectDropdownCoordinatorGeneric<Any>
	val onLogout: () -> Unit
}

class MainBarCoordinator(
	override val tagsCoordinator: ReadOnlyMultiSelectDropdownCoordinatorGeneric<Any>,
	override val onLogout: () -> Unit
) : MainBarCoordinatorImpl

val stubMainBarCoordinator = MainBarCoordinator(
	tagsCoordinator = ReadOnlyMultiSelectDropdownCoordinatorGeneric(),
	onLogout = {}
)