package com.piledrive.brainhelper.ui.screens.main.views

import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.lib_compose_components.ui.dropdown.readonly.multiselect.ReadOnlyMultiSelectDropdownCoordinatorGeneric

interface MainBarCoordinatorImpl {
	val tagsCoordinator: ReadOnlyMultiSelectDropdownCoordinatorGeneric<Tag>
	val onLogout: () -> Unit
}

class MainBarCoordinator(
	override val tagsCoordinator: ReadOnlyMultiSelectDropdownCoordinatorGeneric<Tag>,
	override val onLogout: () -> Unit
) : MainBarCoordinatorImpl

val stubMainBarCoordinator = MainBarCoordinator(
	tagsCoordinator = ReadOnlyMultiSelectDropdownCoordinatorGeneric(),
	onLogout = {}
)