package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface NoteImpl {
	val updatedAt: String
	val title: String?
	val content: String
}

data class NoteSlug(
	override val updatedAt: String,
	override val title: String?,
	override val content: String
): NoteImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Note(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String = "",
	override val updatedAt: String = "",
	override val content: String = "",
	override val title: String?
) : NoteImpl, SupaBaseModel, FullDataModel
