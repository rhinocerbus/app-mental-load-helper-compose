package com.piledrive.brainhelper.ui.screens.scratch

import com.piledrive.brainhelper.data.model.Scratch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface ScratchPadScreenCoordinatorImpl {
	val scratchesSourceFlow: StateFlow<Scratch?>
	val onTextChanged: (String) -> Unit
}

class ScratchPadScreenCoordinator(
	override val scratchesSourceFlow: StateFlow<Scratch?>,
	override val onTextChanged: (String) -> Unit
) : ScratchPadScreenCoordinatorImpl {

}

val stubScratchPadScreenCoordinator = ScratchPadScreenCoordinator(
	scratchesSourceFlow = MutableStateFlow(null),
	onTextChanged = { _ -> }
)