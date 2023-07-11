package com.egornemov.polyglotgame.view

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    lateinit var target: Map<String, Int>
    lateinit var fullScore: Map<String, Int>
    lateinit var restOfTracks: List<Track>
    lateinit var targetTrack: Track
    var step = 0
    var score = 0
    var total = 0

    private var mediaPlayer: MediaPlayer? = MediaPlayer()

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

        val btnRestart = view.findViewById<Button>(R.id.btn_restart)
        val btnPlay = view.findViewById<Button>(R.id.btn_play)
        val btnChoiceA = view.findViewById<Button>(R.id.btn_choice_a)
        val btnChoiceB = view.findViewById<Button>(R.id.btn_choice_b)
        val btnChoiceC = view.findViewById<Button>(R.id.btn_choice_c)
        val btnChoiceD = view.findViewById<Button>(R.id.btn_choice_d)
        val btnChoiceE = view.findViewById<Button>(R.id.btn_choice_e)
        val btnChoiceF = view.findViewById<Button>(R.id.btn_choice_f)

        val tvCardDescription = view.findViewById<TextView>(R.id.tv_card_description)
        val pbMediaplayerInit = view.findViewById<ProgressBar>(R.id.pb_mediaplayer_init)

        tvCardDescription.text = if (step == 1) {
            "Listen track and choose the language. It's first of $total."
        } else {
            "It's $step of $total"
        }

        mediaPlayer?.setDataSource(targetTrack.url)

        btnRestart.setOnClickListener {
            activity?.run {
                (application as PGApplication).serviceLocator.mainCoordinator
                    .restart(
                        (application as PGApplication).serviceLocator.data
                    )
            }
        }

        btnPlay.setOnClickListener {
            pbMediaplayerInit.isVisible = true
            btnPlay.isEnabled = false
            btnPlay.isClickable = false

            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener { mp ->

                pbMediaplayerInit.isVisible = false

                val duration = DURATION_MS //mp.duration
                val startPosition = mp.duration / 8 + Random().nextInt(mp.duration / 2)
                mp.seekTo(startPosition)
                mp.start()

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
                                    btnPlay.text = "PLAY"
                                    positionUpdater.purge()
                                } else {
                                    btnPlay.text = "PLAYING ($count of $DURATION_S)"
                                }
                            }
                        }
                    }
                    positionUpdater.scheduleAtFixedRate(positionUpdate, 1000, 1000)



                    // Stop playback after the desired duration
                    handler.postDelayed({
                        if (mediaPlayer != null) {
                            mp.pause()
                            mp.seekTo(startPosition) // Reset position for future playback

                            btnPlay.isEnabled = true
                            btnPlay.isClickable = true
                        }
                    }, duration.toLong())
                }
            }
        }

        initChoices(listOf(btnChoiceA, btnChoiceB, btnChoiceC, btnChoiceD,
//            btnChoiceE, btnChoiceF,
            ), targetTrack)

        return view
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
            val list = this
        }.shuffled().forEachIndexed { index, pair ->
            buttons.get(index).run {
                text = pair.first
                setOnClickListener {
                    mediaPlayer?.release()
                    mediaPlayer = null
                    (activity?.application as PGApplication)
                        .serviceLocator
                        .mainCoordinator
                        .nextStep(restOfTracks, target, step + 1, score + if (pair.second) 1 else 0,
                            fullScore.toMutableMap().apply {
                                if (pair.second) {
                                    val prevScore: Int = get(pair.first) ?: 0
                                    set(pair.first, prevScore + 1)
                                }
                            }, total)
                }
            }
        }
    }


}