package com.egornemov.polyglotgame.di

import android.content.Context
import android.media.MediaPlayer
import com.egornemov.polyglotgame.domain.Data
import com.egornemov.polyglotgame.domain.Track
import com.egornemov.polyglotgame.presenter.coordinator.MainCoordinator
import kotlin.random.Random

class ServiceLocator {
    val mainCoordinator = MainCoordinator()
    lateinit var mediaPlayer: MediaPlayer

    private val languages = Data.languageMap.keys.toList()
    val flattenedList = Data.languageMap.flatMap { entry ->
        entry.value.map { value -> entry.key to value }
    }

    private val tracks: List<Track>
        get() = mutableListOf<Track>().apply {
            val targetLanguages = Data.languageMap.keys.let {
                if (it.size < 6) {
                    it + it
                } else {
                    it
                }
            }.shuffled().subList(0, 6).toList().run {
                this + this
            }

            val noiseLanguages = Data.languageMap.keys.let {
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

    lateinit var mediaPlayers: List<MediaPlayer>

    val data: List<Track>
        get() {
            val newTracks = tracks
            mediaPlayers = newTracks.map {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(it.url)
                mediaPlayer.prepareAsync()
                mediaPlayer
            }
            return newTracks
        }
}