package com.piledrive.brainhelper.data.state

import com.piledrive.brainhelper.data.model.Family

data class FamilyContentState(
	val families: List<Family> = listOf(),
	val activeFamily: Family? = null
) {
}