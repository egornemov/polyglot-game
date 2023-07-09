package com.egornemov.polyglotgame.domain

data class Track(
    val url: String,
    val choices: List<String>,
    val answer: String,
)