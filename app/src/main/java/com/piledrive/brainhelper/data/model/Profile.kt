package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Profile(
	override val id: String = "",
	@Json(name = "first_name")
	val firstName: String = "",
	@Json(name = "last_name")
	val lastName: String = "",
	val color: String = "",
) : SupaBaseModel {
	val fullName = "$firstName $lastName"

	@Deprecated(level = DeprecationLevel.ERROR, message = "not part of remote model but need to conform to interface")
	override val createdAt: String = ""
}
