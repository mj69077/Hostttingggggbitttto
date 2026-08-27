package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.components.SleepTimerDialog
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    viewModel: MainViewModel,
    initialSurahNumber: Int = 1,
    modifier: Modifier = Modifier
) {
    val allSurahs by viewModel.allSurahs.collectAsState()
    val allIndexes by viewModel.allQuranIndexes.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    var selectedSurah by remember { mutableStateOf<Surah?>(null) }
    var verses by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var surahSearchQuery by remember { mutableStateOf("") }
    var selectedIndexTab by remember { mutableStateOf(0) } // 0: السور, 1: الأجزاء, 2: الأحزاب, 3: الفهرس الموضوعي
    var fontSizeSp by remember { mutableStateOf(24f) }

    var showTafsirDialogForAyah by remember { mutableStateOf<Ayah?>(null) }
    var showReciterDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Automatically select initial surah if passed
    LaunchedEffect(initialSurahNumber, allSurahs) {
        if (allSurahs.isNotEmpty() && selectedSurah == null) {
            val target = allSurahs.firstOrNull { it.number == initialSurahNumber } ?: allSurahs[0]
            selectedSurah = target
        }
    }

    // Load verses when selectedSurah changes
    LaunchedEffect(selectedSurah) {
        selectedSurah?.let { surah ->
            viewModel.getVersesForSurah(surah.number).collect { loadedVerses ->
                verses = loadedVerses
            }
        }
    }

    // Auto scroll to highlighted ayah when playing
    LaunchedEffect(playbackState.currentAyahNumber) {
        if (selectedSurah?.number == playbackState.currentSurah?.number && playbackState.isPlaying) {
            val index = (playbackState.currentAyahNumber - 1).coerceIn(0, verses.size - 1)
            if (index >= 0 && verses.isNotEmpty()) {
                listState.animateScrollToItem(index)
            }
        }
    }

    val filteredSurahs = remember(allSurahs, surahSearchQuery) {
        if (surahSearchQuery.isBlank()) allSurahs
        else allSurahs.filter {
            it.nameArabic.contains(surahSearchQuery.trim()) ||
            it.nameEnglish.contains(surahSearchQuery.trim(), ignoreCase = true) ||
            it.number.toString() == surahSearchQuery.trim()
        }
    }

    val filteredAjzaa = remember(allIndexes, surahSearchQuery) {
        val list = allIndexes.filter { it.type == "juz" }
        if (surahSearchQuery.isBlank()) list
        else list.filter {
            it.titleArabic.contains(surahSearchQuery.trim()) ||
            it.subtitleArabic.contains(surahSearchQuery.trim()) ||
            it.startSurahName.contains(surahSearchQuery.trim()) ||
            it.keywords.contains(surahSearchQuery.trim())
        }
    }

    val filteredAhzab = remember(allIndexes, surahSearchQuery) {
        val list = allIndexes.filter { it.type == "hizb" }
        if (surahSearchQuery.isBlank()) list
        else list.filter {
            it.titleArabic.contains(surahSearchQuery.trim()) ||
            it.subtitleArabic.contains(surahSearchQuery.trim()) ||
            it.startSurahName.contains(surahSearchQuery.trim()) ||
            it.keywords.contains(surahSearchQuery.trim())
        }
    }

    val filteredThematic = remember(allIndexes, surahSearchQuery) {
        val list = allIndexes.filter { it.type == "thematic" }
        if (surahSearchQuery.isBlank()) list
        else list.filter {
            it.titleArabic.contains(surahSearchQuery.trim()) ||
            it.subtitleArabic.contains(surahSearchQuery.trim()) ||
            it.startSurahName.contains(surahSearchQuery.trim()) ||
            it.topicGroup.contains(surahSearchQuery.trim()) ||
            it.description.contains(surahSearchQuery.trim()) ||
            it.keywords.contains(surahSearchQuery.trim())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedSurah != null) {
                        Column {
                            Text(
                                text = "سورة ${selectedSurah!!.nameArabic}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "آياتها ${selectedSurah!!.totalVerses} • ${selectedSurah!!.revelationTypeArabic} • ص ${selectedSurah!!.pageNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "سور القرآن الكريم",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (selectedSurah != null) {
                        IconButton(
                            onClick = { selectedSurah = null },
                            modifier = Modifier.testTag("back_to_surah_list")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (selectedSurah != null) {
                        // Font Size adjust
                        IconButton(
                            onClick = {
                                fontSizeSp = if (fontSizeSp >= 32f) 20f else fontSizeSp + 4f
                            },
                            modifier = Modifier.testTag("font_size_toggle_btn")
                        ) {
                            Icon(Icons.Default.FormatSize, contentDescription = "Font Size")
                        }

                        // Play/Pause current surah
                        val isCurrentSurahPlaying = playbackState.isPlaying && playbackState.currentSurah?.number == selectedSurah?.number
                        IconButton(
                            onClick = {
                                if (isCurrentSurahPlaying) {
                                    viewModel.togglePlayPause()
                                } else {
                                    selectedSurah?.let { viewModel.playSurah(it) }
                                }
                            },
                            modifier = Modifier.testTag("play_surah_header_btn")
                        ) {
                            Icon(
                                imageVector = if (isCurrentSurahPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = "Play Surah",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Reciter Chooser
                        IconButton(
                            onClick = { showReciterDialog = true },
                            modifier = Modifier.testTag("open_reciters_dialog")
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = "Reciter")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedSurah == null) {
                // Quran Indexes Screen (Tabs: السور, الأجزاء, الأحزاب, الفهرس الموضوعي)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = surahSearchQuery,
                        onValueChange = { surahSearchQuery = it },
                        placeholder = { 
                            Text(
                                when (selectedIndexTab) {
                                    0 -> "ابحث عن اسم السورة أو رقمها..."
                                    1 -> "ابحث في أجزاء القرآن الثلاثين..."
                                    2 -> "ابحث في أحزاب القرآن الستين..."
                                    else -> "ابحث في موضوعات القرآن الكريم..."
                                }
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (surahSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { surahSearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("surah_index_search_input")
                    )

                    // Index Tabs
                    PrimaryTabRow(
                        selectedTabIndex = selectedIndexTab,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedIndexTab == 0,
                            onClick = { selectedIndexTab = 0 },
                            text = { Text("السور (114)", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedIndexTab == 1,
                            onClick = { selectedIndexTab = 1 },
                            text = { Text("الأجزاء (30)", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedIndexTab == 2,
                            onClick = { selectedIndexTab = 2 },
                            text = { Text("الأحزاب (60)", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedIndexTab == 3,
                            onClick = { selectedIndexTab = 3 },
                            text = { Text("الموضوعي", fontWeight = FontWeight.Bold) }
                        )
                    }

                    when (selectedIndexTab) {
                        0 -> {
                            // Surahs List
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 90.dp)
                            ) {
                                items(filteredSurahs) { surah ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("surah_card_${surah.number}")
                                            .clickable { selectedSurah = surah },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 1.dp,
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                                            ) {
                                                // Number badge
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${surah.number}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }

                                                Column {
                                                    Text(
                                                        text = surah.nameArabic,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "${surah.revelationTypeArabic} • ${surah.totalVerses} آية",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "صفحة ${surah.pageNumber}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = surah.nameEnglish,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Ajza' List (30)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 90.dp)
                            ) {
                                items(filteredAjzaa) { indexItem ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val target = allSurahs.firstOrNull { it.number == indexItem.startSurahNumber }
                                                if (target != null) selectedSurah = target
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 1.dp,
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${indexItem.itemNumber}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = indexItem.titleArabic,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = indexItem.subtitleArabic,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "ص ${indexItem.startPage}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = indexItem.startSurahName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Ahzab List (60)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 90.dp)
                            ) {
                                items(filteredAhzab) { indexItem ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val target = allSurahs.firstOrNull { it.number == indexItem.startSurahNumber }
                                                if (target != null) selectedSurah = target
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 1.dp,
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${indexItem.itemNumber}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = indexItem.titleArabic,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = indexItem.subtitleArabic,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "ص ${indexItem.startPage}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = indexItem.startSurahName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            // Thematic Topics List (الفهرس الموضوعي)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 90.dp)
                            ) {
                                items(filteredThematic) { indexItem ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val target = allSurahs.firstOrNull { it.number == indexItem.startSurahNumber }
                                                if (target != null) selectedSurah = target
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 1.dp,
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                SuggestionChip(
                                                    onClick = {},
                                                    label = { Text(indexItem.topicGroup, style = MaterialTheme.typography.labelSmall) }
                                                )
                                                Text(
                                                    text = "سورة ${indexItem.startSurahName} (ص ${indexItem.startPage})",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text(
                                                text = indexItem.titleArabic,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Text(
                                                text = indexItem.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Text(
                                                text = "المواضع: ${indexItem.subtitleArabic}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Surah Reader Content
                val currentSurah = selectedSurah!!
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp)
                ) {
                    // Header / Basmalah
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "سورة ${currentSurah.nameArabic}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${currentSurah.revelationTypeArabic} • عدد آياتها: ${currentSurah.totalVerses} • ترتيبها: ${currentSurah.number}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (currentSurah.number != 9 && currentSurah.number != 1) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Verses List
                    items(verses) { ayah ->
                        val isHighlighted = playbackState.isPlaying &&
                                playbackState.currentSurah?.number == currentSurah.number &&
                                playbackState.currentAyahNumber == ayah.verseNumber

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isHighlighted) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = if (isHighlighted) CardDefaults.outlinedCardBorder() else null,
                            shadowElevation = if (isHighlighted) 3.dp else 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ayah_item_${ayah.verseNumber}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Verse Action Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Verse badge
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${ayah.verseNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isHighlighted) Color.White else MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Tafsir Button
                                        IconButton(
                                            onClick = { showTafsirDialogForAyah = ayah },
                                            modifier = Modifier.size(32.dp).testTag("tafsir_ayah_${ayah.verseNumber}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = "Tafsir",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Play from this ayah
                                        IconButton(
                                            onClick = {
                                                viewModel.playSurah(currentSurah, startAyah = ayah.verseNumber)
                                                viewModel.seekToAyah(ayah.verseNumber)
                                            },
                                            modifier = Modifier.size(32.dp).testTag("play_ayah_${ayah.verseNumber}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Ayah",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Bookmark
                                        IconButton(
                                            onClick = {
                                                viewModel.saveLastRead(currentSurah.number, ayah.verseNumber)
                                            },
                                            modifier = Modifier.size(32.dp).testTag("bookmark_ayah_${ayah.verseNumber}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Ayah Arabic Text
                                Text(
                                    text = ayah.textArabic,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = fontSizeSp.sp,
                                        lineHeight = (fontSizeSp * 1.7f).sp
                                    ),
                                    fontWeight = FontWeight.Medium,
                                    color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Brief Tafsir line
                                if (ayah.tafsirArabic.isNotBlank()) {
                                    Text(
                                        text = ayah.tafsirArabic,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showTafsirDialogForAyah != null && selectedSurah != null) {
        TafsirDialog(
            surah = selectedSurah!!,
            ayah = showTafsirDialogForAyah!!,
            onDismiss = { showTafsirDialogForAyah = null }
        )
    }

    if (showReciterDialog) {
        ReciterSelectionDialog(
            selectedReciter = playbackState.selectedReciter,
            onSelectReciter = { newReciter ->
                selectedSurah?.let { surah ->
                    viewModel.playSurah(surah, reciter = newReciter)
                }
            },
            onDismiss = { showReciterDialog = false }
        )
    }

    if (showSleepTimerDialog) {
        SleepTimerDialog(
            currentOption = playbackState.sleepTimerOption,
            remainingSeconds = playbackState.sleepTimerRemainingSeconds,
            onSelectOption = { option -> viewModel.setSleepTimer(option) },
            onDismiss = { showSleepTimerDialog = false }
        )
    }
}
