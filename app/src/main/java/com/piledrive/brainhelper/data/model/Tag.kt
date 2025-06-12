package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tag(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String = "",
	@Json(name = "profile_id")
	val profileId: String? = null,
	val label: String? = null,
	val color: String? = null,
) : SupaBaseModel

