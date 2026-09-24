package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CastBottomSheet
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.HeaderBar
import com.example.ui.components.NotificationsBottomSheet
import com.example.ui.components.PhotoCaptureDialog
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.DarkMatteSurface
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.RadioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RadioAppRoot()
            }
        }
    }
}

@Composable
fun RadioAppRoot(
    viewModel: RadioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureBlack,
        topBar = {
            HeaderBar(
                unreadCount = uiState.unreadNotificationsCount,
                customPhotoUri = uiState.customPhotoUri,
                onNotificationClick = { viewModel.setNotificationsSheetVisible(true) },
                onProfileClick = { viewModel.setCurrentTab(3) }
            )
        },
        bottomBar = {
            RadioBottomNavigationBar(
                currentTab = uiState.currentTab,
                onTabSelected = { viewModel.setCurrentTab(it) }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = uiState.currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_transition",
            modifier = Modifier.fillMaxSize()
        ) { tabIndex ->
            when (tabIndex) {
                0 -> HomeScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    contentPadding = innerPadding
                )
                1 -> ExploreScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    contentPadding = innerPadding
                )
                2 -> FavoritesScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    contentPadding = innerPadding
                )
                3 -> ProfileScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    contentPadding = innerPadding
                )
            }
        }
    }

    // Modal Sheets & Dialogs
    if (uiState.showFilterSheet) {
        FilterBottomSheet(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = { viewModel.setCategoryFilter(it) },
            onDismiss = { viewModel.setFilterSheetVisible(false) }
        )
    }

    if (uiState.showCastSheet) {
        CastBottomSheet(
            connectedDevice = uiState.connectedCastDevice,
            onConnect = { viewModel.connectCastDevice(it) },
            onDisconnect = { viewModel.disconnectCast() },
            onDismiss = { viewModel.setCastSheetVisible(false) }
        )
    }

    if (uiState.showNotificationsSheet) {
        NotificationsBottomSheet(
            notifications = uiState.notifications,
            onDismiss = { viewModel.setNotificationsSheetVisible(false) }
        )
    }

    if (uiState.showPhotoCaptureDialog) {
        PhotoCaptureDialog(
            currentName = uiState.studentName,
            onAvatarPresetSelected = { viewModel.updateStudentPhoto(it) },
            onDismiss = { viewModel.setPhotoCaptureDialogVisible(false) }
        )
    }
}

@Composable
fun RadioBottomNavigationBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkMatteSurface.copy(alpha = 0.98f),
        shadowElevation = 12.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.navigationBarsPadding()
        ) {
            // Tab 0: Inicio
            NavigationBarItem(
                selected = currentTab == 0,
                onClick = { onTabSelected(0) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == 0) Icons.Filled.Radio else Icons.Outlined.Radio,
                        contentDescription = "Inicio"
                    )
                },
                label = {
                    Text(
                        text = "Inicio",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = ElectricPurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_inicio")
            )

            // Tab 1: Explorar
            NavigationBarItem(
                selected = currentTab == 1,
                onClick = { onTabSelected(1) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == 1) Icons.Filled.Explore else Icons.Outlined.Explore,
                        contentDescription = "Explorar"
                    )
                },
                label = {
                    Text(
                        text = "Explorar",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = ElectricPurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_explorar")
            )

            // Tab 2: Favoritos
            NavigationBarItem(
                selected = currentTab == 2,
                onClick = { onTabSelected(2) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == 2) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritos"
                    )
                },
                label = {
                    Text(
                        text = "Favoritos",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = ElectricPurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_favoritos")
            )

            // Tab 3: Mi Perfil
            NavigationBarItem(
                selected = currentTab == 3,
                onClick = { onTabSelected(3) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Mi Perfil"
                    )
                },
                label = {
                    Text(
                        text = "Mi Perfil",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = ElectricPurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_perfil")
            )
        }
    }
}

// Retain Greeting composable for Robolectric compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun RadioPreview() {
    MyApplicationTheme {
        RadioAppRoot()
    }
}
