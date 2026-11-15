package com.example.data.repository

import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

data class QuranReciter(
    val id: String,
    val displayName: String,
    val styleLabel: String
)

data class QuranSurahInfo(
    val number: Int,
    val arabicName: String,
    val turkishName: String,
    val latinName: String,
    val ayahCount: Int,
    val isMeccan: Boolean
)

data class QuranAyah(
    val globalNumber: Int,
    val numberInSurah: Int,
    val arabicText: String,
    val translationText: String,
    val audioUrl: String
)

data class QuranSurahDetail(
    val surah: QuranSurahInfo,
    val reciter: QuranReciter,
    val translationEdition: String,
    val ayahs: List<QuranAyah>,
    val fullSurahAudioUrl: String,
    val isOfflineFallback: Boolean = false
)

sealed interface QuranSurahUiState {
    data object Loading : QuranSurahUiState
    data class Success(val detail: QuranSurahDetail) : QuranSurahUiState
    data class Error(val message: String, val surahNumber: Int) : QuranSurahUiState
}

object QuranCatalog {
    val RECITERS: List<QuranReciter> = listOf(
        QuranReciter("ar.alafasy", "Mishary Rashid Alafasy", "Murattal"),
        QuranReciter("ar.husary", "Mahmoud Khalil Al-Husary", "Murattal"),
        QuranReciter("ar.minshawi", "Mohamed Siddiq El-Minshawi", "Murattal"),
        QuranReciter("ar.mahermuaiqly", "Maher Al Mueaqly", "Haremeyn"),
        QuranReciter("ar.abdulbasitmurattal", "Abdul Basit Abdus Samad", "Murattal")
    )

    fun translationEditionForLanguage(language: AppLanguage): String = when (language) {
        AppLanguage.TURKISH -> "tr.diyanet"
        AppLanguage.KURDISH_SORANI,
        AppLanguage.KURDISH_BADINI -> "ku.asan"
        AppLanguage.ENGLISH -> "en.sahih"
        AppLanguage.ARABIC -> "ar.muyassar"
    }

    fun fullSurahAudioUrl(reciterId: String, surahNumber: Int): String =
        "https://cdn.islamic.network/quran/audio-surah/128/$reciterId/$surahNumber.mp3"

    fun ayahAudioUrl(reciterId: String, globalAyahNumber: Int): String =
        "https://cdn.islamic.network/quran/audio/128/$reciterId/$globalAyahNumber.mp3"

    val SURAHS: List<QuranSurahInfo> = listOf(
        QuranSurahInfo(1, "الفاتحة", "Fâtiha", "Al-Fatihah", 7, true),
        QuranSurahInfo(2, "البقرة", "Bakara", "Al-Baqarah", 286, false),
        QuranSurahInfo(3, "آل عمران", "Âl-i İmrân", "Ali 'Imran", 200, false),
        QuranSurahInfo(4, "النساء", "Nisâ", "An-Nisa", 176, false),
        QuranSurahInfo(5, "المائدة", "Mâide", "Al-Ma'idah", 120, false),
        QuranSurahInfo(6, "الأنعام", "En'âm", "Al-An'am", 165, true),
        QuranSurahInfo(7, "الأعراف", "A'râf", "Al-A'raf", 206, true),
        QuranSurahInfo(8, "الأنفال", "Enfâl", "Al-Anfal", 75, false),
        QuranSurahInfo(9, "التوبة", "Tevbe", "At-Tawbah", 129, false),
        QuranSurahInfo(10, "يونس", "Yûnus", "Yunus", 109, true),
        QuranSurahInfo(11, "هود", "Hûd", "Hud", 123, true),
        QuranSurahInfo(12, "يوسف", "Yûsuf", "Yusuf", 111, true),
        QuranSurahInfo(13, "الرعد", "Ra'd", "Ar-Ra'd", 43, false),
        QuranSurahInfo(14, "إبراهيم", "İbrâhîm", "Ibrahim", 52, true),
        QuranSurahInfo(15, "الحجر", "Hicr", "Al-Hijr", 99, true),
        QuranSurahInfo(16, "النحل", "Nahl", "An-Nahl", 128, true),
        QuranSurahInfo(17, "الإسراء", "İsrâ", "Al-Isra", 111, true),
        QuranSurahInfo(18, "الكهف", "Kehf", "Al-Kahf", 110, true),
        QuranSurahInfo(19, "مريم", "Meryem", "Maryam", 98, true),
        QuranSurahInfo(20, "طه", "Tâhâ", "Taha", 135, true),
        QuranSurahInfo(21, "الأنبياء", "Enbiyâ", "Al-Anbya", 112, true),
        QuranSurahInfo(22, "الحج", "Hac", "Al-Hajj", 78, false),
        QuranSurahInfo(23, "المؤمنون", "Mü'minûn", "Al-Mu'minun", 118, true),
        QuranSurahInfo(24, "النور", "Nûr", "An-Nur", 64, false),
        QuranSurahInfo(25, "الفرقان", "Furkân", "Al-Furqan", 77, true),
        QuranSurahInfo(26, "الشعراء", "Şuarâ", "Ash-Shu'ara", 227, true),
        QuranSurahInfo(27, "النمل", "Neml", "An-Naml", 93, true),
        QuranSurahInfo(28, "القصص", "Kasas", "Al-Qasas", 88, true),
        QuranSurahInfo(29, "العنكبوت", "Ankebût", "Al-'Ankabut", 69, true),
        QuranSurahInfo(30, "الروم", "Rûm", "Ar-Rum", 60, true),
        QuranSurahInfo(31, "لقمان", "Lokmân", "Luqman", 34, true),
        QuranSurahInfo(32, "السجدة", "Secde", "As-Sajdah", 30, true),
        QuranSurahInfo(33, "الأحزاب", "Ahzâb", "Al-Ahzab", 73, false),
        QuranSurahInfo(34, "سبإ", "Sebe'", "Saba", 54, true),
        QuranSurahInfo(35, "فاطر", "Fâtır", "Fatir", 45, true),
        QuranSurahInfo(36, "يس", "Yâsîn", "Ya-Sin", 83, true),
        QuranSurahInfo(37, "الصافات", "Sâffât", "As-Saffat", 182, true),
        QuranSurahInfo(38, "ص", "Sâd", "Sad", 88, true),
        QuranSurahInfo(39, "الزمر", "Zümer", "Az-Zumar", 75, true),
        QuranSurahInfo(40, "غافر", "Mü'min (Gâfir)", "Ghafir", 85, true),
        QuranSurahInfo(41, "فصلت", "Fussilet", "Fussilat", 54, true),
        QuranSurahInfo(42, "الشورى", "Şûrâ", "Ash-Shuraa", 53, true),
        QuranSurahInfo(43, "الزخرف", "Zuhruf", "Az-Zukhruf", 89, true),
        QuranSurahInfo(44, "الدخان", "Duhân", "Ad-Dukhan", 59, true),
        QuranSurahInfo(45, "الجاثية", "Câsiye", "Al-Jathiyah", 37, true),
        QuranSurahInfo(46, "الأحقاف", "Ahkâf", "Al-Ahqaf", 35, true),
        QuranSurahInfo(47, "محمد", "Muhammed", "Muhammad", 38, false),
        QuranSurahInfo(48, "الفتح", "Fetih", "Al-Fath", 29, false),
        QuranSurahInfo(49, "الحجرات", "Hucurât", "Al-Hujurat", 18, false),
        QuranSurahInfo(50, "ق", "Kâf", "Qaf", 45, true),
        QuranSurahInfo(51, "الذاريات", "Zâriyât", "Adh-Dhariyat", 60, true),
        QuranSurahInfo(52, "الطور", "Tûr", "At-Tur", 49, true),
        QuranSurahInfo(53, "النجم", "Necm", "An-Najm", 62, true),
        QuranSurahInfo(54, "القمر", "Kamer", "Al-Qamar", 55, true),
        QuranSurahInfo(55, "الرحمن", "Rahmân", "Ar-Rahman", 78, false),
        QuranSurahInfo(56, "الواقعة", "Vâkıa", "Al-Waqi'ah", 96, true),
        QuranSurahInfo(57, "الحديد", "Hadîd", "Al-Hadid", 29, false),
        QuranSurahInfo(58, "المجادلة", "Mücâdele", "Al-Mujadila", 22, false),
        QuranSurahInfo(59, "الحشر", "Haşr", "Al-Hashr", 24, false),
        QuranSurahInfo(60, "الممتحنة", "Mümtehine", "Al-Mumtahanah", 13, false),
        QuranSurahInfo(61, "الصف", "Saf", "As-Saf", 14, false),
        QuranSurahInfo(62, "الجمعة", "Cum'a", "Al-Jumu'ah", 11, false),
        QuranSurahInfo(63, "المنافقون", "Münâfikûn", "Al-Munafiqun", 11, false),
        QuranSurahInfo(64, "التغابن", "Tegâbün", "At-Taghabun", 18, false),
        QuranSurahInfo(65, "الطلاق", "Talâk", "At-Talaq", 12, false),
        QuranSurahInfo(66, "التحريم", "Tahrîm", "At-Tahrim", 12, false),
        QuranSurahInfo(67, "الملك", "Mülk", "Al-Mulk", 30, true),
        QuranSurahInfo(68, "القلم", "Kalem", "Al-Qalam", 52, true),
        QuranSurahInfo(69, "الحاقة", "Hâkka", "Al-Haqqah", 52, true),
        QuranSurahInfo(70, "المعارج", "Meâric", "Al-Ma'arij", 44, true),
        QuranSurahInfo(71, "نوح", "Nûh", "Nuh", 28, true),
        QuranSurahInfo(72, "الجن", "Cin", "Al-Jinn", 28, true),
        QuranSurahInfo(73, "المزمل", "Müzzemmil", "Al-Muzzammil", 20, true),
        QuranSurahInfo(74, "المدثر", "Müddessir", "Al-Muddaththir", 56, true),
        QuranSurahInfo(75, "القيامة", "Kıyâmet", "Al-Qiyamah", 40, true),
        QuranSurahInfo(76, "الإنسان", "İnsân", "Al-Insan", 31, false),
        QuranSurahInfo(77, "المرسلات", "Mürselât", "Al-Mursalat", 50, true),
        QuranSurahInfo(78, "النبإ", "Nebe'", "An-Naba", 40, true),
        QuranSurahInfo(79, "النازعات", "Nâziât", "An-Nazi'at", 46, true),
        QuranSurahInfo(80, "عبس", "Abese", "Abasa", 42, true),
        QuranSurahInfo(81, "التكوير", "Tekvîr", "At-Takwir", 29, true),
        QuranSurahInfo(82, "الإنفطار", "İnfitâr", "Al-Infitar", 19, true),
        QuranSurahInfo(83, "المطففين", "Mutaffifîn", "Al-Mutaffifin", 36, true),
        QuranSurahInfo(84, "الإنشقاق", "İnşikâk", "Al-Inshiqaq", 25, true),
        QuranSurahInfo(85, "البروج", "Burûc", "Al-Buruj", 22, true),
        QuranSurahInfo(86, "الطارق", "Târık", "At-Tariq", 17, true),
        QuranSurahInfo(87, "الأعلى", "A'lâ", "Al-A'la", 19, true),
        QuranSurahInfo(88, "الغاشية", "Gâşiye", "Al-Ghashiyah", 26, true),
        QuranSurahInfo(89, "الفجر", "Fecr", "Al-Fajr", 30, true),
        QuranSurahInfo(90, "البلد", "Beled", "Al-Balad", 20, true),
        QuranSurahInfo(91, "الشمس", "Şems", "Ash-Shams", 15, true),
        QuranSurahInfo(92, "الليل", "Leyl", "Al-Layl", 21, true),
        QuranSurahInfo(93, "الضحى", "Duhâ", "Ad-Duhaa", 11, true),
        QuranSurahInfo(94, "الشرح", "İnşirâh", "Ash-Sharh", 8, true),
        QuranSurahInfo(95, "التين", "Tîn", "At-Tin", 8, true),
        QuranSurahInfo(96, "العلق", "Alak", "Al-'Alaq", 19, true),
        QuranSurahInfo(97, "القدر", "Kadir", "Al-Qadr", 5, true),
        QuranSurahInfo(98, "البينة", "Beyyine", "Al-Bayyinah", 8, false),
        QuranSurahInfo(99, "الزلزلة", "Zilzâl", "Az-Zalzalah", 8, false),
        QuranSurahInfo(100, "العاديات", "Âdiyât", "Al-'Adiyat", 11, true),
        QuranSurahInfo(101, "القارعة", "Kâria", "Al-Qari'ah", 11, true),
        QuranSurahInfo(102, "التكاثر", "Tekâsür", "At-Takathur", 8, true),
        QuranSurahInfo(103, "العصر", "Asr", "Al-'Asr", 3, true),
        QuranSurahInfo(104, "الهمزة", "Hümeze", "Al-Humazah", 9, true),
        QuranSurahInfo(105, "الفيل", "Fîl", "Al-Fil", 5, true),
        QuranSurahInfo(106, "قريش", "Kureyş", "Quraysh", 4, true),
        QuranSurahInfo(107, "الماعون", "Mâûn", "Al-Ma'un", 7, true),
        QuranSurahInfo(108, "الكوثر", "Kevser", "Al-Kawthar", 3, true),
        QuranSurahInfo(109, "الكافرون", "Kâfirûn", "Al-Kafirun", 6, true),
        QuranSurahInfo(110, "النصر", "Nasr", "An-Nasr", 3, false),
        QuranSurahInfo(111, "المسد", "Tebbet", "Al-Masad", 5, true),
        QuranSurahInfo(112, "الإخلاص", "İhlâs", "Al-Ikhlas", 4, true),
        QuranSurahInfo(113, "الفلق", "Felak", "Al-Falaq", 5, true),
        QuranSurahInfo(114, "الناس", "Nâs", "An-Nas", 6, true)
    )

    fun surahByNumber(number: Int): QuranSurahInfo =
        SURAHS.firstOrNull { it.number == number } ?: SURAHS.first()

    internal fun offlineFallbackFor(
        surahNumber: Int,
        reciter: QuranReciter,
        language: AppLanguage
    ): QuranSurahDetail? {
        val surah = SURAHS.firstOrNull { it.number == surahNumber } ?: return null
        val translationEdition = translationEditionForLanguage(language)
        val ayahs = when (surahNumber) {
            1 -> listOf(
                QuranAyah(
                    1, 1,
                    "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                    localizedLine(language, "Rahmân ve Rahîm olan Allah'ın adıyla.", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "بە ناوی خوای بەخشندەی میهرەبان.", "بسم الله الرحمن الرحيم"),
                    ayahAudioUrl(reciter.id, 1)
                ),
                QuranAyah(
                    2, 2,
                    "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
                    localizedLine(language, "Hamd, âlemlerin Rabbi Allah'a mahsustur.", "All praise is due to Allah, Lord of the worlds.", "هەموو سوپاس و ستایش بۆ خوای پەروەردگاری جیهانیان.", "الحمد لله رب العالمين"),
                    ayahAudioUrl(reciter.id, 2)
                ),
                QuranAyah(
                    3, 3,
                    "ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                    localizedLine(language, "O, Rahmân'dır ve Rahîm'dir.", "The Entirely Merciful, the Especially Merciful.", "بەخشندە و میهرەبانە.", "الرحمن الرحيم"),
                    ayahAudioUrl(reciter.id, 3)
                ),
                QuranAyah(
                    4, 4,
                    "مَٰلِكِ يَوْمِ ٱلدِّينِ",
                    localizedLine(language, "Din (hesap) gününün tek sahibidir.", "Sovereign of the Day of Recompense.", "خاوەنی ڕۆژی دوایی و لێپرسینەوەیە.", "مالك يوم الدين"),
                    ayahAudioUrl(reciter.id, 4)
                ),
                QuranAyah(
                    5, 5,
                    "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                    localizedLine(language, "(Rabbimiz!) Yalnız sana kulluk eder ve yalnız senden yardım dileriz.", "It is You we worship and You we ask for help.", "تەنها تۆ دەپەرستین و هەر لە تۆش داوای یارمەتی دەکەین.", "إياك نعبد وإياك نستعين"),
                    ayahAudioUrl(reciter.id, 5)
                ),
                QuranAyah(
                    6, 6,
                    "ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ",
                    localizedLine(language, "Bizi dosdoğru yola ilet.", "Guide us to the straight path.", "ڕێنموونیمان بکە بۆ ڕێگای ڕاست.", "اهدنا الصراط المستقيم"),
                    ayahAudioUrl(reciter.id, 6)
                ),
                QuranAyah(
                    7, 7,
                    "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ",
                    localizedLine(language, "Kendilerine nimet verdiklerinin yoluna; gazaba uğramışların ve sapmışların yoluna değil.", "The path of those upon whom You have bestowed favor, not of those who have evoked anger or of those who are astray.", "ڕێگای ئەوانەی ناز و نیعمەتت پێداون، نەک ئەوانەی غەزەبت لێ گرتوون یان گومڕا بوون.", "صراط الذين أنعمت عليهم غير المغضوب عليهم ولا الضالين"),
                    ayahAudioUrl(reciter.id, 7)
                )
            )
            112 -> listOf(
                QuranAyah(6222, 1, "قُلْ هُوَ ٱللَّهُ أَحَدٌ", localizedLine(language, "De ki: O, Allah'tır, bir tektir.", "Say, 'He is Allah, who is One,'", "بڵێ: ئەو خوایە تاک و تەنهایە.", "قل هو الله أحد"), ayahAudioUrl(reciter.id, 6222)),
                QuranAyah(6223, 2, "ٱللَّهُ ٱلصَّمَدُ", localizedLine(language, "Allah Samed'dir (her şey O'na muhtaçtır, O hiçbir şeye muhtaç değildir).", "Allah, the Eternal Refuge.", "خوا بێ نیازە و هەموو پێویستییان پێیەتی.", "الله الصمد"), ayahAudioUrl(reciter.id, 6223)),
                QuranAyah(6224, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", localizedLine(language, "Doğurmamıştır ve doğurulmamıştır.", "He neither begets nor is born,", "نە کەسی لێ بووە و نە لە کەس بووە.", "لم يلد ولم يولد"), ayahAudioUrl(reciter.id, 6224)),
                QuranAyah(6225, 4, "وَلَمْ يَكُن لَّهُۥ كُفُوًا أَحَدٌ", localizedLine(language, "Hiçbir şey O'na denk değildir.", "Nor is there to Him any equivalent.", "و هیچ شتێک هاوتا و هاوشێوەی نییە.", "ولم يكن له كفوا أحد"), ayahAudioUrl(reciter.id, 6225))
            )
            113 -> listOf(
                QuranAyah(6226, 1, "قُلْ أَعُوذُ بِرَبِّ ٱلْفَلَقِ", localizedLine(language, "De ki: Sabahın Rabbine sığınırım.", "Say, 'I seek refuge in the Lord of daybreak'", "بڵێ: پەنا دەگرم بە پەروەردگاری بەرەبەیان.", "قل أعوذ برب الفلق"), ayahAudioUrl(reciter.id, 6226)),
                QuranAyah(6227, 2, "مِن شَرِّ مَا خَلَقَ", localizedLine(language, "Yarattığı şeylerin şerrinden.", "From the evil of that which He created", "لە خراپەی هەرچی دروستی کردووە.", "من شر ما خلق"), ayahAudioUrl(reciter.id, 6227)),
                QuranAyah(6228, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", localizedLine(language, "Karanlığı çöktüğü zaman gecenin şerrinden.", "And from the evil of darkness when it settles", "وە لە خراپەی شەوگار کاتێک تاریکی دادێت.", "ومن شر غاسق إذا وقب"), ayahAudioUrl(reciter.id, 6228)),
                QuranAyah(6229, 4, "وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِى ٱلْعُقَدِ", localizedLine(language, "Düğümlere üfleyen büyücülerin şerrinden.", "And from the evil of the blowers in knots", "وە لە خراپەی ئەوانەی فوو لە گرێکان دەکەن.", "ومن شر النفاثات في العقد"), ayahAudioUrl(reciter.id, 6229)),
                QuranAyah(6230, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", localizedLine(language, "Ve haset ettiği zaman hasetçinin şerrinden.", "And from the evil of an envier when he envies.", "وە لە خراپەی حەسوود کاتێک حەسوودی دەبات.", "ومن شر حاسد إذا حسد"), ayahAudioUrl(reciter.id, 6230))
            )
            114 -> listOf(
                QuranAyah(6231, 1, "قُلْ أَعُوذُ بِرَبِّ ٱلنَّاسِ", localizedLine(language, "De ki: İnsanların Rabbine sığınırım.", "Say, 'I seek refuge in the Lord of mankind,'", "بڵێ: پەنا دەگرم بە پەروەردگاری خەڵکی.", "قل أعوذ برب الناس"), ayahAudioUrl(reciter.id, 6231)),
                QuranAyah(6232, 2, "مَلِكِ ٱلنَّاسِ", localizedLine(language, "İnsanların Melikine (hükümdarına).", "The Sovereign of mankind,", "پادشای خەڵکی.", "ملك الناس"), ayahAudioUrl(reciter.id, 6232)),
                QuranAyah(6233, 3, "إِلَٰهِ ٱلنَّاسِ", localizedLine(language, "İnsanların ilâhına.", "The God of mankind,", "پەرستراوی خەڵکی.", "إله الناس"), ayahAudioUrl(reciter.id, 6233)),
                QuranAyah(6234, 4, "مِن شَرِّ ٱلْوَسْوَاسِ ٱلْخَنَّاسِ", localizedLine(language, "O sinsi vesvesecinin şerrinden.", "From the evil of the retreating whisperer", "لە خراپەی وەسوەسەدەری خۆحەشاردەر.", "من شر الوسواس الخناس"), ayahAudioUrl(reciter.id, 6234)),
                QuranAyah(6235, 5, "ٱلَّذِى يُوَسْوِسُ فِى صُدُورِ ٱلنَّاسِ", localizedLine(language, "O ki insanların göğüslerine vesvese verir.", "Who whispers in the breasts of mankind", "ئەوەی وەسوەسە دەخاتە ناو سینەی خەڵکەوە.", "الذي يوسوس في صدور الناس"), ayahAudioUrl(reciter.id, 6235)),
                QuranAyah(6236, 6, "مِنَ ٱلْجِنَّةِ وَٱلنَّاسِ", localizedLine(language, "Gerek cinlerden gerek insanlardan.", "From among the jinn and mankind.", "لە جنۆکە و لە ئادەمیزاد.", "من الجنة والناس"), ayahAudioUrl(reciter.id, 6236))
            )
            else -> emptyList()
        }
        if (ayahs.isEmpty()) return null
        return QuranSurahDetail(
            surah = surah,
            reciter = reciter,
            translationEdition = translationEdition,
            ayahs = ayahs,
            fullSurahAudioUrl = fullSurahAudioUrl(reciter.id, surahNumber),
            isOfflineFallback = true
        )
    }

    private fun localizedLine(
        language: AppLanguage,
        tr: String,
        en: String,
        ku: String,
        ar: String
    ): String = when (language) {
        AppLanguage.TURKISH -> tr
        AppLanguage.ENGLISH -> en
        AppLanguage.KURDISH_SORANI,
        AppLanguage.KURDISH_BADINI -> ku
        AppLanguage.ARABIC -> ar
    }
}

object QuranApiParser {
    fun parseSurahEditionsResponse(
        rawJson: String,
        surahNumber: Int,
        reciter: QuranReciter,
        translationEdition: String
    ): QuranSurahDetail {
        val root = JSONObject(rawJson)
        val dataArray = root.optJSONArray("data")
            ?: throw IOException("Geçersiz Kur'an API yanıtı")
        if (dataArray.length() < 2) {
            throw IOException("Sure verisi eksik döndü")
        }

        val arabicEditionObj = dataArray.getJSONObject(0)
        val translationEditionObj = dataArray.getJSONObject(1)
        val audioEditionObj = if (dataArray.length() >= 3) dataArray.optJSONObject(2) else null

        val arabicAyahs = arabicEditionObj.getJSONArray("ayahs")
        val translationAyahs = translationEditionObj.getJSONArray("ayahs")
        val audioAyahs = audioEditionObj?.optJSONArray("ayahs")

        val ayahs = ArrayList<QuranAyah>(arabicAyahs.length())
        for (i in 0 until arabicAyahs.length()) {
            val arObj = arabicAyahs.getJSONObject(i)
            val trObj = translationAyahs.optJSONObject(i)
            val auObj = audioAyahs?.optJSONObject(i)

            val globalNum = arObj.optInt("number", 0)
            val numInSurah = arObj.optInt("numberInSurah", i + 1)
            val arabicText = arObj.optString("text", "")
            val translationText = trObj?.optString("text", "").orEmpty()
            val audioFromJson = auObj?.optString("audio", "").orEmpty()
            val resolvedAudioUrl = if (audioFromJson.startsWith("http")) {
                audioFromJson
            } else if (globalNum > 0) {
                QuranCatalog.ayahAudioUrl(reciter.id, globalNum)
            } else {
                ""
            }

            ayahs.add(
                QuranAyah(
                    globalNumber = globalNum,
                    numberInSurah = numInSurah,
                    arabicText = arabicText,
                    translationText = translationText,
                    audioUrl = resolvedAudioUrl
                )
            )
        }

        val surahInfo = QuranCatalog.surahByNumber(surahNumber)
        return QuranSurahDetail(
            surah = surahInfo,
            reciter = reciter,
            translationEdition = translationEdition,
            ayahs = ayahs,
            fullSurahAudioUrl = QuranCatalog.fullSurahAudioUrl(reciter.id, surahNumber),
            isOfflineFallback = false
        )
    }
}

class QuranRepository(
    private val okHttpClient: OkHttpClient
) {
    private val cache = LinkedHashMap<String, QuranSurahDetail>()

    suspend fun fetchSurah(
        surahNumber: Int,
        reciter: QuranReciter,
        language: AppLanguage
    ): QuranSurahDetail = withContext(Dispatchers.IO) {
        val translationEdition = QuranCatalog.translationEditionForLanguage(language)
        val cacheKey = "${surahNumber}_${translationEdition}_${reciter.id}"
        synchronized(cache) {
            cache[cacheKey]?.let { return@withContext it }
        }

        val apiUrl =
            "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,$translationEdition,${reciter.id}"
        val request = Request.Builder()
            .url(apiUrl)
            .header("User-Agent", "JheHakarTV/1.0 (Android)")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP ${response.code}")
                }
                val body = response.body?.string().orEmpty()
                val parsed = QuranApiParser.parseSurahEditionsResponse(
                    rawJson = body,
                    surahNumber = surahNumber,
                    reciter = reciter,
                    translationEdition = translationEdition
                )
                synchronized(cache) {
                    if (cache.size >= 20) {
                        val oldestKey = cache.keys.firstOrNull()
                        if (oldestKey != null) cache.remove(oldestKey)
                    }
                    cache[cacheKey] = parsed
                }
                return@withContext parsed
            }
        } catch (e: Exception) {
            val fallback = QuranCatalog.offlineFallbackFor(surahNumber, reciter, language)
            if (fallback != null) {
                return@withContext fallback
            }
            throw e
        }
    }
}
