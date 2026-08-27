package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class AsmaAllahItem(
    val id: Int,
    val name: String,
    val meaning: String
)

val ASMA_ALLAH_LIST = listOf(
    AsmaAllahItem(1, "الرَّحْمَنُ", "الذي وسعت رحمته كل شيء في الدنيا والآخرة"),
    AsmaAllahItem(2, "الرَّحِيمُ", "المنعم بلطائف النعم والرحيم بالمؤمنين"),
    AsmaAllahItem(3, "المَلِكُ", "المتصرف في ملكه كيف يشاء بلا منازع"),
    AsmaAllahItem(4, "القُدُّوسُ", "المنزه والمطهر عن كل عيب ونقص"),
    AsmaAllahItem(5, "السَّلَامُ", "ناشر الأمان والسلام والبريء من كل آفة"),
    AsmaAllahItem(6, "المُؤْمِنُ", "الذي أمن عباده من ظلمه والمصدق لرسله"),
    AsmaAllahItem(7, "المُهَيْمِنُ", "الرقيب الشاهد الحافظ لكل شيء"),
    AsmaAllahItem(8, "العَزِيزُ", "القوي الغالب الذي لا يغلب أمره"),
    AsmaAllahItem(9, "الجَبَّارُ", "الذي يجبر قلوب المنكسرين وينفذ مشيئته"),
    AsmaAllahItem(10, "المُتَكَبِّرُ", "المتعالي عن صفات الخلق والمنفرد بالعظمة"),
    AsmaAllahItem(11, "الخَالِقُ", "المبدع والمقدر للأشياء على غير مثال سابق"),
    AsmaAllahItem(12, "البَارِئُ", "الموجد للأشياء من العدم والمميز لها"),
    AsmaAllahItem(13, "المُصَوِّرُ", "الذي أنشأ خلقه على صور مختلفة وهيئات بديعة"),
    AsmaAllahItem(14, "الغَفَّارُ", "الساتر لذنوب عباده المرة بعد المرة"),
    AsmaAllahItem(15, "القَهَّارُ", "الذي قهر الكائنات بعزته وخضعت له الرقاب"),
    AsmaAllahItem(16, "الوَهَّابُ", "الكثير العطايا والمنح بلا مقابل"),
    AsmaAllahItem(17, "الرَّزَّاقُ", "المتكفل بأرزاق العباد وأقواتهم أجمعين"),
    AsmaAllahItem(18, "الفَتَّاحُ", "الذي يفتح أبواب الرحمة والرزق والخير"),
    AsmaAllahItem(19, "العَلِيمُ", "المحيط بكل شيء علماً ظاهراً وباطناً"),
    AsmaAllahItem(20, "القَابِضُ", "المضيق للأرزاق والنفوس بحكمته وعدله"),
    AsmaAllahItem(21, "البَاسِطُ", "الموسع للأرزاق والمبهج للقلوب بجوده"),
    AsmaAllahItem(22, "الخَافِضُ", "الذي يخفض الكافرين والظالمين بالإذلال"),
    AsmaAllahItem(23, "الرَّافِعُ", "المعلي لأقدار أوليائه والمقرب لهم"),
    AsmaAllahItem(24, "المُعِزُّ", "الذي يهب العزة لمن يشاء من عباده"),
    AsmaAllahItem(25, "المُذِلُّ", "الذي يذل أهل الكفر والعناد والطغيان"),
    AsmaAllahItem(26, "السَّمِيعُ", "الذي لا يخفى عليه صوت ولا سر"),
    AsmaAllahItem(27, "البَصِيرُ", "الذي يرى دبيب النملة السوداء على الصخرة الملساء"),
    AsmaAllahItem(28, "الحَكَمُ", "العادل الذي لا يجور في قضائه وأحكامه"),
    AsmaAllahItem(29, "العَدْلُ", "المنزه عن الجور والظلم في تدبيره وخلقه"),
    AsmaAllahItem(30, "اللَّطِيفُ", "البر بعباده الرفيق بهم الموصل للخير بخفاء"),
    AsmaAllahItem(31, "الخَبِيرُ", "العالم بدقائق الأمور وخفايا الصدور"),
    AsmaAllahItem(32, "الحَلِيمُ", "الذي لا يعاجل بالعقوبة بل يمهل ليتوبوا"),
    AsmaAllahItem(33, "العَظِيمُ", "ذو العظمة والجلال الذي لا تقاس عظمته"),
    AsmaAllahItem(34, "الغَفُورُ", "المغطي للخطايا المتجاوز عن السيئات"),
    AsmaAllahItem(35, "الشَّكُورُ", "الذي يثيب على العمل القليل بالثواب الجزيل"),
    AsmaAllahItem(36, "العَلِيُّ", "المتعالي في ذاته وصفاته فوق خلقه"),
    AsmaAllahItem(37, "الكَبِيرُ", "العظيم الكبرياء والجلال الذي كل شيء دونه"),
    AsmaAllahItem(38, "الحَفِيظُ", "الحافظ للسموات والأرض وأعمال العباد"),
    AsmaAllahItem(39, "المُقِيتُ", "الخالق للأقوات والمتكفل بإيصالها إلى الأبدان"),
    AsmaAllahItem(40, "الحَسِيبُ", "الكافي لعباده والمحاسب لهم على أعمالهم"),
    AsmaAllahItem(41, "الجَلِيلُ", "الموصوف بنعوت الجلال والكمال ورفعة القدر"),
    AsmaAllahItem(42, "الكَرِيمُ", "الكثير الخير الجواد الذي يعطي من غير سؤال"),
    AsmaAllahItem(43, "الرَّقِيبُ", "المطلع على ما أكنته الضمائر وحفظته الجوارح"),
    AsmaAllahItem(44, "المُجِيبُ", "الذي يجيب دعاء الداعين ويكشف كرب المكروبين"),
    AsmaAllahItem(45, "الوَاسِعُ", "الذي وسع كل شيء رحمة وعلماً وإحساناً"),
    AsmaAllahItem(46, "الحَكِيمُ", "المحكم للأمور الذي يضع الأشياء في مواضعها"),
    AsmaAllahItem(47, "الوَدُودُ", "المحب لأوليائه والمودود في قلوب المؤمنين"),
    AsmaAllahItem(48, "المَجِيدُ", "الشريف الذات الواسع الكرم والفضل العظيم"),
    AsmaAllahItem(49, "البَاعِثُ", "الذي يبعث الموتى للحساب والجزاء يوم القيامة"),
    AsmaAllahItem(50, "الشَّهِيدُ", "الحاضر الذي لا يغيب عنه شيء من الكائنات"),
    AsmaAllahItem(51, "الحَقُّ", "الثابت الموجود يقيناً الذي لا شك في ربوبيته"),
    AsmaAllahItem(52, "الوَكِيلُ", "المتفوض إليه تدبير خلقه المتكفل بمصالحهم"),
    AsmaAllahItem(53, "القَوِيُّ", "التام القوة والقدرة الذي لا يعجزه شيء"),
    AsmaAllahItem(54, "المَتِينُ", "الشديد القوة الذي لا تلحقه مشقة ولا تعب"),
    AsmaAllahItem(55, "الوَلِيُّ", "الناصر والمحب والمعين لعباده المؤمنين"),
    AsmaAllahItem(56, "الحَمِيدُ", "المستحق للحمد والثناء على كل حال"),
    AsmaAllahItem(57, "المُحْصِي", "الذي أحاط بكل شيء عدداً فلا يفوته شيء"),
    AsmaAllahItem(58, "المُبْدِئُ", "الذي بدأ خلق الكائنات وأنشأها أولاً"),
    AsmaAllahItem(59, "المُعِيدُ", "الذي يعيد الخلق بعد الموت والفناء"),
    AsmaAllahItem(60, "المُحْيِي", "الذي يحيي الأجساد ويبعث فيها الروح"),
    AsmaAllahItem(61, "المُمِيتُ", "الذي يسلب الحياة عن الأحياء بمشيئته"),
    AsmaAllahItem(62, "الحَيُّ", "الدائم الباقي المتصف بالحياة الكاملة الأزلية"),
    AsmaAllahItem(63, "القَيُّومُ", "القائم بنفسه المقيم لكل ما سواه من الكائنات"),
    AsmaAllahItem(64, "الوَاجِدُ", "الذي لا يعوزه شيء ولا يفوته مطلوب"),
    AsmaAllahItem(65, "المَاجِدُ", "الكامل في الشرف والمجد الواسع الفضل"),
    AsmaAllahItem(66, "الوَاحِدُ", "الفرد الذي لا شريك له في ذاته وصفاته وأفعاله"),
    AsmaAllahItem(67, "الأَحَدُ", "المنفرد بالألوهية الذي ليس كمثله شيء"),
    AsmaAllahItem(68, "الصَّمَدُ", "السيد المقصود في الحوائج الذي تصمد إليه الخلائق"),
    AsmaAllahItem(69, "القَادِرُ", "المتمكن من فعل كل ما يشاء بحكمته"),
    AsmaAllahItem(70, "المُقْتَدِرُ", "التام الاقتدار الذي لا يمتنع عليه شيء"),
    AsmaAllahItem(71, "المُقَدِّمُ", "الذي يقدم الأشياء ويضعها في مراتبها"),
    AsmaAllahItem(72, "المُؤَخِّرُ", "الذي يؤخر ما يشاء لحكمة بالغة"),
    AsmaAllahItem(73, "الأَوَّلُ", "الذي ليس قبله شيء وهو الأزلي بلا ابتداء"),
    AsmaAllahItem(74, "الآخِرُ", "الذي ليس بعده شيء وهو الباقي بلا انتهاء"),
    AsmaAllahItem(75, "الظَّاهِرُ", "العالي فوق كل شيء بالغلبة وبراهين وجوده"),
    AsmaAllahItem(76, "البَاطِنُ", "الذي احتجب عن الأبصار والعالم بالسرائر"),
    AsmaAllahItem(77, "الوَالِي", "المتولي لأمور خلقه القائم بتدبير ملكه"),
    AsmaAllahItem(78, "المُتَعَالِي", "المتنزه عن صفات المخلوقين ونقائصهم"),
    AsmaAllahItem(79, "البَرُّ", "العطوف المحسن على خلقه الواسع الإحسان"),
    AsmaAllahItem(80, "التَّوَّابُ", "الذي يقبل توبة التائبين ويغفر الذنوب"),
    AsmaAllahItem(81, "المُنْتَقِمُ", "المعاقب للعصاة والظالمين بعد الإعذار والإنذار"),
    AsmaAllahItem(82, "العَفُوُّ", "الممحو للذنوب المتجاوز عن السيئات فضلاً"),
    AsmaAllahItem(83, "الرَّؤُوفُ", "الشديد الرحمة والرأفة بعباده"),
    AsmaAllahItem(84, "مَالِكُ المُلْكِ", "المتصرف في الكون كيف يشاء يؤتي الملك من يشاء"),
    AsmaAllahItem(85, "ذُو الجَلَالِ وَالإِكْرَامِ", "المستحق للتعظيم والإجلال الواسع الكرم"),
    AsmaAllahItem(86, "المُقْسِطُ", "العادل في حكمه المنصف للمظلوم من الظالم"),
    AsmaAllahItem(87, "الجَامِعُ", "الجامع للخلائق يوم القيامة للحساب"),
    AsmaAllahItem(88, "الغَنِيُّ", "المستغني عن كل خلقه والكل مفتقر إليه"),
    AsmaAllahItem(89, "المُغْنِي", "الذي يغني من يشاء من خلقه بكرمه"),
    AsmaAllahItem(90, "المَانِعُ", "الذي يمنع البلاء عمن يشاء أو يحرم بحكمته"),
    AsmaAllahItem(91, "الضَّارُّ", "الذي ينزل الضر بمن يشاء بحكمته وعدله"),
    AsmaAllahItem(92, "النَّافِعُ", "الذي يوصل النفع والخير لمن يشاء"),
    AsmaAllahItem(93, "النُّورُ", "الذي أضاء السموات والأرض وهدى قلوب المؤمنين"),
    AsmaAllahItem(94, "الهَادِي", "المرشد لخلقه إلى مصالحهم وهادي القلوب للإيمان"),
    AsmaAllahItem(95, "البَدِيعُ", "الذي أبدع الكون وخلقه على غير مثال سابق"),
    AsmaAllahItem(96, "البَاقِي", "الدائم الوجود الذي لا يفنى ولا يزول"),
    AsmaAllahItem(97, "الوَارِثُ", "الباقي بعد فناء خلقه الذي يرث الأرض ومن عليها"),
    AsmaAllahItem(98, "الرَّشِيدُ", "الذي يرشد خلقه بحكمته ودبر الأمور بحسن تقديره"),
    AsmaAllahItem(99, "الصَّبُورُ", "الذي لا يعاجل بالعقوبة بل يصبر على عباده")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsmaAllahDialog(
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredNames = remember(searchQuery) {
        if (searchQuery.isBlank()) ASMA_ALLAH_LIST
        else ASMA_ALLAH_LIST.filter { it.name.contains(searchQuery.trim()) || it.meaning.contains(searchQuery.trim()) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Names of Allah",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "أسماء الله الحسنى ومعانيها",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_asma_allah_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث في أسماء الله الحسنى...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_asma_allah_input")
                )

                Text(
                    text = "قال رسول الله ﷺ: «إِنَّ لِلَّهِ تِسْعَةً وَتِسْعِينَ اسْمًا، مِائَةً إِلَّا وَاحِدًا، مَنْ أَحْصَاهَا دَخَلَ الجَنَّةَ»",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNames) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.meaning,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
