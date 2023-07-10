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
    private val flattenedList = Data.languageMap.flatMap { entry ->
        entry.value.map { value -> entry.key to value }
    }

    private val tracks: List<Track>
        get() = mutableListOf<Track>().apply {
            for (i in 0 .. 5) {
                val sample = flattenedList.get(
                    Random.nextInt(0, flattenedList.size)
                )

                val answer = sample.first
                val url = sample.second
                val track = Track(
                    url,
                    languages,
                    answer,
                )
                add(track)
            }
            val tail = mutableListOf<Track>()
            forEach { t ->
                val sample = flattenedList.filter { it.first == t.answer }.shuffled().first()
                val answer = sample.first
                val url = sample.second
                val track = Track(
                    url,
                    languages,
                    answer,
                )
                tail.add(track)
            }
            addAll(tail)
        }.shuffled()

    val data: List<Track>
        get() = tracks
}