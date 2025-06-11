package com.piledrive.brainhelper.data.model.composite

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.model.Tag

class FullTag(
	private val tag: Tag,
	private val profile: Profile?
) {
	val id: String = tag.id
	val tagText: String = profile?.firstName ?: tag.label ?: "ERR"
	val tagColor: String = profile?.color ?: tag.color ?: "#00000000"
}