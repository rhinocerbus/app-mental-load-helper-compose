package com.piledrive.brainhelper.data.model

import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * todo
 * - add "last updated by" column, db extension
 */
interface NoteImpl {
	val title: String?
	val content: String
}

data class NoteSlug(
	override val title: String?,
	override val content: String
): NoteImpl, SlugDataModel

@JsonClass(generateAdapter = true)
data class Note(
	override val id: String = "",
	@Json(name = "created_at")
	override val createdAt: String = "",
	val updatedAt: String = "",
	override val content: String = "",
	override val title: String?
) : NoteImpl, SupaBaseModel, FullDataModel
