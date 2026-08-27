package com.example.data.local

import com.example.data.model.AthkarCategory
import com.example.data.model.AthkarItem

object OfflineAthkarData {

    fun getInitialAthkar(): List<AthkarItem> {
        val list = mutableListOf<AthkarItem>()

        // 1. Morning Athkar
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ.",
            countTarget = 1,
            virtue = "من قالها حين يصبح حماه الله ووهبه خير اليوم",
            reference = "رواه مسلم"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
            countTarget = 1,
            virtue = "سيد الاستغفار: من قالها موقنا بها فمات من يومه دخل الجنة",
            reference = "رواه البخاري"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
            countTarget = 3,
            virtue = "لم يضره شيء حتى يمسي",
            reference = "رواه الترمذي وأبو داود"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا.",
            countTarget = 3,
            virtue = "كان حقاً على الله أن يرضيه يوم القيامة",
            reference = "رواه الترمذي وأحمد"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.",
            countTarget = 1,
            virtue = "صلاح الأمر كله والاعتصام بالله",
            reference = "رواه الحاكم وحسنه الألباني"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.",
            countTarget = 3,
            virtue = "تعدل ساعات طويلة من الذكر والتسبيح",
            reference = "رواه مسلم"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ.",
            countTarget = 100,
            virtue = "حُطّت خطاياه وإن كانت مثل زبد البحر",
            reference = "متفق عليه"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.MORNING.id,
            textArabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ.",
            countTarget = 10,
            virtue = "من صلى عليّ عشراً أدركته شفاعتي يوم القيامة",
            reference = "رواه الطبراني وحسنه الألباني"
        ))

        // 2. Evening Athkar
        list.add(AthkarItem(
            categoryId = AthkarCategory.EVENING.id,
            textArabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا.",
            countTarget = 1,
            virtue = "حفظ المسلم حتى يصبح وبركة الليلة",
            reference = "رواه مسلم"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.EVENING.id,
            textArabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
            countTarget = 3,
            virtue = "لم يضره شيء في تلك الليلة ولا يمسه سم أو لدغة",
            reference = "رواه مسلم"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.EVENING.id,
            textArabic = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ.",
            countTarget = 1,
            virtue = "تسليم الأمر لله والاستسلام لرحمته",
            reference = "رواه الترمذي"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.EVENING.id,
            textArabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ.",
            countTarget = 3,
            virtue = "تفريج الهموم والديون وراحة البال",
            reference = "رواه البخاري"
        ))

        // 3. After Prayer Athkar
        list.add(AthkarItem(
            categoryId = AthkarCategory.AFTER_PRAYER.id,
            textArabic = "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ.",
            countTarget = 1,
            virtue = "استغفار بعد كل فريضة",
            reference = "رواه مسلم"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.AFTER_PRAYER.id,
            textArabic = "سُبْحَانَ اللَّهِ",
            countTarget = 33,
            virtue = "تسبيح دبر كل صلاة",
            reference = "متفق عليه"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.AFTER_PRAYER.id,
            textArabic = "الْحَمْدُ لِلَّهِ",
            countTarget = 33,
            virtue = "تحميد دبر كل صلاة",
            reference = "متفق عليه"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.AFTER_PRAYER.id,
            textArabic = "اللَّهُ أَكْبَرُ",
            countTarget = 33,
            virtue = "تكبير دبر كل صلاة",
            reference = "متفق عليه"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.AFTER_PRAYER.id,
            textArabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
            countTarget = 1,
            virtue = "تمام المائة: غفرت خطاياه وإن كانت مثل زبد البحر",
            reference = "رواه مسلم"
        ))

        // 4. Sleep Athkar
        list.add(AthkarItem(
            categoryId = AthkarCategory.SLEEP.id,
            textArabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، إِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ.",
            countTarget = 1,
            virtue = "حفظ النفس والروح عند النوم",
            reference = "متفق عليه"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.SLEEP.id,
            textArabic = "اللَّهُمَّ إِنَّكَ خَلَقْتَ نَفْسِي وَأَنْتَ تَوَفَّاهَا، لَكَ مَمَاتُهَا وَمَحْيَاهَا، إِنْ أَحْيَيْتَهَا فَاحْفَظْهَا، وَإِنْ أَمَتَّهَا فَاغْفِرْ لَهَا، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَافِيَةَ.",
            countTarget = 1,
            virtue = "دعاء النوم وحفظ العافية",
            reference = "رواه مسلم"
        ))

        // 5. Wakeup Athkar
        list.add(AthkarItem(
            categoryId = AthkarCategory.WAKEUP.id,
            textArabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ.",
            countTarget = 1,
            virtue = "شكر الله على نعمة الحياة بعد النوم",
            reference = "رواه البخاري"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.WAKEUP.id,
            textArabic = "الْحَمْدُ لِلَّهِ الَّذِي عَافَانِي فِي جَسَدِي، وَرَدَّ عَلَيَّ رُوحِي، وَأَذِنَ لِي بِذِكْرِهِ.",
            countTarget = 1,
            virtue = "شكر نعمة العافية وحفظ الروح والذكر",
            reference = "رواه الترمذي"
        ))

        // 6. Quran Duas (أدعية قرآنية)
        list.add(AthkarItem(
            categoryId = AthkarCategory.QURAN_DUAS.id,
            textArabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ.",
            countTarget = 3,
            virtue = "أكثر دعاء كان يدعو به النبي ﷺ ويجمع خيري الدنيا والآخرة",
            reference = "سورة البقرة: 201"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.QURAN_DUAS.id,
            textArabic = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ.",
            countTarget = 3,
            virtue = "دعاء الثبات على دين الله والهداية للحق",
            reference = "سورة آل عمران: 8"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.QURAN_DUAS.id,
            textArabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي يَفْقَهُوا قَوْلِي.",
            countTarget = 3,
            virtue = "دعاء موسى عليه السلام لتيسير الأمور وانشراح الصدر",
            reference = "سورة طه: 25-28"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.QURAN_DUAS.id,
            textArabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ.",
            countTarget = 3,
            virtue = "دعاء ذي النون: لم يدعُ به مكروب أو مكروب إلا فرج الله عنه",
            reference = "سورة الأنبياء: 87"
        ))
        list.add(AthkarItem(
            categoryId = AthkarCategory.QURAN_DUAS.id,
            textArabic = "رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ.",
            countTarget = 3,
            virtue = "دعاء إبراهيم عليه السلام لطلب المغفرة للوالدين ولعموم المؤمنين",
            reference = "سورة إبراهيم: 41"
        ))

        return list
    }
}
