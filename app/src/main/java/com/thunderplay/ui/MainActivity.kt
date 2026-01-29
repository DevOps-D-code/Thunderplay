package com.thunderplay.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.thunderplay.ui.components.BottomNavBar
import com.thunderplay.ui.components.BottomNavItem
import com.thunderplay.ui.navigation.DownloadsRoute
import com.thunderplay.ui.navigation.FavoritesRoute
import com.thunderplay.ui.navigation.HomeRoute
import com.thunderplay.ui.navigation.LibraryRoute
import com.thunderplay.ui.navigation.PlaylistsRoute
import com.thunderplay.ui.navigation.SearchRoute
import com.thunderplay.ui.navigation.SettingsRoute
import com.thunderplay.ui.navigation.ThunderplayNavHost
import com.thunderplay.ui.theme.ThunderplayTheme
import com.thunderplay.util.rememberThunderplayPermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThunderplayTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val mainViewModel: MainViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val currentTrack by mainViewModel.currentTrack.collectAsState()
                val playerState by mainViewModel.playerState.collectAsState()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currentRoute = currentDestination?.route
                        // Show bottom bar only on top-level routes
                         val showBottomBar = currentRoute?.contains("HomeRoute") == true ||
                                            currentRoute?.contains("SearchRoute") == true ||
                                            currentRoute?.contains("LibraryRoute") == true ||
                                            currentRoute?.contains("SettingsRoute") == true ||
                                            currentRoute?.contains("FavoritesRoute") == true ||
                                            currentRoute?.contains("PlaylistsRoute") == true ||
                                            currentRoute?.contains("DownloadsRoute") == true

                        androidx.compose.foundation.layout.Column {
                            // Mini Player
                            if (currentTrack != null) {
                                com.thunderplay.ui.components.MiniPlayer(
                                    track = currentTrack!!,
                                    isPlaying = playerState.isPlaying,
                                    progress = mainViewModel.sliderPosition,
                                    onPlayPause = mainViewModel::togglePlayPause,
                                    onSkipPrevious = mainViewModel::skipPrevious,
                                    onSkipNext = mainViewModel::skipNext,
                                    onClick = { 
                                        navController.navigate(com.thunderplay.ui.navigation.NowPlayingRoute(currentTrack!!.id)) {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        
                            if (showBottomBar) {
                                BottomNavBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { item ->
                                        val navItem = item as BottomNavItem
                                        
                                        val route = when (navItem.title) {
                                            "Home" -> HomeRoute
                                            "Search" -> SearchRoute
                                            "Library" -> LibraryRoute
                                            "Downloads" -> DownloadsRoute
                                            else -> HomeRoute
                                        }
                                        
                                        navController.navigate(route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    ThunderplayNavHost(
                        navController = navController,
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
