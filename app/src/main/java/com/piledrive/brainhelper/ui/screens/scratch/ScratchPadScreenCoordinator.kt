package com.piledrive.brainhelper.ui.screens.scratch

import com.piledrive.brainhelper.data.model.Scratch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface ScratchPadScreenCoordinatorImpl {
	val scratchesSourceFlow: StateFlow<Scratch?>
}

class ScratchPadScreenCoordinator(
	override val scratchesSourceFlow: StateFlow<Scratch?>,
) : ScratchPadScreenCoordinatorImpl {

}

val stubScratchPadScreenCoordinator = ScratchPadScreenCoordinator(
	scratchesSourceFlow = MutableStateFlow(null),
)