package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface Tags2NotesImpl {
	val noteId: String
	val tagId: String
}

data class Tags2NotesSlug(
	override val noteId: String,
	override val tagId: String,
) : Tags2NotesImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Tags2Notes(
	override val id: String = "",
	@Json(name = "note_id")
	override val noteId: String,
	@Json(name = "tag_id")
	override val tagId: String = "",
) : Tags2NotesImpl, SupaBaseModel, FullDataModel {
	override val createdAt: String = ""
}
