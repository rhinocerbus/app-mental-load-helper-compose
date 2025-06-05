package com.piledrive.brainhelper.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tag(
	val id: String = "",
	@Json(name = "created_at")
	val createdAt: String = "",
	@Json(name = "profile_id")
	val profileId: String? = null,
	val label: String? = null,
	val color: String? = null,
)

