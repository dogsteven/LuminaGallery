package com.foxsteven.luminagallery.presentation.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foxsteven.luminagallery.presentation.detail.components.DetailContentSheet
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState
import java.io.File
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    viewModel: ImageDetailViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showTagSheet by remember { mutableStateOf(value = false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = viewModel::rotateImage) {
                        Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = "Rotate Image")
                    }
                    IconButton(onClick = { viewModel.shareImage(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Image")
                    }
                    IconButton(onClick = { showTagSheet = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Image Info")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
                    val rotation by viewModel.rotation.collectAsStateWithLifecycle()

                    val offsetY = remember { Animatable(0f) }
                    val coroutineScope = rememberCoroutineScope()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(0, offsetY.value.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        coroutineScope.launch {
                                            offsetY.snapTo(offsetY.value + dragAmount.y)
                                        }
                                    },
                                    onDragEnd = {
                                        if (offsetY.value > 300f) {
                                            onDismiss()
                                        } else {
                                            coroutineScope.launch {
                                                offsetY.animateTo(0f, animationSpec = tween(300))
                                            }
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        ZoomableAsyncImage(
                            model = imageFile,
                            contentDescription = state.image.description,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationZ = rotation },
                            state = rememberZoomableImageState(rememberZoomableState())
                        )
                    }

                    if (showTagSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showTagSheet = false },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            DetailContentSheet(
                                image = state.image,
                                assignedTags = state.assignedTags,
                                availableTags = state.availableTags,
                                onAddTag = { viewModel.addTag(it) },
                                onRemoveTag = viewModel::removeTag,
                                updateDescription = viewModel::updateDescription,
                                onDeleteClick = {
                                    showDeleteDialog = true
                                    showTagSheet = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Image") },
            text = { Text("Are you sure you want to permanently delete this image? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteImage(onDismiss)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
