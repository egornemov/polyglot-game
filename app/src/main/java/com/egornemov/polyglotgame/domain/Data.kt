package com.egornemov.polyglotgame.domain

object Data {

    val easyLanguages = listOf(
        "Catalan",
        "English",
        "French",
        "German",
        "Greek",
        "Italian",
        "Spanish",
        "Turkish",
    )

    val europeLanguages = listOf(
        "Albanian",
        "Armenian",
        "Azerbaijani",
        "Belarusian",
        "Bosnian",
        "Bulgarian",
        "Catalan",
        "Croatian",
        "Czech",
        "Danish",
        "Dutch",
        "English",
        "Estonian",
        "Finnish",
        "French",
        "Georgian",
        "German",
        "Greek",
        "Hungarian",
        "Icelandic",
        "Irish",
        "Italian",
        "Kazakh",
        "Latvian",
        "Lithuanian",
        "Luxembourgish",
        "Macedonian",
        "Maltese",
        "Moldovan",
        "Montenegrin",
        "Norwegian",
        "Polish",
        "Portuguese",
        "Romanian",
        "Russian",
        "Serbian",
        "Slovak",
        "Slovenian",
        "Spanish",
        "Swedish",
        "Turkish",
        "Ukrainian",
    )

    val slavicLanguages = listOf(
        "Belarusian",
        "Bosnian",
        "Bulgarian",
        "Croatian",
        "Czech",
        "Kashubian",
        "Macedonian",
        "Montenegrin",
        "Old Church Slavonic",
        "Polish",
        "Russian",
        "Serbian",
        "Slovak",
        "Slovenian",
        "Sorbian (Lower Sorbian and Upper Sorbian)",
        "Ukrainian",
        "Rusyn"
    ).filter { europeLanguages.contains(it) }

    val germanicLanguages = listOf(
        "Afrikaans",
        "Danish",
        "Dutch",
        "English",
        "Faroese",
        "German",
        "Icelandic",
        "Limburgish",
        "Low German",
        "Luxembourgish",
        "Norwegian",
        "Scots",
        "Swedish",
        "Yiddish"
    ).filter { europeLanguages.contains(it) }

    val romanceLanguages = listOf(
        "Aragonese",
        "Aromanian",
        "Asturian",
        "Catalan",
        "Corsican",
        "Emilian-Romagnol",
        "Extremaduran",
        "French",
        "Friulian",
        "Galician",
        "Gallo-Italic",
        "Italian",
        "Judeo-Italian",
        "Ladin",
        "Leonese",
        "Ligurian",
        "Lombard",
        "Mozarabic",
        "Neapolitan",
        "Occitan",
        "Picard",
        "Portuguese",
        "Romanian",
        "Romansh",
        "Sardinian",
        "Sicilian",
        "Spanish",
        "Venetian"
    ).filter { europeLanguages.contains(it) }

}



