package com.egornemov.polyglotgame.di

import android.media.MediaPlayer
import com.egornemov.polyglotgame.R
import com.egornemov.polyglotgame.domain.Data
import com.egornemov.polyglotgame.domain.DataSources
import com.egornemov.polyglotgame.domain.Track
import com.egornemov.polyglotgame.presenter.coordinator.MainCoordinator
import com.google.api.gax.paging.Page
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import java.util.concurrent.TimeUnit

class ServiceLocator {

    val mainCoordinator = MainCoordinator()
    lateinit var flattenedList: List<Pair<String, String>>
    lateinit var mediaPlayers: MutableList<MediaPlayer?>
    lateinit var data: List<Track>

    fun prepareEuropeData(cb: () -> Unit) {
        val workerThread = object : Thread() {
            override fun run() {
                val credentialsInputStream =
                    mainCoordinator.context.resources.openRawResource(R.raw.service_account_key)
                val credentials = GoogleCredentials.fromStream(credentialsInputStream)
                // Create a Storage instance
                val storage: Storage = StorageOptions.newBuilder()
                    .setProjectId(DataSources.PROJECT_ID)
                    .setCredentials(credentials)
                    .build()
                    .service

                val bucketName = DataSources.BUCKET_NAME

                val bucket: Bucket = storage.get(bucketName)
                var hasNextPage = true
                var fileList: Page<Blob>

                fileList = bucket.list()
                while (hasNextPage) {

                    for (blob in fileList.iterateAll()) {
                        //println(blob.name)
                        if (blob.name.startsWith(DataSources.BUCKET_PREFIX)) {
                            val shortName = blob.name.replace(DataSources.BUCKET_PREFIX + "/", "")
                            if (Data.europeLanguages.fold(false) { acc, s -> acc || shortName.startsWith(s) }) {
                                europeblobNames.put(blob.name, shortName.split("/").first())
                            }
                            if (Data.easyLanguages.fold(false) { acc, s -> acc || shortName.startsWith(s) }) {
                                easyBlobNames.put(blob.name, shortName.split("/").first())
                            }
                            if (Data.slavicLanguages.fold(false) { acc, s -> acc || shortName.startsWith(s) }) {
                                slavicBlobNames.put(blob.name, shortName.split("/").first())
                            }
                            if (Data.germanicLanguages.fold(false) { acc, s -> acc || shortName.startsWith(s) }) {
                                germanicBlobNames.put(blob.name, shortName.split("/").first())
                            }
                            if (Data.romanceLanguages.fold(false) { acc, s -> acc || shortName.startsWith(s) }) {
                                romanceBlobNames.put(blob.name, shortName.split("/").first())
                            }
                        }
                        // Do something with the file name
                    }

                    hasNextPage = fileList.hasNextPage()
                    if (hasNextPage) {
                        fileList = fileList.nextPage
                    }
                }

                cb()
            }
        }
        workerThread.start()
    }

    private var easyBlobNames = mutableMapOf<String, String>()
    private var europeblobNames = mutableMapOf<String, String>()
    private var slavicBlobNames = mutableMapOf<String, String>()
    private var germanicBlobNames = mutableMapOf<String, String>()
    private var romanceBlobNames = mutableMapOf<String, String>()

    private var easyLanguageUrlMap = mutableMapOf<String, MutableList<String>>()
    private var europeLanguageUrlMap = mutableMapOf<String, MutableList<String>>()
    private var slavicLanguageUrlMap = mutableMapOf<String, MutableList<String>>()
    private var germanicLanguageUrlMap = mutableMapOf<String, MutableList<String>>()
    private var romanceLanguageUrlMap = mutableMapOf<String, MutableList<String>>()

    fun prepareEasyUrls(cb: () -> Unit) {
        val workerThread = object : Thread() {
            override fun run() {
                val credentialsInputStream =
                    mainCoordinator.context.resources.openRawResource(R.raw.service_account_key)
                val credentials = GoogleCredentials.fromStream(credentialsInputStream)
                // Create a Storage instance
                val storage: Storage = StorageOptions.newBuilder()
                    .setProjectId(DataSources.PROJECT_ID)
                    .setCredentials(credentials)
                    .build()
                    .service

                val bucketName = DataSources.BUCKET_NAME

                val targetLanguages = Data.easyLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }
                val noiseLanguages = Data.easyLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6)
                val languages = (targetLanguages + noiseLanguages).toMutableList()

                val caseBlobs = languages.map { l ->
                    easyBlobNames
                        .filter { it.value == l }
                        .map { it.key }
                        .shuffled()
                        .first() to l
                }

                easyLanguageUrlMap.clear()
                caseBlobs.forEach {
                    val objectName = it.first
                    val duration = 3600 // Expiration time in seconds

                    val blobId = BlobId.of(bucketName, objectName)
                    val blob: Blob = storage.get(blobId)
                    val signedUrl: String = blob.signUrl(duration.toLong(), TimeUnit.SECONDS).toString()
                    val urls = easyLanguageUrlMap.get(it.second) ?: mutableListOf()
                    urls.add(signedUrl)
                    easyLanguageUrlMap.put(it.second, urls)
                }

                cb()
            }
        }
        workerThread.start()
    }

    fun prepareEuropeUrls(cb: () -> Unit) {
        val workerThread = object : Thread() {
            override fun run() {
                val credentialsInputStream =
                    mainCoordinator.context.resources.openRawResource(R.raw.service_account_key)
                val credentials = GoogleCredentials.fromStream(credentialsInputStream)
                // Create a Storage instance
                val storage: Storage = StorageOptions.newBuilder()
                    .setProjectId(DataSources.PROJECT_ID)
                    .setCredentials(credentials)
                    .build()
                    .service

                val bucketName = DataSources.BUCKET_NAME

                val targetLanguages = Data.europeLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }
                val noiseLanguages = Data.europeLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6)
                val languages = (targetLanguages + noiseLanguages).toMutableList()

                val caseBlobs = languages.map { l ->
                    europeblobNames
                        .filter { it.value == l }
                        .map { it.key }
                        .shuffled()
                        .first() to l
                }

                europeLanguageUrlMap.clear()
                caseBlobs.forEach {
                    val objectName = it.first
                    val duration = 3600 // Expiration time in seconds

                    val blobId = BlobId.of(bucketName, objectName)
                    val blob: Blob = storage.get(blobId)
                    val signedUrl: String = blob.signUrl(duration.toLong(), TimeUnit.SECONDS).toString()
                    val urls = europeLanguageUrlMap.get(it.second) ?: mutableListOf()
                    urls.add(signedUrl)
                    europeLanguageUrlMap.put(it.second, urls)
                }

                cb()
            }
        }
        workerThread.start()
    }

    fun prepareSlavicUrls(cb: () -> Unit) {
        val workerThread = object : Thread() {
            override fun run() {
                val credentialsInputStream =
                    mainCoordinator.context.resources.openRawResource(R.raw.service_account_key)
                val credentials = GoogleCredentials.fromStream(credentialsInputStream)
                // Create a Storage instance
                val storage: Storage = StorageOptions.newBuilder()
                    .setProjectId(DataSources.PROJECT_ID)
                    .setCredentials(credentials)
                    .build()
                    .service

                val bucketName = DataSources.BUCKET_NAME

                val targetLanguages = Data.slavicLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }
                val noiseLanguages = Data.slavicLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6)
                val languages = (targetLanguages + noiseLanguages).toMutableList()

                val caseBlobs = languages.map { l ->
                    slavicBlobNames
                        .filter { it.value == l }
                        .map { it.key }
                        .shuffled()
                        .first() to l
                }

                slavicLanguageUrlMap.clear()
                caseBlobs.forEach {
                    val objectName = it.first
                    val duration = 3600 // Expiration time in seconds

                    val blobId = BlobId.of(bucketName, objectName)
                    val blob: Blob = storage.get(blobId)
                    val signedUrl: String = blob.signUrl(duration.toLong(), TimeUnit.SECONDS).toString()
                    val urls = slavicLanguageUrlMap.get(it.second) ?: mutableListOf()
                    urls.add(signedUrl)
                    slavicLanguageUrlMap.put(it.second, urls)
                }

                cb()
            }
        }
        workerThread.start()
    }

    fun prepareGermanicUrls(cb: () -> Unit) {
        val workerThread = object : Thread() {
            override fun run() {
                val credentialsInputStream =
                    mainCoordinator.context.resources.openRawResource(R.raw.service_account_key)
                val credentials = GoogleCredentials.fromStream(credentialsInputStream)
                // Create a Storage instance
                val storage: Storage = StorageOptions.newBuilder()
                    .setProjectId(DataSources.PROJECT_ID)
                    .setCredentials(credentials)
                    .build()
                    .service

                val bucketName = DataSources.BUCKET_NAME

                val targetLanguages = Data.germanicLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }
                val noiseLanguages = Data.germanicLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6)
                val languages = (targetLanguages + noiseLanguages).toMutableList()

                val caseBlobs = languages.map { l ->
                    germanicBlobNames
                        .filter { it.value == l }
                        .map { it.key }
                        .shuffled()
                        .first() to l
                }

                germanicLanguageUrlMap.clear()
                caseBlobs.forEach {
                    val objectName = it.first
                    val duration = 3600 // Expiration time in seconds

                    val blobId = BlobId.of(bucketName, objectName)
                    val blob: Blob = storage.get(blobId)
                    val signedUrl: String = blob.signUrl(duration.toLong(), TimeUnit.SECONDS).toString()
                    val urls = germanicLanguageUrlMap.get(it.second) ?: mutableListOf()
                    urls.add(signedUrl)
                    germanicLanguageUrlMap.put(it.second, urls)
                }

                cb()
            }
        }
        workerThread.start()
    }

    fun prepareRomanceUrls(cb: () -> Unit) {
        val workerThread2 = object : Thread() {
            override fun run() {
                val credentialsInputStream =
                    mainCoordinator.context.resources.openRawResource(R.raw.service_account_key)
                val credentials = GoogleCredentials.fromStream(credentialsInputStream)
                // Create a Storage instance
                val storage: Storage = StorageOptions.newBuilder()
                    .setProjectId(DataSources.PROJECT_ID)
                    .setCredentials(credentials)
                    .build()
                    .service

                val bucketName = DataSources.BUCKET_NAME

                val targetLanguages = Data.romanceLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }
                val noiseLanguages = Data.romanceLanguages.let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6)
                val languages = (targetLanguages + noiseLanguages).toMutableList()

                val caseBlobs = languages.mapIndexed { i, l ->
                    romanceBlobNames
                        .filter { it.value == l }
                        .map { it.key }
                        .shuffled()
                        .first() to l
                }

                romanceLanguageUrlMap.clear()
                caseBlobs.forEach {
                    val objectName = it.first
                    val duration = 3600 // Expiration time in seconds

                    val blobId = BlobId.of(bucketName, objectName)
                    val blob: Blob = storage.get(blobId)
                    val signedUrl: String = blob.signUrl(duration.toLong(), TimeUnit.SECONDS).toString()
                    val urls = romanceLanguageUrlMap.get(it.second) ?: mutableListOf()
                    urls.add(signedUrl)
                    romanceLanguageUrlMap.put(it.second, urls)
                }

                cb()
            }
        }
        workerThread2.start()
    }

    val easyTracks: List<Track>
        get() {
            val languages = easyLanguageUrlMap.keys.toList()
            val flattenedList = easyLanguageUrlMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = easyLanguageUrlMap.keys.toList().let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = easyLanguageUrlMap.keys.toList().let {
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

    val europeanTracks: List<Track>
        get() {
            val languages = europeLanguageUrlMap.keys.toList()
            val flattenedList = europeLanguageUrlMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = europeLanguageUrlMap.keys.toList().let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = europeLanguageUrlMap.keys.toList().let {
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

    val slavicTracks: List<Track>
        get() {
            val languages = slavicLanguageUrlMap.keys.toList()
            val flattenedList = slavicLanguageUrlMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = slavicLanguageUrlMap.keys.toList().let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = slavicLanguageUrlMap.keys.toList().let {
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

    val germanicTracks: List<Track>
        get() {
            val languages = germanicLanguageUrlMap.keys.toList()
            val flattenedList = germanicLanguageUrlMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = germanicLanguageUrlMap.keys.toList().let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = germanicLanguageUrlMap.keys.toList().let {
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

    val romanceTracks: List<Track>
        get() {
            val languages = romanceLanguageUrlMap.keys.toList()
            val flattenedList = romanceLanguageUrlMap.flatMap { entry ->
                entry.value.map { value -> entry.key to value }
            }
            this.flattenedList = flattenedList

            val tracks: List<Track> = mutableListOf<Track>().apply {
                val targetLanguages = romanceLanguageUrlMap.keys.toList().let {
                    if (it.size < 6) {
                        it + it
                    } else {
                        it
                    }
                }.shuffled().subList(0, 6).toList().run {
                    this + this
                }

                val noiseLanguages = romanceLanguageUrlMap.keys.toList().let {
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
}