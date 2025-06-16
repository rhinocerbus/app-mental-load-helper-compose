package com.piledrive.brainhelper.data.model.composite

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.lib_supabase_powersync.data.model.abstracts.powersync.FullDataModel
import com.piledrive.lib_supabase_powersync.data.model.abstracts.supabase.SupaBaseModel

class FullTag(
	private val tag: Tag,
	private val profile: Profile?
) : SupaBaseModel, FullDataModel {
	override val id: String = tag.id
	override val createdAt: String = tag.createdAt
	val tagText: String = profile?.firstName ?: tag.label ?: "ERR"
	val tagColor: String? = profile?.color ?: tag.color
}