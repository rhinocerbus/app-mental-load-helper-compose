package com.piledrive.brainhelper.ui.screens.main.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.SuggestionChipDefaults.suggestionChipBorder
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.composite.FullTag
import com.piledrive.brainhelper.data.state.SampleData
import com.piledrive.lib_compose_components.ui.chips.ChipGroup
import com.piledrive.lib_compose_components.ui.spacer.Gap
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme

@Composable
fun NoteCard(
	modifier: Modifier,
	note: Note,
	tags: List<FullTag>,
	onClick: () -> Unit
) {
	Box(modifier = modifier.fillMaxWidth()) {
		Surface(
			modifier = modifier.fillMaxWidth(),
			shape = RoundedCornerShape(12.dp),
			color = MaterialTheme.colorScheme.secondaryContainer,
			onClick = onClick
		) {
			Column(
				modifier = modifier
					.fillMaxWidth()
					.padding(8.dp)
			) {
				Text(modifier = modifier.fillMaxWidth(), text = note.title ?: "No title")

				if (tags.isNotEmpty()) {
					Gap(8)

					ChipGroup {

						tags.forEach {
							val tagColor = it.tagColor
							SuggestionChip(
								onClick = {
									// todo - launch edit tag?
									//coordinator.launchAddTag()
								},
								label = { Text(it.tagText) },
								border = if (tagColor != null) {
									//SuggestionChipDefaults.suggestionChipBorder(true).copy(brush = SolidColor(Color(tagColor.toColorInt())))
									suggestionChipBorder(enabled = true, borderColor = Color(tagColor.toColorInt()))
								} else {
									suggestionChipBorder(enabled = true)
								},
								colors = if (tagColor != null) {
									SuggestionChipDefaults.suggestionChipColors().copy(labelColor = Color(tagColor.toColorInt()))
								} else {
									SuggestionChipDefaults.suggestionChipColors()
								},
								icon = {
									// todo - profile color?
								}
							)
						}
					}
				}

				Gap(8)

				Text(modifier = modifier.fillMaxWidth(), text = note.content ?: "No title")
			}
		}
	}
}

@Preview
@Composable
private fun NoteCardPreview() {
	AppTheme {
		NoteCard(
			modifier = Modifier,
			note = Note(
				title = "Note Title",
				content = "asdfgasdgsdagsdah dofihgodf hopi hjdfaio hpjadsfpojh adspofpadsofjhpdaojhpaeorjh aerh- ae-r4h jear-hjae-rh09jea-r09he0o rjladfkj hpaeojrh padoljfhpadoj fh"
			),
			tags = SampleData.sampleFullTags,
			onClick = {}
		)
	}
}