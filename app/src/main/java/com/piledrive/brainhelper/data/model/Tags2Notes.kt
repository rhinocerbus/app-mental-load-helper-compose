package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface Tags2NotesImpl {
	val noteId: String
	val tagId: String
	val _enabled: Int
}

data class Tags2NotesSlug(
	override val noteId: String,
	override val tagId: String,
	override val _enabled: Int,
) : Tags2NotesImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Tags2Notes(
	override val id: String = "",
	@Json(name = "note_id")
	override val noteId: String,
	@Json(name = "tag_id")
	override val tagId: String = "",
	@Json(name = "enabled")
	override val _enabled: Int = 0,
) : Tags2NotesImpl, SupaBaseModel, FullDataModel {
	override val createdAt: String = ""
	val enabled: Boolean = _enabled == 1
}
