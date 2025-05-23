package com.piledrive.brainhelper.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Profile(
	val id: String = "",
	@Json(name = "first_name")
	val firstName: String = "",
	@Json(name = "last_name")
	val lastName: String = "",
	val color: String = "",
)
