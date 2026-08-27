package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.Surah
import com.example.ui.components.GlassCard
import com.example.ui.components.IslamicHeader
import com.example.ui.components.PrayerCountdownCard
import com.example.ui.components.QuranProgressCard
import com.example.ui.viewmodel.MainViewModel

data class QuickFeatureItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val containerColor: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDashboardScreen(
    viewModel: MainViewModel,
    onNavigateToQuran: (surahNumber: Int) -> Unit,
    onNavigateToAthkar: (category: String) -> Unit,
    onNavigateToPrayer: () -> Unit,
    onNavigateToDuas: () -> Unit,
    onNavigateToFatwas: () -> Unit,
    onNavigateToGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val activeKhatmah by viewModel.activeKhatmahPlan.collectAsState()
    val lastReadSurah by viewModel.lastReadSurah.collectAsState()
    val lastReadAyah by viewModel.lastReadAyah.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var showKhatmahDialog by remember { mutableStateOf(false) }
    var showAsmaAllahDialog by remember { mutableStateOf(false) }
    var showZakatDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }

    val quickFeatures = remember(allSurahs) {
        listOf(
            QuickFeatureItem(
                title = "أذكار الصباح",
                subtitle = "تحصين وبداية مباركة",
                icon = Icons.Default.WbSunny,
                containerColor = Color(0xFFFFF3E0),
                onClick = { onNavigateToAthkar("morning") }
            ),
            QuickFeatureItem(
                title = "أذكار المساء",
                subtitle = "سكينة وطمأنينة الليل",
                icon = Icons.Default.NightsStay,
                containerColor = Color(0xFFEDE7F6),
                onClick = { onNavigateToAthkar("evening") }
            ),
            QuickFeatureItem(
                title = "سورة الكهف",
                subtitle = "نور ما بين الجمعتين",
                icon = Icons.Default.AutoStories,
                containerColor = Color(0xFFE8F5E9),
                onClick = { onNavigateToQuran(18) }
            ),
            QuickFeatureItem(
                title = "سورة الملك",
                subtitle = "المانعة من عذاب القبر",
                icon = Icons.Default.Shield,
                containerColor = Color(0xFFE0F2F1),
                onClick = { onNavigateToQuran(67) }
            ),
            QuickFeatureItem(
                title = "فتاوى وأحكام",
                subtitle = "أحكام الصيام والصلاة والزكاة",
                icon = Icons.Default.MenuBook,
                containerColor = Color(0xFFE1F5FE),
                onClick = onNavigateToFatwas
            ),
            QuickFeatureItem(
                title = "حاسبة الزكاة",
                subtitle = "حساب زكاة المال والذهب",
                icon = Icons.Default.AccountBalanceWallet,
                containerColor = Color(0xFFF1F8E9),
                onClick = { showZakatDialog = true }
            ),
            QuickFeatureItem(
                title = "معرض البطاقات",
                subtitle = "خلفيات وبطاقات إسلامية",
                icon = Icons.Default.Image,
                containerColor = Color(0xFFFCE4EC),
                onClick = onNavigateToGallery
            ),
            QuickFeatureItem(
                title = "أسماء الله الحسنى",
                subtitle = "٩٩ اسماً مع الشرح",
                icon = Icons.Default.Star,
                containerColor = Color(0xFFFFF8E1),
                onClick = { showAsmaAllahDialog = true }
            ),
            QuickFeatureItem(
                title = "الأدعية القرآنية",
                subtitle = "جوامع الدعاء والرجاء",
                icon = Icons.Default.VolunteerActivism,
                containerColor = Color(0xFFFBE9E7),
                onClick = onNavigateToDuas
            )
        )
    }

    Scaffold(
        topBar = {
            IslamicHeader(
                selectedCity = selectedCity,
                onCityClick = { showCityDialog = true },
                onSearchClick = { showSearchDialog = true },
                onSettingsClick = { showSettingsDialog = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            // Prayer Countdown Card
            item {
                PrayerCountdownCard(
                    prayers = prayerTimes,
                    onQiblaClick = onNavigateToPrayer
                )
            }

            // Quran Khatmah & Last Read
            item {
                QuranProgressCard(
                    khatmahPlan = activeKhatmah,
                    lastReadSurah = lastReadSurah,
                    lastReadAyah = lastReadAyah,
                    onResumeReading = {
                        val targetSurah = lastReadSurah?.number ?: 1
                        onNavigateToQuran(targetSurah)
                    },
                    onPlanKhatmah = { showKhatmahDialog = true }
                )
            }

            // Daily Hadith / Ayah Inspiration
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "آية اليوم المباركة",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "﴿ أَلا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "سورة الرعد - آية ٢٨",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Section: Quick Portals
            item {
                Text(
                    text = "الأوراد والأبواب المباركة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val chunked = quickFeatures.chunked(2)
                    chunked.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { item ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("quick_feature_${item.title}")
                                        .clickable { item.onClick() },
                                    shape = RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = 2.dp,
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(item.containerColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = item.subtitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showKhatmahDialog) {
        KhatmahPlanDialog(
            currentPlan = activeKhatmah,
            onSavePlan = { days, start -> viewModel.saveKhatmahPlan(days, start) },
            onUpdatePage = { p -> viewModel.updateKhatmahPage(p) },
            onDismiss = { showKhatmahDialog = false }
        )
    }

    if (showAsmaAllahDialog) {
        AsmaAllahDialog(onDismiss = { showAsmaAllahDialog = false })
    }

    if (showZakatDialog) {
        ZakatCalculatorDialog(onDismiss = { showZakatDialog = false })
    }

    if (showCityDialog) {
        CitySelectionDialog(
            selectedCity = selectedCity,
            onSelectCity = { viewModel.setSelectedCity(it) },
            onDismiss = { showCityDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsBackupDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showSearchDialog) {
        var queryText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                showSearchDialog = false
                viewModel.clearSearch()
            },
            title = {
                Text(
                    text = "البحث في آيات وسور القرآن",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = {
                            queryText = it
                            viewModel.search(it)
                        },
                        placeholder = { Text("اكتب كلمة للبحث عنها...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_search_input")
                    )

                    if (isSearching) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (searchResults.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(searchResults) { result ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showSearchDialog = false
                                            viewModel.clearSearch()
                                            onNavigateToQuran(result.ayah.surahNumber)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = result.surahName,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "آية ${result.ayah.verseNumber}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = result.ayah.textArabic,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    } else if (queryText.isNotBlank()) {
                        Text(
                            text = "لا توجد نتائج مطابقة لبحثك",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSearchDialog = false
                        viewModel.clearSearch()
                    }
                ) {
                    Text("إغلاق")
                }
            }
        )
    }
}
