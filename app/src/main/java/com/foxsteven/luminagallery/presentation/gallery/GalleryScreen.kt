package com.foxsteven.luminagallery.presentation.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxsteven.luminagallery.presentation.gallery.components.FilterBottomSheet
import com.foxsteven.luminagallery.presentation.gallery.components.GalleryItem

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier,
    onViewDetail: (String, String) -> Unit,
    onPickImage: ((String) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPickerMode by viewModel.isPickerMode.collectAsStateWithLifecycle()
    val pendingImportUri by viewModel.pendingImportUri.collectAsStateWithLifecycle()
    val filterCriteria by viewModel.filterCriteria.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val savedCriteria by viewModel.savedCriteria.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImagePicked(it) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            GalleryUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            GalleryUiState.Empty -> {
                Text(
                    text = "No images found. Import some!",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            GalleryUiState.NoResults -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "No images match your filters.")
                    TextButton(onClick = { viewModel.onFilterClear() }) {
                        Text("Clear Filters")
                    }
                }
            }
            is GalleryUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.images,
                        key = { "${it.source}_${it.identifier}" }
                    ) { image ->
                        GalleryItem(
                            image = image,
                            onClick = {
                                if (isPickerMode) {
                                    onPickImage?.invoke(image.originalPath)
                                } else {
                                    onViewDetail(image.source, image.identifier.toString())
                                }
                            },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }

        if (!isPickerMode) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = { showFilterSheet = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Import Image")
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            criteria = filterCriteria,
            allTags = allTags,
            savedCriteria = savedCriteria,
            onFilterUpdate = { viewModel.onFilterUpdate(it) },
            onFilterClear = { viewModel.onFilterClear() },
            onSaveCriteria = { viewModel.onSaveCriteria(it) },
            onDeleteSavedCriteria = { viewModel.onDeleteSavedCriteria(it) },
            onApplySavedCriteria = { viewModel.onApplySavedCriteria(it) },
            onDismissRequest = { showFilterSheet = false }
        )
    }

    pendingImportUri?.let { _ ->
        ImportDescriptionDialog(
            onDismiss = { viewModel.onImportCancel() },
            onConfirm = { description ->
                viewModel.onImportConfirm(description)
            }
        )
    }
}

@Composable
fun ImportDescriptionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Description") },
        text = {
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(description) }) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
