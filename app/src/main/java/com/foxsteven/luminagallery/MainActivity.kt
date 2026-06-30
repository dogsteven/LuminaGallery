package com.foxsteven.luminagallery

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
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
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var galleryService: GalleryService

    @Inject
    lateinit var authenticationService: AuthenticationService

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Privacy: Block screenshots and hide content in app switcher
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

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

                val isPickerMode by galleryViewModel.isPickerMode.collectAsState()

                LaunchedEffect(intent) {
                    val action = intent?.action
                    if ((action == Intent.ACTION_GET_CONTENT) || (action == Intent.ACTION_PICK)) {
                        galleryViewModel.setPickerMode(enabled = true)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    val galleryDestination = TopLevelDestination(
                        name = "Gallery",
                        route = GalleryRoute,
                        selectedIcon = Icons.Filled.Photo,
                        unselectedIcon = Icons.Outlined.Photo
                    )
                    val tagsDestination = TopLevelDestination(
                        name = "Tags",
                        route = TagsRoute,
                        selectedIcon = Icons.AutoMirrored.Filled.Label,
                        unselectedIcon = Icons.AutoMirrored.Outlined.Label
                    )

                    val sections = listOf(
                        NavigationSection(
                            title = "Main",
                            destinations = listOf(galleryDestination, tagsDestination)
                        )
                    )

                    val allDestinations = sections.flatMap { it.destinations }

                    val isTopLevelDestination = allDestinations.any { destination ->
                        currentDestination?.hasRoute(destination.route::class) == true
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = !isPickerMode && isTopLevelDestination,
                        drawerContent = {
                            ModalDrawerSheet {
                                Spacer(Modifier.height(12.dp))
                                sections.forEach { section ->
                                    Text(
                                        text = section.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
                                    )
                                    section.destinations.forEach { destination ->
                                        val selected = currentDestination?.hierarchy?.any {
                                            it.hasRoute(destination.route::class)
                                        } == true
                                        NavigationDrawerItem(
                                            label = { Text(destination.name) },
                                            selected = selected,
                                            onClick = {
                                                scope.launch { drawerState.close() }
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
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                if (isTopLevelDestination) {
                                    val currentTitle = allDestinations.find { destination ->
                                        currentDestination?.hasRoute(destination.route::class) == true
                                    }?.name ?: "LuminaGallery"

                                    TopAppBar(
                                        title = { Text(if (isPickerMode) "Select Photo" else currentTitle) },
                                        navigationIcon = {
                                            if (!isPickerMode) {
                                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                                    Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
                                                }
                                            }
                                        }
                                    )
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
                                        onViewDetail = { source, identifier ->
                                            navController.navigate(ImageDetailRoute(source, identifier))
                                        },
                                        onPickImage = { path ->
                                            handleImageSelection(path)
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
                                        onDismiss = {
                                            navController.popBackStack()
                                        }
                                    )
                                }
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

    private fun handleImageSelection(originalPath: String) {
        val file = File(filesDir, originalPath)
        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )
        
        val resultIntent = Intent().apply {
            data = uri
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}

data class TopLevelDestination(
    val name: String,
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

data class NavigationSection(
    val title: String,
    val destinations: List<TopLevelDestination>
)
