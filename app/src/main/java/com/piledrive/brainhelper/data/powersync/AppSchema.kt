package com.piledrive.brainhelper.data.powersync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val AppSchema: Schema = Schema(
	listOf(
		Table(
			name = "families",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("name"),
			)
		),
		Table(
			name = "profiles",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				//Column.text("created_at"),
				Column.text("first_name"),
				Column.text("last_name"),
				Column.text("color"),
			)
		),
		Table(
			name = "profile_to_family",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("profile_id"),
				Column.text("family_id")
			)
		),
		Table(
			name = "notes",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("content"),
				Column.text("family_id")
			)
		),
		Table(
			name = "notes_to_family",
			columns = listOf(
				// added by powersync
				//Column.text("id"),
				Column.text("note_id"),
				Column.text("family_id")
			)
		),
	)
)