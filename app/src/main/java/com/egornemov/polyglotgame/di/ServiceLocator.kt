package com.egornemov.polyglotgame.di

import android.content.Context
import android.media.MediaPlayer
import com.egornemov.polyglotgame.R
import com.egornemov.polyglotgame.domain.Data
import com.egornemov.polyglotgame.domain.Track
import com.egornemov.polyglotgame.presenter.coordinator.MainCoordinator
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.random.Random

class ServiceLocator {
    val mainCoordinator = MainCoordinator()

    lateinit var flattenedList: List<Pair<String, String>>

    lateinit var mediaPlayers: MutableList<MediaPlayer?>
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

            mediaPlayers = tracks.mapIndexed { index, track ->
                val mediaPlayer = MediaPlayer()
                if (index == 0) {
                    mediaPlayer.setDataSource(track.url)
                    mediaPlayer.prepareAsync()
                    mediaPlayer
                } else null
//                mediaPlayer
            }.toMutableList()
            data = tracks
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
            }.toMutableList()
            data = tracks
            return tracks
        }

    val gcsEasyData: List<Track>
        get() {
            val languages = Data.gcsEasyLanguageMap.keys.toList()
            val flattenedList = Data.gcsEasyLanguageMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = Data.gcsEasyLanguageMap.keys.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = Data.gcsEasyLanguageMap.keys.let {
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

            val credentialsInputStream = mainCoordinator.context.resources.openRawResource(R.raw.service_account_key)
            val credentials = GoogleCredentials.fromStream(credentialsInputStream)
            // Create a Storage instance
            val storage: Storage = StorageOptions.newBuilder()
                .setProjectId(Data.PROJECT_ID)
                .setCredentials(credentials)
                .build()
                .service

            val bucketName = Data.BUCKET_NAME

            val workerThread2 = object : Thread() {
                override fun run() {
                    mediaPlayers = tracks.map {
                        val mediaPlayer = MediaPlayer()
                        val url = if (it.url.contains("https://")) {
                            it.url
                        } else {
                            val startPosition = abs(java.util.Random().run {
                                setSeed(it.url.hashCode().toLong())
                                nextInt(300000)
                            })

                            val folder = if (startPosition < 30000) {
                                "0000"
                            } else if (startPosition < 60000) {
                                "0030"
                            } else if (startPosition < 90000) {
                                "0100"
                            } else if (startPosition < 120000) {
                                "0130"
                            } else if (startPosition < 150000) {
                                "0200"
                            } else if (startPosition < 180000) {
                                "0230"
                            } else if (startPosition < 210000) {
                                "0300"
                            } else if (startPosition < 240000) {
                                "0330"
                            } else if (startPosition < 270000) {
                                "0400"
                            } else if (startPosition < 300000) {
                                "0430"
                            } else {
                                "0000"
                            }

                            val objectName = "${Data.BUCKET_PREFIX}/${folder}/${it.url}"
                            val duration = 3600 // Expiration time in seconds

                            val blobId = BlobId.of(bucketName, objectName)
                            val blob: Blob = storage.get(blobId)
                            val signedUrl: String = blob.signUrl(duration.toLong(), TimeUnit.SECONDS).toString()
                            signedUrl
                        }
                        mediaPlayer.setDataSource(url)
                        mediaPlayer.prepareAsync()
                        mediaPlayer
                    }.toMutableList()

                }
//                }
            }
            workerThread2.start()
            workerThread2.join()

            data = tracks
            return tracks
        }
}