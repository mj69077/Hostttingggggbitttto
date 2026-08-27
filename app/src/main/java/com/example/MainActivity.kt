package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.AudioPlayerBar
import com.example.ui.components.SleepTimerDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

enum class NavigationTab(
    val titleArabic: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    QURAN("المصحف", Icons.Filled.AutoStories, Icons.Outlined.AutoStories),
    ATHKAR("الأذكار", Icons.Filled.TouchApp, Icons.Outlined.TouchApp),
    PRAYER("المواقيت", Icons.Filled.AccessTime, Icons.Outlined.AccessTime),
    DUAS("الأدعية", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

enum class SecondaryScreen {
    NONE,
    FATWAS,
    GALLERY
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }
    var secondaryScreen by remember { mutableStateOf(SecondaryScreen.NONE) }
    var quranTargetSurah by remember { mutableStateOf(1) }
    var athkarTargetCategory by remember { mutableStateOf("morning") }

    val playbackState by viewModel.playbackState.collectAsState()
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showReciterDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (secondaryScreen == SecondaryScreen.NONE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    // Persistent Floating Media Player Bar
                    AnimatedVisibility(
                        visible = playbackState.currentSurah != null,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        AudioPlayerBar(
                            playbackState = playbackState,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSeekTo = { pos -> viewModel.seekTo(pos) },
                            onRewind15 = { viewModel.seekRewind15() },
                            onForward15 = { viewModel.seekForward15() },
                            onSpeedChange = { speed -> viewModel.setPlaybackSpeed(speed) },
                            onSleepTimerClick = { showSleepTimerDialog = true },
                            onReciterClick = { showReciterDialog = true },
                            onClose = { viewModel.stopAudio() }
                        )
                    }

                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    selectedTab = tab
                                    secondaryScreen = SecondaryScreen.NONE
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.titleArabic
                                    )
                                },
                                label = { Text(tab.titleArabic) },
                                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (secondaryScreen) {
                SecondaryScreen.FATWAS -> {
                    FatwasScreen(
                        viewModel = viewModel,
                        onBack = { secondaryScreen = SecondaryScreen.NONE }
                    )
                }
                SecondaryScreen.GALLERY -> {
                    IslamicGalleryScreen(
                        onBack = { secondaryScreen = SecondaryScreen.NONE }
                    )
                }
                SecondaryScreen.NONE -> {
                    when (selectedTab) {
                        NavigationTab.DASHBOARD -> {
                            DailyDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToQuran = { surahNum ->
                                    quranTargetSurah = surahNum
                                    selectedTab = NavigationTab.QURAN
                                },
                                onNavigateToAthkar = { cat ->
                                    athkarTargetCategory = cat
                                    selectedTab = NavigationTab.ATHKAR
                                },
                                onNavigateToPrayer = {
                                    selectedTab = NavigationTab.PRAYER
                                },
                                onNavigateToDuas = {
                                    selectedTab = NavigationTab.DUAS
                                },
                                onNavigateToFatwas = {
                                    secondaryScreen = SecondaryScreen.FATWAS
                                },
                                onNavigateToGallery = {
                                    secondaryScreen = SecondaryScreen.GALLERY
                                }
                            )
                        }
                        NavigationTab.QURAN -> {
                            QuranReaderScreen(
                                viewModel = viewModel,
                                initialSurahNumber = quranTargetSurah
                            )
                        }
                        NavigationTab.ATHKAR -> {
                            AthkarTasbihScreen(
                                viewModel = viewModel,
                                initialCategory = athkarTargetCategory
                            )
                        }
                        NavigationTab.PRAYER -> {
                            PrayerQiblaScreen(viewModel = viewModel)
                        }
                        NavigationTab.DUAS -> {
                            DuasScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    if (showSleepTimerDialog) {
        SleepTimerDialog(
            currentOption = playbackState.sleepTimerOption,
            remainingSeconds = playbackState.sleepTimerRemainingSeconds,
            onSelectOption = { option -> viewModel.setSleepTimer(option) },
            onDismiss = { showSleepTimerDialog = false }
        )
    }

    if (showReciterDialog) {
        ReciterSelectionDialog(
            selectedReciter = playbackState.selectedReciter,
            onSelectReciter = { newReciter ->
                playbackState.currentSurah?.let { surah ->
                    viewModel.playSurah(surah, reciter = newReciter)
                }
            },
            onDismiss = { showReciterDialog = false }
        )
    }
}
