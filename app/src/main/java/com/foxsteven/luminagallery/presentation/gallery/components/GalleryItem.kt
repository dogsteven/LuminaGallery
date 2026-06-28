package com.foxsteven.luminagallery.presentation.gallery.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import com.foxsteven.luminagallery.data.model.ImageEntity
import java.io.File

@Composable
fun GalleryItem(
    image: ImageEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val thumbnailFile = File(context.filesDir, image.thumbnailPath)

    Surface(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f)
    ) {
        AsyncImage(
            model = thumbnailFile,
            contentDescription = image.description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
