package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface Note2FamilyImpl {
	val familyId: String
	val noteId: String
}

data class Note2FamilySlug(
	override val familyId: String,
	override val noteId: String
): Note2FamilyImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Note2Family(
	override val id: String = "",
	@Json(name = "family_id")
	override val familyId: String = "",
	@Json(name = "note_id")
	override val noteId: String
) : Note2FamilyImpl, SupaBaseModel, FullDataModel {
	override val createdAt: String = ""
}
