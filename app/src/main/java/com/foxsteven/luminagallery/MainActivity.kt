package com.foxsteven.luminagallery

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.foxsteven.luminagallery.application.AuthenticationService
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.presentation.auth.AuthScreen
import com.foxsteven.luminagallery.presentation.auth.SecurityViewModel
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
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var galleryService: GalleryService

    @Inject
    lateinit var authenticationService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Observe process lifecycle for app-wide backgrounding
        ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    authenticationService.onAppBackgrounded(System.currentTimeMillis())
                }
                Lifecycle.Event.ON_START -> {
                    authenticationService.onAppForegrounded(System.currentTimeMillis())
                }
                else -> {}
            }
        })

        setContent {
            LuminaGalleryTheme {
                val isAuthorized by authenticationService.isAuthorized.collectAsState()
                
                // ViewModels scoped to the Activity for durability
                val galleryViewModel: GalleryViewModel = hiltViewModel()
                val tagViewModel: TagViewModel = hiltViewModel()
                val securityViewModel: SecurityViewModel = hiltViewModel()

                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
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
                            selectedIcon = Icons.AutoMirrored.Filled.Label,
                            unselectedIcon = Icons.AutoMirrored.Outlined.Label
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
                                GalleryScreen(
                                    viewModel = galleryViewModel,
                                    onImageClick = { id ->
                                        navController.navigate(ImageDetailRoute(id))
                                    }
                                )
                            }
                            composable<TagsRoute> {
                                TagManagementScreen(viewModel = tagViewModel)
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

                    // Auth Overlay
                    if (!isAuthorized) {
                        AuthScreen(viewModel = securityViewModel)
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
