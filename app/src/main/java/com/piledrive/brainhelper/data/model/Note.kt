package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Note(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String = "",
	val content: String = "",
) : SupaBaseModel
