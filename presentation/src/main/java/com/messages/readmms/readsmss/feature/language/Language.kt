package com.messages.readmms.readsmss.feature.language

enum class Language(
    val languageName: String,
    val code: String,
    val flag: String,
    val translationText: String? = null
) { //bn,
    ENGLISH(
        languageName = "English",
        code = "en",
        flag = "flag_en",
        translationText = "English"
    ),
    HINDI(
        languageName = "Hindi",
        code = "hi",
        flag = "flag_hi",
        "हिंदी"
    ),
    ARABIC(
        languageName = "Arabic",
        code = "ar",
        flag = "flag_ar",
        translationText = "العربية"
    ),
//    BENGALI(
//        languageName = "Bengali", code = "bn", flag = "ic_bn", translationText = "বাংলা"
//    ), //flag check
    CZECH(languageName = "Czech", code = "cs", flag = "flag_cs", translationText = "Čeština"),
    DANISH(
        languageName = "Danish",
        code = "da",
        flag = "flag_da",
        translationText = "Dansk"
    ),
    GERMAN(
        languageName = "German",
        code = "de",
        flag = "flag_de",
        translationText = "Deutsch"
    ),
//    GREEK(
//        languageName = "Greek",
//        code = "el",
//        flag = "ic_el",
//        translationText = "Ελληνικά"
//    ),
    SPANISH(
        languageName = "Spanish",
        code = "es",
        flag = "flag_es",
        translationText = "Español"
    ),
//    FARSI(languageName = "Farsi", code = "fa", flag = "ic_fa", translationText = ""), FINNISH(
//        languageName = "Finnish",
//        code = "fi",
//        flag = "ic_fi",
//        translationText = "Suomi"
//    ),
    FRENCH(
        languageName = "French",
        code = "fr",
        flag = "flag_fr",
        translationText = "Français"
    ),
//    SCOTTISH_GAELIC(
//        languageName = "Scottish Gaelic", code = "gd", flag = "ic_gd", translationText = ""
//    ),
//    CROATIAN(
//        languageName = "Croatian",
//        code = "hr",
//        flag = "ic_hr",
//        translationText = "Hrvatski"
//    ),
//    HUNGARIAN(
//        languageName = "Hungarian",
//        code = "hu",
//        flag = "ic_hu",
//        translationText = "Magyar"
//    ),
//    IN(languageName = "in", code = "in", flag = "in", translationText = ""),//language check
    ITALIAN(
        languageName = "Italian",
        code = "it",
        flag = "flag_it",
        translationText = "Italiano"
    ),
//    HEBREW(languageName = "Hebrew", code = "iw", flag = "ic_iw", translationText = ""),
    JAPANESE(
        languageName = "Japanese",
        code = "ja",
        flag = "flag_ja",
        translationText = "日本語"
    ),
//    KANNADA(
//        languageName = "Kannada",
//        code = "kn",
//    ),
    KOREAN(languageName = "Korean", code = "ko", flag = "flag_ko", translationText = "한국어"),
//    IT(
//        languageName = "IT",
//        code = "IT",
//        flag = "IT"
//    ),//language check
//    NORWEGIAN_BOKMAL(languageName = "Norwegian Bokmål", code = "nb", flag = "ic_nb"),
//    NEPALI(
//        languageName = "Nepali",
//        code = "ne",
//        flag = "ic_ne"
//    ),
    DUTCH(languageName = "Dutch", code = "nl", flag = "flag_nl", translationText = "Nederlands"),
    POLISH(
        languageName = "Polish",
        code = "pl",
        flag = "flag_pl",
        translationText = "Polski"
    ),
    PORTUGUESE(languageName = "Portuguese", code = "pt", flag = "flag_pt", translationText = "Português"),
//    PORTUGUESE_BRAZILIAN(
//        languageName = "Portuguese (Brazil)",
//        code = "pt-rBR",
//        flag = "ic_pt_br"
//    ),
//    ROMANIAN(
//        languageName = "Romanian",
//        code = "ro",
//        flag = "ic_ro"
//    ),
    RUSSIAN(languageName = "Russian", code = "ru", flag = "flag_ru", translationText = "Русский"),
//    SLOVAK(
//        languageName = "Slovak",
//        code = "sk",
//        flag = "flag_sv"
//    ),
//    SLAVIC(languageName = "Slavic", code = "sl", flag = "ic_sl"),
//    SERBIAN(
//        languageName = "Serbian",
//        code = "sr",
//        flag = "ic_sr"
//    ),
    SWEDISH(languageName = "Swedish", code = "sv", flag = "flag_sv", translationText = "Svenska"),
//    TAMIL(
//        languageName = "Tamil",
//        code = "ta",
//        flag = "ic_hi"
//    ), //--------
//    TAGALOG(
//        languageName = "Tagalog",
//        code = "tl",
//        flag = "ic_tl"
//    ),
    TURKISH(
        languageName = "Turkish",
        code = "tr",
        flag = "flag_tr",
        translationText = "Türkçe"
    ),
    UKRAINIAN(
        languageName = "Ukrainian",
        code = "uk",
        flag = "flag_uk",
        translationText = "Українська"
    ),
    VIETNAMESE(
        languageName = "Vietnamese",
        code = "vi",
        flag = "flag_vi",
        translationText = "Tiếng Việt"
    ),
//    CHINESE_PRC(
//        languageName = "Chinese(China)",
//        code = "zh-rCN",
//        flag = "ic_zn_cn"
//    ),
//    CHINESE_HONG_KONG(
//        languageName = "Chinese(HongKong)",
//        code = "zh-rHK",
//        flag = "ic_zn_hk"
//    ),
//    CHINESE_TAIWAN(languageName = "Chinese(Taiwan)", code = "zh-rTW", flag = "ic_zn_tw")
}