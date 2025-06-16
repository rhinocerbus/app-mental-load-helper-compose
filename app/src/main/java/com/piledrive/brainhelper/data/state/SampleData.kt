package com.piledrive.brainhelper.data.state

import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.brainhelper.data.model.composite.FullTag

object SampleData {

	val sampleTag = Tag(label = "Test Tag", color = "#ffff0000")

	val sampleFullTags = listOf(
		FullTag(
			tag = sampleTag,
			profile = null
		)
	)
}