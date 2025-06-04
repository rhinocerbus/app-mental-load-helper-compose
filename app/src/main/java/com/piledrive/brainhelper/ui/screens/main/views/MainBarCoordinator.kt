package com.piledrive.brainhelper.ui.screens.main.views

interface MainBarCoordinatorImpl {
	val onLogout: () -> Unit
}

class MainBarCoordinator(
	override val onLogout: () -> Unit
) : MainBarCoordinatorImpl

val stubMainBarCoordinator = MainBarCoordinator(
	onLogout = {}
)