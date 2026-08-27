package com.example.data.local

import com.example.data.model.QuranIndexItem

object OfflineQuranIndexData {

    fun getInitialIndexes(): List<QuranIndexItem> {
        val list = mutableListOf<QuranIndexItem>()

        // 1. 30 Ajza' (الأجزاء)
        val ajzaa = listOf(
            Triple(1, "الجزء الأول (الم)", "من سورة الفاتحة 1 إلى سورة البقرة 141") to (1 to 21),
            Triple(2, "الجزء الثاني (سيقول السفهاء)", "من سورة البقرة 142 إلى سورة البقرة 252") to (22 to 41),
            Triple(3, "الجزء الثالث (تلك الرسل)", "من سورة البقرة 253 إلى سورة آل عمران 92") to (42 to 61),
            Triple(4, "الجزء الرابع (لن تنالوا البر)", "من سورة آل عمران 93 إلى سورة النساء 23") to (62 to 81),
            Triple(5, "الجزء الخامس (والمحصنات)", "من سورة النساء 24 إلى سورة النساء 147") to (82 to 101),
            Triple(6, "الجزء السادس (لا يحب الله)", "من سورة النساء 148 إلى سورة المائدة 81") to (102 to 121),
            Triple(7, "الجزء السابع (وإذا سمعوا)", "من سورة المائدة 82 إلى سورة الأنعام 110") to (122 to 141),
            Triple(8, "الجزء الثامن (ولو أننا نزلنا)", "من سورة الأنعام 111 إلى سورة الأعراف 87") to (142 to 161),
            Triple(9, "الجزء التاسع (قال الملأ)", "من سورة الأعراف 88 إلى سورة الأنفال 40") to (162 to 181),
            Triple(10, "الجزء العاشر (واعلموا)", "من سورة الأنفال 41 إلى سورة التوبة 92") to (182 to 201),
            Triple(11, "الجزء الحادي عشر (يعتذرون إليكم)", "من سورة التوبة 93 إلى سورة هود 5") to (202 to 221),
            Triple(12, "الجزء الثاني عشر (وما من دابة)", "من سورة هود 6 إلى سورة يوسف 52") to (222 to 241),
            Triple(13, "الجزء الثالث عشر (وما أبرئ نفسي)", "من سورة يوسف 53 إلى سورة إبراهيم 52") to (242 to 261),
            Triple(14, "الجزء الرابع عشر (ربما يود)", "من سورة الحجر 1 إلى سورة النحل 128") to (262 to 281),
            Triple(15, "الجزء الخامس عشر (سبحان الذي أسرى)", "من سورة الإسراء 1 إلى سورة الكهف 74") to (282 to 301),
            Triple(16, "الجزء السادس عشر (قال ألم أقل لك)", "من سورة الكهف 75 إلى سورة طه 135") to (302 to 321),
            Triple(17, "الجزء السابع عشر (اقترب للناس)", "من سورة الأنبياء 1 إلى سورة الحج 78") to (322 to 341),
            Triple(18, "الجزء الثامن عشر (قد أفلح المؤمنون)", "من سورة المؤمنون 1 إلى سورة الفرقان 20") to (342 to 361),
            Triple(19, "الجزء التاسع عشر (وقال الذين لا يرجون)", "من سورة الفرقان 21 إلى سورة النمل 55") to (362 to 381),
            Triple(20, "الجزء العشرون (فما كان جواب قومه)", "من سورة النمل 56 إلى سورة العنكبوت 45") to (382 to 401),
            Triple(21, "الجزء الحادي والعشرون (اتل ما أوحي)", "من سورة العنكبوت 46 إلى سورة الأحزاب 30") to (402 to 421),
            Triple(22, "الجزء الثاني والعشرون (ومن يقنت منكن)", "من سورة الأحزاب 31 إلى سورة يس 27") to (422 to 441),
            Triple(23, "الجزء الثالث والعشرون (وما أنزلنا على قومه)", "من سورة يس 28 إلى سورة الزمر 31") to (442 to 461),
            Triple(24, "الجزء الرابع والعشرون (فمن أظلم)", "من سورة الزمر 32 إلى سورة فصلت 46") to (462 to 481),
            Triple(25, "الجزء الخامس والعشرون (إليه يرد علم الساعة)", "من سورة فصلت 47 إلى سورة الجاثية 37") to (482 to 501),
            Triple(26, "الجزء السادس والعشرون (حم الأحقاف)", "من سورة الأحقاف 1 إلى سورة الذاريات 30") to (502 to 521),
            Triple(27, "الجزء السابع والعشرون (قال فما خطبكم)", "من سورة الذاريات 31 إلى سورة الحديد 29") to (522 to 541),
            Triple(28, "الجزء الثامن والعشرون (قد سمع الله)", "من سورة المجادلة 1 إلى سورة التحريم 12") to (542 to 561),
            Triple(29, "الجزء التاسع والعشرون (تبارك الذي بيده الملك)", "من سورة الملك 1 إلى سورة المرسلات 50") to (562 to 581),
            Triple(30, "الجزء الثلاثون (عم يتساءلون)", "من سورة النبأ 1 إلى سورة الناس 6") to (582 to 604)
        )

        val surahStartMap = mapOf(
            1 to (1 to "الفاتحة"), 2 to (2 to "البقرة"), 3 to (2 to "البقرة"), 4 to (3 to "آل عمران"),
            5 to (4 to "النساء"), 6 to (4 to "النساء"), 7 to (5 to "المائدة"), 8 to (6 to "الأنعام"),
            9 to (7 to "الأعراف"), 10 to (8 to "الأنفال"), 11 to (9 to "التوبة"), 12 to (11 to "هود"),
            13 to (12 to "يوسف"), 14 to (15 to "الحجر"), 15 to (17 to "الإسراء"), 16 to (18 to "الكهف"),
            17 to (21 to "الأنبياء"), 18 to (23 to "المؤمنون"), 19 to (25 to "الفرقان"), 20 to (27 to "النمل"),
            21 to (29 to "العنكبوت"), 22 to (33 to "الأحزاب"), 23 to (36 to "يس"), 24 to (39 to "الزمر"),
            25 to (41 to "فصلت"), 26 to (46 to "الأحقاف"), 27 to (51 to "الذاريات"), 28 to (58 to "المجادلة"),
            29 to (67 to "الملك"), 30 to (78 to "النبأ")
        )

        for (item in ajzaa) {
            val num = item.first.first
            val title = item.first.second
            val subtitle = item.first.third
            val pStart = item.second.first
            val pEnd = item.second.second
            val sInfo = surahStartMap[num] ?: (1 to "الفاتحة")

            list.add(
                QuranIndexItem(
                    type = "juz",
                    itemNumber = num,
                    titleArabic = title,
                    subtitleArabic = subtitle,
                    startSurahNumber = sInfo.first,
                    startSurahName = sInfo.second,
                    startAyahNumber = 1,
                    startPage = pStart,
                    endPage = pEnd,
                    topicGroup = "الأجزاء",
                    description = "الجزء $num من القرآن الكريم يمتد من الصفحة $pStart إلى $pEnd",
                    keywords = "جزء $num $title $subtitle"
                )
            )
        }

        // 2. 60 Ahzab (الأحزاب)
        for (h in 1..60) {
            val jNum = ((h - 1) / 2) + 1
            val half = if (h % 2 != 0) "الأول" else "الثاني"
            val pStart = (h - 1) * 10 + 1
            val pEnd = minOf(h * 10, 604)
            val sInfo = surahStartMap[jNum] ?: (1 to "الفاتحة")

            list.add(
                QuranIndexItem(
                    type = "hizb",
                    itemNumber = h,
                    titleArabic = "الحزب $h",
                    subtitleArabic = "النصف $half من الجزء $jNum • صفحة $pStart",
                    startSurahNumber = sInfo.first,
                    startSurahName = sInfo.second,
                    startAyahNumber = 1,
                    startPage = pStart,
                    endPage = pEnd,
                    topicGroup = "الأحزاب",
                    description = "الحزب رقم $h يبدأ من الصفحة $pStart وينتهي عند الصفحة $pEnd",
                    keywords = "حزب $h احزاب القرآن"
                )
            )
        }

        // 3. Thematic Index (الفهرس الموضوعي للقرآن الكريم)
        val topics = listOf(
            QuranIndexItem(
                type = "thematic",
                itemNumber = 1,
                titleArabic = "التوحيد والإيمان بصفات الله",
                subtitleArabic = "آية الكرسي وسورة الإخلاص وأواخر البقرة والحشر",
                startSurahNumber = 2,
                startSurahName = "البقرة",
                startAyahNumber = 255,
                startPage = 42,
                endPage = 43,
                topicGroup = "عقيدة",
                description = "آيات إثبات وحدانية الله وعظمته وسعة علمه وسلطانه وأسمائه الحسنى",
                keywords = "توحيد ايمان عقيدة كرسي اخلاص صفات الله"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 2,
                titleArabic = "أركان الإسلام وأحكام الصلاة",
                subtitleArabic = "سورة البقرة، النساء، الإسراء، النور، المزمل",
                startSurahNumber = 2,
                startSurahName = "البقرة",
                startAyahNumber = 43,
                startPage = 7,
                endPage = 8,
                topicGroup = "عبادات",
                description = "الأمر بإقامة الصلاة، الخشوع، أوقات الصلاة، صلاة الجماعة وقصر الصلاة",
                keywords = "صلاة عبادة قيام ركوع سجود خشوع فرض"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 3,
                titleArabic = "الصيام وشهر رمضان المبارك",
                subtitleArabic = "سورة البقرة (الآيات 183 - 187)",
                startSurahNumber = 2,
                startSurahName = "البقرة",
                startAyahNumber = 183,
                startPage = 28,
                endPage = 29,
                topicGroup = "عبادات",
                description = "فرض الصيام، رخصة المريض والمسافر، ليلة القدر، ونزول القرآن في رمضان",
                keywords = "صيام رمضان فدية سحور افطار اعتكاف"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 4,
                titleArabic = "الزكاة والصدقات والإنفاق في سبيل الله",
                subtitleArabic = "سورة البقرة والتوبة ومصارف الزكاة الثمانية",
                startSurahNumber = 9,
                startSurahName = "التوبة",
                startAyahNumber = 60,
                startPage = 196,
                endPage = 197,
                topicGroup = "عبادات",
                description = "فضل الصدقة، مصارف الزكاة، التحذير من البخل ومنع الزكاة والرياء",
                keywords = "زكاة صدقة انفاق فقراء مساكين مال تجارة"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 5,
                titleArabic = "الحج والعمرة والبيت الحرام",
                subtitleArabic = "سورة البقرة والحج وآل عمران",
                startSurahNumber = 22,
                startSurahName = "الحج",
                startAyahNumber = 26,
                startPage = 335,
                endPage = 336,
                topicGroup = "عبادات",
                description = "مناسك الحج، الطواف والسعي، الوقوف بعرفة، الهدي وشعائر الله",
                keywords = "حج عمرة كعبة عرفة منى طواف سعي احرام"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 6,
                titleArabic = "قصة موسى عليه السلام وبني إسرائيل",
                subtitleArabic = "سورة البقرة، الأعراف، طه، القصص، الشعراء",
                startSurahNumber = 28,
                startSurahName = "القصص",
                startAyahNumber = 1,
                startPage = 385,
                endPage = 396,
                topicGroup = "قصص الأنبياء",
                description = "ولادة موسى، نجاته في التابوت، اللقاء مع فرعون، وخروج بني إسرائيل ومعجزات العصا واليد",
                keywords = "موسى فرعون بني اسرائيل هارون طور سيناء"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 7,
                titleArabic = "قصة يوسف عليه السلام كاملة",
                subtitleArabic = "سورة يوسف (أحسن القصص)",
                startSurahNumber = 12,
                startSurahName = "يوسف",
                startAyahNumber = 1,
                startPage = 235,
                endPage = 248,
                topicGroup = "قصص الأنبياء",
                description = "رؤيا يوسف، كيد الإخوة، الصبر في الجب والسجن، وحكم مصر وعفو يوسف عن إخوته",
                keywords = "يوسف يعقوب رؤيا صبر مصر عفو"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 8,
                titleArabic = "قصة إبراهيم خليل الرحمن وبناء الكعبة",
                subtitleArabic = "سورة البقرة، إبراهيم، مريم، الأنبياء، الصافات",
                startSurahNumber = 14,
                startSurahName = "إبراهيم",
                startAyahNumber = 35,
                startPage = 260,
                endPage = 261,
                topicGroup = "قصص الأنبياء",
                description = "دعوة إبراهيم للتوحيد، كسر الأصنام، النجاة من النار، وبناء البيت الحرام مع إسماعيل",
                keywords = "ابراهيم اسماعيل كعبة توحيد فداء خليل"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 9,
                titleArabic = "بر الوالدين وصلة الأرحام",
                subtitleArabic = "سورة الإسراء، لقمان، مريم، الأحقاف",
                startSurahNumber = 17,
                startSurahName = "الإسراء",
                startAyahNumber = 23,
                startPage = 284,
                endPage = 285,
                topicGroup = "أخلاق",
                description = "الوصية بالوالدين وخفض جناح الذل لهما والدعاء لهما بالرحمة والإحسان إليهما كباراً",
                keywords = "والدين بر ام اب احسان ارحام صلة"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 10,
                titleArabic = "الأخلاق والآداب والمعاملات الاجتماعية",
                subtitleArabic = "سورة الحجرات، النور، الفرقان (صفات عباد الرحمن)",
                startSurahNumber = 49,
                startSurahName = "الحجرات",
                startAyahNumber = 1,
                startPage = 515,
                endPage = 517,
                topicGroup = "أخلاق",
                description = "النهي عن الغيبة والنميمة والتجسس والسخرية والتنازع، والأمر بالإصلاح والأخوة والصدق",
                keywords = "اخلاق حجرات غيبة تجسس ستر امانة عدل"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 11,
                titleArabic = "اليوم الآخر، أهوال القيامة، وصفة الجنة والنار",
                subtitleArabic = "سورة الواقعة، الرحمن، الملك، النبأ، التكوير، الانفطار",
                startSurahNumber = 56,
                startSurahName = "الواقعة",
                startAyahNumber = 1,
                startPage = 534,
                endPage = 537,
                topicGroup = "يوم القيامة",
                description = "مشاهد البعث والحساب، أصناف الناس الثلاثة: السابقون وأصحاب اليمين وأصحاب الشمال، ونعيم الجنة",
                keywords = "قيامة يوم اخر جنة نار بعث حساب حشر واقعة"
            ),
            QuranIndexItem(
                type = "thematic",
                itemNumber = 12,
                titleArabic = "الأدعية القرآنية المباركة",
                subtitleArabic = "أواخر سورة البقرة، آل عمران، الأعراف، الفرقان، الأنبياء",
                startSurahNumber = 2,
                startSurahName = "البقرة",
                startAyahNumber = 286,
                startPage = 49,
                endPage = 49,
                topicGroup = "أدعية",
                description = "ربنا لا تؤاخذنا إن نسينا أو أخطأنا، ربنا آتنا في الدنيا حسنة، دعاء يونس، ودعاء زكريا وأيوب",
                keywords = "دعاء ادعية قرانية مغفرة رحمة هداية استجابة"
            )
        )

        list.addAll(topics)

        return list
    }
}
