package com.piledrive.brainhelper.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Family(
	val id: String = "",
	@Json(name = "created_at")
	val createdAt: String = "",
	val name: String = "",
)
