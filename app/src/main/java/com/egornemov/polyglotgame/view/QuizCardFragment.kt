package com.egornemov.polyglotgame.view

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.egornemov.polyglotgame.PGApplication
import com.egornemov.polyglotgame.R
import com.egornemov.polyglotgame.domain.Track
import java.util.Random
import java.util.Timer
import java.util.TimerTask

class QuizCardFragment : Fragment() {
    var solutionTimeMs: Long = 0L
    lateinit var target: Map<String, Int>
    lateinit var fullScore: Map<String, Int>
    lateinit var restOfTracks: List<Track>
    lateinit var targetTrack: Track
    var step = 0
    var score = 0
    var total = 0

    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var startPosition: Int = 0

    private var startSolutionMs: Long = 0L

    companion object {
        const val DURATION_S = 20
        const val DURATION_MS = 1000 * DURATION_S
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_card, container, false)

        val btnRestart = view.findViewById<ImageButton>(R.id.btn_restart)
        val btnPlay = view.findViewById<ImageButton>(R.id.btn_play)
        val tvPlay = view.findViewById<TextView>(R.id.tv_play)

        val btnRefresh = view.findViewById<ImageButton>(R.id.btn_refresh)
        val tvRefresh = view.findViewById<TextView>(R.id.tv_refresh)

        val btnChoiceA = view.findViewById<Button>(R.id.btn_choice_a)
        val btnChoiceB = view.findViewById<Button>(R.id.btn_choice_b)
        val btnChoiceC = view.findViewById<Button>(R.id.btn_choice_c)
        val btnChoiceD = view.findViewById<Button>(R.id.btn_choice_d)
        val btnChoiceE = view.findViewById<Button>(R.id.btn_choice_e)
        val btnChoiceF = view.findViewById<Button>(R.id.btn_choice_f)

        val tvCardDescription = view.findViewById<TextView>(R.id.tv_card_description)
        val pbMediaplayerInit = view.findViewById<ProgressBar>(R.id.pb_mediaplayer_init)

        tvCardDescription.text = if (step == 1) {
            resources.getString(R.string.quiz_description_first, total)
        } else {
            resources.getString(R.string.quiz_description_rest, step, total)
        }

        btnRestart.setOnClickListener {
            activity?.run {
                (application as PGApplication).serviceLocator.mainCoordinator
                    .restart(
                        (application as PGApplication).serviceLocator.data
                    )
            }
        }

        btnRefresh.setOnClickListener {
            mediaPlayer?.reset()
            targetTrack = targetTrack.copy(
                url = (activity?.application as PGApplication).serviceLocator.flattenedList.filter {
                    it.first == targetTrack.answer && it.second != targetTrack.url
                }.shuffled().first().second
            )
            prepareMediaPlayer(pbMediaplayerInit, btnPlay, tvPlay, btnRefresh, tvRefresh)
        }

        pbMediaplayerInit.isVisible = true
        btnPlay.isEnabled = false
        btnPlay.isClickable = false
        btnRefresh.isVisible = true
        tvRefresh.isVisible = true

        prepareMediaPlayer(pbMediaplayerInit, btnPlay, tvPlay, btnRefresh, tvRefresh)

        btnPlay.setOnClickListener {
            playTrack(btnPlay, tvPlay)
        }

        initChoices(listOf(btnChoiceA, btnChoiceB, btnChoiceC, btnChoiceD,
//            btnChoiceE, btnChoiceF,
            ), targetTrack)

        return view
    }

    private fun prepareMediaPlayer(pbMediaplayerInit: ProgressBar, btnPlay: ImageButton, tvPlay: TextView, btnRefresh: ImageButton, tvRefresh: TextView) {

        mediaPlayer?.setDataSource(targetTrack.url)
        mediaPlayer?.setOnPreparedListener { mp ->

            pbMediaplayerInit.isVisible = false
            btnPlay.isEnabled = true
            btnPlay.isClickable = true

            startPosition = mp.duration / 8 + Random().nextInt(mp.duration / 2)
            mp.seekTo(startPosition)

            btnRefresh.isVisible = false
            tvRefresh.isVisible = false

            playTrack(btnPlay, tvPlay)
        }
        mediaPlayer?.prepareAsync()
    }

    private var isResumable = false
    private var isPausedFragment = false

    override fun onResume() {
        super.onResume()
        isPausedFragment = false
        if (isResumable) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        isPausedFragment = true
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
            isResumable = false
        }
        super.onPause()
    }

    private fun playTrack(btnPlay: ImageButton, tvPlay: TextView) {
        isResumable = true
        if (isPausedFragment) {
            return
        }
        btnPlay.isEnabled = false
        btnPlay.isClickable = false
        tvPlay.isVisible = true
        startSolutionMs = System.currentTimeMillis()
        mediaPlayer?.start()

        val mainLooper = activity?.mainLooper
        mainLooper?.run {
            val handler = Handler(this)
            val positionUpdater = Timer()
            val positionUpdate = object : TimerTask() {

                var count = 0

                override fun run() {
                    count++
                    handler.post {
                        if (count > DURATION_S) {
                            positionUpdater.cancel()
                            tvPlay.text = ""
                            tvPlay.isVisible = false
                            count = 0
                        } else {
                            tvPlay.run {
                                text = context.resources.getString(R.string.quiz_playback_status, count, DURATION_S)
                            }
                        }
                    }
                }
            }
            positionUpdater.scheduleAtFixedRate(positionUpdate, 1000, 1000)



            // Stop playback after the desired duration
            handler.postDelayed({
                if (mediaPlayer != null) {
                    mediaPlayer?.pause()
                    mediaPlayer?.seekTo(startPosition) // Reset position for future playback

                    btnPlay.isEnabled = true
                    btnPlay.isClickable = true
                }
            }, DURATION_MS.toLong())
        }
    }

    private fun initChoices(buttons: List<Button>, targetTrack: Track) {
        buttons.forEach {
            it.isVisible = true
        }
        val list = mutableListOf<Pair<String, Boolean>>()
        list.apply {
            add(targetTrack.answer to true)
            targetTrack.choices.filter { it != targetTrack.answer }.shuffled().subList(0, 3).map { it to false }.let { it ->
                addAll(it)
            }
        }.shuffled().forEachIndexed { index, pair ->
            buttons.get(index).run {
                text = pair.first
                setOnClickListener {
                    mediaPlayer?.release()
                    mediaPlayer = null
                    val currenSolutionTimeMs = System.currentTimeMillis() - startSolutionMs
                    (activity?.application as PGApplication)
                        .serviceLocator
                        .mainCoordinator
                        .nextStep(restOfTracks, target, step + 1, score + if (pair.second) 1 else 0,
                            fullScore.toMutableMap().apply {
                                if (pair.second) {
                                    val prevScore: Int = get(pair.first) ?: 0
                                    set(pair.first, prevScore + 1)
                                }
                            }, total,
                            solutionTimeMs + currenSolutionTimeMs)
                }
            }
        }
    }


}