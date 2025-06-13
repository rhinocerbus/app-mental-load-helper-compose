package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface ScratchImpl {
	val familyId: String
	val content: String?
}

data class ScratchSlug(
	override val familyId: String,
	override val content: String?
) : ScratchImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Scratch(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String = "",
	@Json(name = "family_id")
	override val familyId: String = "",
	override val content: String? = null,
) : ScratchImpl, SupaBaseModel, FullDataModel


