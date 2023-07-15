package com.egornemov.polyglotgame.di

import android.content.Context
import android.media.MediaPlayer
import com.egornemov.polyglotgame.domain.Data
import com.egornemov.polyglotgame.domain.Track
import com.egornemov.polyglotgame.presenter.coordinator.MainCoordinator
import kotlin.random.Random

class ServiceLocator {
    val mainCoordinator = MainCoordinator()

    lateinit var flattenedList: List<Pair<String, String>>

    lateinit var mediaPlayers: List<MediaPlayer>
    lateinit var data: List<Track>

    val easyData: List<Track>
        get() {
            val languages = Data.easyLanguageMap.keys.toList()
            val flattenedList = Data.easyLanguageMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = Data.easyLanguageMap.keys.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = Data.easyLanguageMap.keys.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6)

                (targetLanguages + noiseLanguages).forEach { l ->
                    val sample = flattenedList.filter { it.first == l }.shuffled().first()

                    val answer = sample.first
                    val url = sample.second
                    val track = Track(
                        url,
                        languages,
                        answer,
                    )
                    add(track)
                }
            }.shuffled()

            mediaPlayers = tracks.map {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(it.url)
                mediaPlayer.prepareAsync()
                mediaPlayer
            }
            return tracks
        }

    val europeData: List<Track>
        get() {
            val languages = Data.europeLanguageMap.keys.toList()
            val flattenedList = Data.europeLanguageMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = Data.europeLanguageMap.keys.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = Data.europeLanguageMap.keys.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6)

                (targetLanguages + noiseLanguages).forEach { l ->
                    val sample = flattenedList.filter { it.first == l }.shuffled().first()

                    val answer = sample.first
                    val url = sample.second
                    val track = Track(
                        url,
                        languages,
                        answer,
                    )
                    add(track)
                }
            }.shuffled()

            mediaPlayers = tracks.map {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(it.url)
                mediaPlayer.prepareAsync()
                mediaPlayer
            }
            data = tracks
            return tracks
        }
}