package com.thunderplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.thunderplay.ui.theme.Cyan500
import com.thunderplay.ui.theme.GradientVioletCyan
import com.thunderplay.ui.theme.Violet600

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String // Kept string for now logic
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (Any) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home, "com.thunderplay.ui.navigation.HomeRoute"),
        BottomNavItem("Search", Icons.Filled.Search, "com.thunderplay.ui.navigation.SearchRoute"),
        BottomNavItem("Library", Icons.Filled.Favorite, "com.thunderplay.ui.navigation.LibraryRoute"), // Using Favorite for Library
        BottomNavItem("Downloads", Icons.Filled.Download, "com.thunderplay.ui.navigation.DownloadsRoute")
    )

    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.8f), // Semi-transparent black
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(32.dp))
            .height(80.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                         Color(0xFF1A1A1A),
                         Color(0xFF2D2D2D)
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            ),
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            // Simple string check for now. Ideally we use KClass or similar but string representation of route object usually works for simple check if we are consistent.
            // But with type safe nav, the route in backstack is Type map. 
            // We will let the caller decide "isSelected"
             
             // Wait, for this component to be pure, I should probably just let it be valid.
             // I will fix the "route" matching in MainActivity to pass the simplistic route object or check.
             
             // Let's rely on simple Route mapping in MainActivity and pass just the "selected" state or logic.
             // But for now, to keep this component self-contained for display:
            
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item) }, // Pass item back to handle navigation logic
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Cyan500 else Color.Gray
                    )
                },
                label = {
                    if(isSelected) {
                        Text(
                            text = item.title,
                            color = Cyan500
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent, // Remove standard indicator
                    selectedIconColor = Cyan500,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Cyan500,
                    unselectedTextColor = Color.Transparent
                )
            )
        }
    }
}
