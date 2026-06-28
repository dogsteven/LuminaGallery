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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxsteven.luminagallery.presentation.gallery.components.GalleryItem

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onImageClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pendingImportUri by viewModel.pendingImportUri.collectAsStateWithLifecycle()

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
            is GalleryUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.images,
                        key = { it.id }
                    ) { image ->
                        GalleryItem(
                            image = image,
                            onClick = { onImageClick(image.id) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Import Image")
        }
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
