package com.foxsteven.luminagallery

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.presentation.detail.ImageDetailScreen
import com.foxsteven.luminagallery.presentation.detail.ImageDetailViewModel
import com.foxsteven.luminagallery.presentation.gallery.GalleryScreen
import com.foxsteven.luminagallery.presentation.gallery.GalleryViewModel
import com.foxsteven.luminagallery.presentation.navigation.GalleryRoute
import com.foxsteven.luminagallery.presentation.navigation.ImageDetailRoute
import com.foxsteven.luminagallery.presentation.navigation.TagsRoute
import com.foxsteven.luminagallery.presentation.tags.TagManagementScreen
import com.foxsteven.luminagallery.presentation.tags.TagViewModel
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
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val topLevelDestinations = listOf(
                    TopLevelDestination(
                        name = "Gallery",
                        route = GalleryRoute,
                        selectedIcon = Icons.Filled.Photo,
                        unselectedIcon = Icons.Outlined.Photo
                    ),
                    TopLevelDestination(
                        name = "Tags",
                        route = TagsRoute,
                        selectedIcon = Icons.Filled.Label,
                        unselectedIcon = Icons.Outlined.Label
                    )
                )

                // Show bottom bar only on top-level destinations
                val showBottomBar = topLevelDestinations.any { destination ->
                    currentDestination?.hasRoute(destination.route::class) == true
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                topLevelDestinations.forEach { destination ->
                                    val selected = currentDestination?.hierarchy?.any { 
                                        it.hasRoute(destination.route::class) 
                                    } == true
                                    
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(destination.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                                contentDescription = destination.name
                                            )
                                        },
                                        label = { Text(destination.name) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = GalleryRoute,
                        modifier = Modifier.padding(innerPadding)
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
                        composable<TagsRoute> {
                            val viewModel: TagViewModel = hiltViewModel()
                            TagManagementScreen(viewModel = viewModel)
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
}

data class TopLevelDestination(
    val name: String,
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
