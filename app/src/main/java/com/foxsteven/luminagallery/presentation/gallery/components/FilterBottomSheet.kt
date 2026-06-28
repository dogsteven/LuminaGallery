package com.foxsteven.luminagallery.presentation.gallery.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foxsteven.luminagallery.data.model.FilterCriteria
import com.foxsteven.luminagallery.data.model.SavedCriteriaEntity
import com.foxsteven.luminagallery.data.model.TagEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    criteria: FilterCriteria,
    allTags: List<TagEntity>,
    savedCriteria: List<SavedCriteriaEntity>,
    onFilterUpdate: (FilterCriteria) -> Unit,
    onFilterClear: () -> Unit,
    onSaveCriteria: (String) -> Unit,
    onDeleteSavedCriteria: (SavedCriteriaEntity) -> Unit,
    onApplySavedCriteria: (SavedCriteriaEntity) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSaveDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                text = "Filter Images",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Text Query
            OutlinedTextField(
                value = criteria.query ?: "",
                onValueChange = { onFilterUpdate(criteria.copy(query = it)) },
                label = { Text("Search description") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (!criteria.query.isNullOrBlank()) {
                        IconButton(onClick = { onFilterUpdate(criteria.copy(query = null)) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selection
            Row(modifier = Modifier.fillMaxWidth()) {
                DatePickerField(
                    label = "Start Date",
                    timestamp = criteria.startDate,
                    onDateSelected = { onFilterUpdate(criteria.copy(startDate = it)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                DatePickerField(
                    label = "End Date",
                    timestamp = criteria.endDate,
                    onDateSelected = { onFilterUpdate(criteria.copy(endDate = it)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            Text(text = "Tags", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allTags.forEach { tag ->
                    FilterChip(
                        selected = criteria.tagNames.contains(tag.name),
                        onClick = {
                            val newTags = if (criteria.tagNames.contains(tag.name)) {
                                criteria.tagNames - tag.name
                            } else {
                                criteria.tagNames + tag.name
                            }
                            onFilterUpdate(criteria.copy(tagNames = newTags))
                        },
                        label = { Text(tag.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFilterClear) {
                    Text("Clear All")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showSaveDialog = true }) {
                    Text("Save Filter")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Saved Criteria
            Text(
                text = "Saved Filters",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(savedCriteria) { saved ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onApplySavedCriteria(saved) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(saved.name, modifier = Modifier.fillMaxWidth())
                        }
                        IconButton(onClick = { onDeleteSavedCriteria(saved) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        SaveCriteriaDialog(
            onDismiss = { showSaveDialog = false },
            onConfirm = { name ->
                onSaveCriteria(name)
                showSaveDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    timestamp: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = timestamp
    )

    OutlinedCard(
        onClick = { showDatePicker = true },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            val dateText = timestamp?.let {
                val date = Instant.fromEpochMilliseconds(it)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                "${date.year}-${date.monthNumber}-${date.dayOfMonth}"
            } ?: label
            Text(text = dateText, style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDateSelected(null)
                    showDatePicker = false
                }) { Text("Clear") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun SaveCriteriaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Filter") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Filter Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
