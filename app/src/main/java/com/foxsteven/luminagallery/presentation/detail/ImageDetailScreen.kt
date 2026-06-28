package com.foxsteven.luminagallery.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    viewModel: ImageDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                ImageDetailUiState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                ImageDetailUiState.Error -> {
                    Text(text = "Failed to load image", color = Color.White)
                }
                is ImageDetailUiState.Success -> {
                    val context = LocalContext.current
                    val imageFile = File(context.filesDir, state.image.originalPath)
                    
                    ZoomableAsyncImage(
                        model = imageFile,
                        contentDescription = state.image.description,
                        modifier = Modifier.fillMaxSize(),
                        state = rememberZoomableImageState(rememberZoomableState())
                    )
                }
            }
        }
    }
}
