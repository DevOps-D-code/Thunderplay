package com.thunderplay.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.thunderplay.ui.screens.home.HomeScreen
import com.thunderplay.ui.screens.nowplaying.NowPlayingScreen
import com.thunderplay.ui.screens.search.SearchScreen
import kotlinx.serialization.Serializable
import androidx.compose.ui.Modifier

// Type-safe navigation destinations
@Serializable object HomeRoute
@Serializable object SearchRoute
@Serializable object LibraryRoute
@Serializable class NowPlayingRoute(val trackId: String)
@Serializable class PlaylistRoute(val playlistId: String)
@Serializable object SettingsRoute
@Serializable object FavoritesRoute
@Serializable object PlaylistsRoute
@Serializable object DownloadsRoute

@Composable
fun ThunderplayNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier
) {
    
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(SearchRoute) },
                onNavigateToNowPlaying = { trackId -> 
                    navController.navigate(NowPlayingRoute(trackId)) 
                },
                onNavigateToLibrary = { navController.navigate(LibraryRoute) }
            )
        }
        
        composable<SearchRoute> {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onTrackClick = { trackId ->
                    navController.navigate(NowPlayingRoute(trackId))
                }
            )
        }
        
        composable<LibraryRoute> {
            com.thunderplay.ui.screens.library.LibraryScreen(
                onNavigateBack = { navController.popBackStack() },
                onTrackClick = { trackId ->
                    navController.navigate(NowPlayingRoute(trackId))
                }
            )
        }
        
        composable<SettingsRoute> {
            com.thunderplay.ui.screens.settings.SettingsScreen()
        }

        composable<FavoritesRoute> {
            com.thunderplay.ui.screens.favorites.FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onTrackClick = { trackId -> navController.navigate(NowPlayingRoute(trackId)) }
            )
        }

        composable<PlaylistsRoute> {
            com.thunderplay.ui.screens.playlists.PlaylistsScreen()
        }

        composable<DownloadsRoute> {
            com.thunderplay.ui.screens.downloads.DownloadsScreen(
                onNavigateBack = { navController.popBackStack() },
                onTrackClick = { trackId -> navController.navigate(NowPlayingRoute(trackId)) }
            )
        }
        
        composable<NowPlayingRoute> {
            NowPlayingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
