package com.piledrive.brainhelper.data.model.composite

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.SlugDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel

interface FullNoteImpl {
	val title: String?
	val content: String
}

data class FullNoteSlug(
	override val title: String?,
	override val content: String,
	val tagIds: List<String>
) : FullNoteImpl, SlugDataModel

data class FullNote(
	val note: Note,
	val tags: List<FullTag>
) : SupaBaseModel, FullDataModel {
	override val id: String = note.id
	override val createdAt: String = note.createdAt
	val noteTitle: String = note.title ?: "No title"
	val noteCotnent: String = note.content
}
