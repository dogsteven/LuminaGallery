package com.foxsteven.luminagallery

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.rememberCoroutineScope
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.presentation.detail.ImageDetailScreen
import com.foxsteven.luminagallery.presentation.detail.ImageDetailViewModel
import com.foxsteven.luminagallery.presentation.gallery.GalleryScreen
import com.foxsteven.luminagallery.presentation.gallery.GalleryViewModel
import com.foxsteven.luminagallery.presentation.navigation.GalleryRoute
import com.foxsteven.luminagallery.presentation.navigation.ImageDetailRoute
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
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                NavHost(
                    navController = navController,
                    startDestination = GalleryRoute
                ) {
                    composable<GalleryRoute> {
                        val viewModel: GalleryViewModel = hiltViewModel()
                        GalleryScreen(
                            viewModel = viewModel,
                            onImageClick = { id ->
                                navController.navigate(ImageDetailRoute(id))
                            },
                            onImportRequest = { uri ->
                                scope.launch {
                                    galleryService.importImage(uri)
                                }
                            }
                        )
                    }
                    composable<ImageDetailRoute> {
                        val viewModel: ImageDetailViewModel = hiltViewModel()
                        ImageDetailScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
