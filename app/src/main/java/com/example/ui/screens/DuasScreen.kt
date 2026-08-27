package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

data class DuaItem(
    val id: Int,
    val category: String,
    val title: String,
    val textArabic: String,
    val reference: String
)

val DUAS_DATABASE = listOf(
    DuaItem(
        1, "quranic", "دعاء الهداية والرحمة",
        "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
        "سورة آل عمران - آية ٨"
    ),
    DuaItem(
        2, "quranic", "دعاء تيسير الأمور وشرح الصدر",
        "رَبِّ اشْرَحْ لِي صَدْرِي * وَيَسِّرْ لِي أَمْرِي * وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي * يَفْقَهُوا قَوْلِي",
        "سورة طه - آيات ٢٥-٢٨"
    ),
    DuaItem(
        3, "quranic", "دعاء المغفرة والرحمة",
        "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
        "سورة الأعراف - آية ٢٣"
    ),
    DuaItem(
        4, "quranic", "دعاء النجاة وتفريج الكرب (دعاء ذي النون)",
        "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
        "سورة الأنبياء - آية ٨٧"
    ),
    DuaItem(
        5, "quranic", "دعاء حسنة الدنيا والآخرة",
        "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
        "سورة البقرة - آية ٢٠١"
    ),
    DuaItem(
        6, "prophetic", "دعاء تفريج الهم والحزن",
        "اللَّهُمَّ إِنِّي عَبْدُكَ، وَابْنُ عَبْدِكَ، وَابْنُ أَمَتِكَ، نَاصِيَتِي بِيَدِكَ، مَاضٍ فِيَّ حُكْمُكَ، عَدْلٌ فِيَّ قَضَاؤُكَ، أَسْأَلُكَ بِكُلِّ اسْمٍ هُوَ لَكَ سَمَّيْتَ بِهِ نَفْسَكَ، أَوْ عَلَّمْتَهُ أَحَدًا مِنْ خَلْقِكَ، أَوْ أَنْزَلْتَهُ فِي كِتَابِكَ، أَوْ اسْتَأْثَرْتَ بِهِ فِي عِلْمِ الغَيْبِ عِنْدَكَ، أَنْ تَجْعَلَ القُرْآنَ رَبِيعَ قَلْبِي، وَنُورَ صَدْرِي، وَجَلَاءَ حُزْنِي، وَذَهَابَ هَمِّي.",
        "رواه أحمد وصححه الألباني"
    ),
    DuaItem(
        7, "prophetic", "سيد الاستغفار",
        "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي، فَاغْفِرْ لِي؛ فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
        "صحيح البخاري"
    ),
    DuaItem(
        8, "prophetic", "دعاء العافية والمعافاة",
        "اللَّهُمَّ إِنِّي أَسْأَلُكَ العَفْوَ وَالعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ العَفْوَ وَالعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي، وَآمِنْ رَوْعَاتِي.",
        "سنن أبي داود"
    ),
    DuaItem(
        9, "healing", "دعاء الشفاء للمريض",
        "اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ البَاسَ، اشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا.",
        "صحيح البخاري ومسلم"
    ),
    DuaItem(
        10, "sustenance", "دعاء الرزق وسداد الدين",
        "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ.",
        "سنن الترمذي"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("all") }

    val filteredDuas = remember(selectedCategory) {
        if (selectedCategory == "all") DUAS_DATABASE
        else DUAS_DATABASE.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الأدعية المأثورة والقرآنية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == "all",
                        onClick = { selectedCategory = "all" },
                        label = { Text("جميع الأدعية") },
                        modifier = Modifier.testTag("dua_chip_all")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == "quranic",
                        onClick = { selectedCategory = "quranic" },
                        label = { Text("أدعية القرآن") },
                        modifier = Modifier.testTag("dua_chip_quranic")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == "prophetic",
                        onClick = { selectedCategory = "prophetic" },
                        label = { Text("أدعية السنة النبوية") },
                        modifier = Modifier.testTag("dua_chip_prophetic")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == "healing",
                        onClick = { selectedCategory = "healing" },
                        label = { Text("الشفاء والعافية") },
                        modifier = Modifier.testTag("dua_chip_healing")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == "sustenance",
                        onClick = { selectedCategory = "sustenance" },
                        label = { Text("الرزق وتفريج الهم") },
                        modifier = Modifier.testTag("dua_chip_sustenance")
                    )
                }
            }

            // Duas List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredDuas) { dua ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dua_card_${dua.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dua.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val text = "${dua.title}\n\n«${dua.textArabic}»\n\nالمصدر: ${dua.reference}"
                                        val clip = ClipData.newPlainText("Dua", text)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "تم نسخ الدعاء بنجاح", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp).testTag("copy_dua_${dua.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = "« ${dua.textArabic} »",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 26.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = dua.reference,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
