package com.foxsteven.luminagallery

import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.presentation.gallery.GalleryScreen
import com.foxsteven.luminagallery.presentation.gallery.GalleryViewModel
import com.foxsteven.luminagallery.ui.theme.LuminaGalleryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var galleryService: GalleryService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuminaGalleryTheme {
                val scope = rememberCoroutineScope()
                val galleryViewModel: GalleryViewModel = viewModel()

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    uri?.let {
                        scope.launch {
                            galleryService.importImage(it)
                        }
                    }
                }

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Import Image")
                        }
                    }
                ) { innerPadding ->
                    GalleryScreen(
                        viewModel = galleryViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
