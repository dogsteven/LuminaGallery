package com.foxsteven.luminagallery.presentation.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxsteven.luminagallery.presentation.gallery.components.GalleryItem

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
