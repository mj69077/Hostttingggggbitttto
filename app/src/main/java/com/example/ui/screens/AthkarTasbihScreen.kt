package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AthkarCategory
import com.example.data.model.AthkarItem
import com.example.ui.viewmodel.MainViewModel

val TASBIH_PHRASES = listOf(
    "سُبْحَانَ اللَّهِ",
    "الْحَمْدُ لِلَّهِ",
    "لَا إِلَهَ إِلَّا اللَّهُ",
    "اللَّهُ أَكْبَرُ",
    "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
    "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
    "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
    "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ"
)

val ATHKAR_CATEGORIES = listOf(
    AthkarCategory.MORNING,
    AthkarCategory.EVENING,
    AthkarCategory.AFTER_PRAYER,
    AthkarCategory.SLEEP,
    AthkarCategory.WAKEUP,
    AthkarCategory.QURAN_DUAS
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthkarTasbihScreen(
    viewModel: MainViewModel,
    initialCategory: String = "morning",
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Athkar, 1: Tasbih
    var selectedCategoryId by remember { mutableStateOf(initialCategory) }

    val athkarList by viewModel.getAthkarByCategory(selectedCategoryId).collectAsState(initial = emptyList())

    // Tasbih State
    var selectedPhrase by remember { mutableStateOf(TASBIH_PHRASES[0]) }
    var currentTasbihCount by remember { mutableStateOf(0) }
    var tasbihTarget by remember { mutableStateOf(33) }
    var tasbihTotalToday by remember { mutableStateOf(0) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "الأذكار والمسبحة الإلكترونية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("الأذكار المأثورة", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                        modifier = Modifier.testTag("tab_athkar")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("المسبحة الرقمية", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.TouchApp, contentDescription = null) },
                        modifier = Modifier.testTag("tab_tasbih")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedTab == 0) {
                // Athkar Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Category Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        items(ATHKAR_CATEGORIES) { cat ->
                            val isSelected = cat.id == selectedCategoryId
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryId = cat.id },
                                label = { Text(cat.titleArabic) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.testTag("athkar_chip_${cat.id}")
                            )
                        }
                    }

                    // Reset Category Button Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeCategory = ATHKAR_CATEGORIES.firstOrNull { it.id == selectedCategoryId }
                        Text(
                            text = activeCategory?.titleArabic ?: "الأذكار",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        TextButton(
                            onClick = { viewModel.resetAthkarCategory(selectedCategoryId) },
                            modifier = Modifier.testTag("reset_athkar_category_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إعادة تصفير الورد")
                        }
                    }

                    // Athkar List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(athkarList) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("athkar_item_${item.id}"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (item.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                ),
                                border = CardDefaults.outlinedCardBorder(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Arabic Text
                                    Text(
                                        text = item.textArabic,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 26.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    // Virtue / Fadl
                                    if (item.virtueArabic.isNotBlank()) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "الفضل: ${item.virtueArabic}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }

                                    // Progress Bar
                                    val progress = (item.currentCount.toFloat() / item.countTarget.toFloat()).coerceIn(0f, 1f)
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    )

                                    // Counter Button
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "التكرار: ${item.currentCount} / ${item.countTarget}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Button(
                                            onClick = { viewModel.incrementAthkar(item) },
                                            enabled = !item.isCompleted,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.testTag("increment_athkar_${item.id}")
                                        ) {
                                            if (item.isCompleted) {
                                                Icon(Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("تم الإتمام")
                                            } else {
                                                Icon(Icons.Default.TouchApp, contentDescription = "Count", modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("ذكر (${item.countTarget - item.currentCount})")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Interactive Digital Tasbih Screen
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp)
                ) {
                    // Phrase Selector Chips
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(TASBIH_PHRASES) { phrase ->
                                val isSelected = phrase == selectedPhrase
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedPhrase = phrase
                                        currentTasbihCount = 0
                                    },
                                    label = { Text(phrase) },
                                    modifier = Modifier.testTag("tasbih_phrase_chip")
                                )
                            }
                        }
                    }

                    // Target Selector (33, 99, 100)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الهدف: ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            listOf(33, 99, 100).forEach { target ->
                                val isSelected = tasbihTarget == target
                                OutlinedButton(
                                    onClick = { tasbihTarget = target },
                                    colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                                    modifier = Modifier.padding(horizontal = 4.dp).testTag("tasbih_target_$target"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("$target")
                                }
                            }
                        }
                    }

                    // Big Circular Touch Area
                    item {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    val next = currentTasbihCount + 1
                                    tasbihTotalToday += 1
                                    if (next >= tasbihTarget) {
                                        viewModel.recordTasbih(selectedPhrase, next, tasbihTarget)
                                        viewModel.triggerGoalHaptic()
                                        currentTasbihCount = 0
                                    } else {
                                        currentTasbihCount = next
                                        viewModel.triggerTapHaptic()
                                    }
                                }
                                .testTag("tasbih_main_tap_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = selectedPhrase,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "$currentTasbihCount",
                                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 54.sp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "من $tasbihTarget",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Bottom Stats & Reset
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("مجموع تسبيحاتك اليوم", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "$tasbihTotalToday",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    currentTasbihCount = 0
                                    viewModel.triggerTapHaptic()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.testTag("reset_tasbih_counter_btn")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تصفير العداد")
                            }
                        }
                    }
                }
            }
        }
    }
}
