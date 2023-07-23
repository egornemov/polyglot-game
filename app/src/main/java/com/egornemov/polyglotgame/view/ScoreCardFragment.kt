package com.egornemov.polyglotgame.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.egornemov.polyglotgame.BuildConfig
import com.egornemov.polyglotgame.PGApplication
import com.egornemov.polyglotgame.R
import kotlin.random.Random

class ScoreCardFragment : Fragment() {
    lateinit var target: Map<String, Int>
    lateinit var fullScore: Map<String, Int>
    var score = 0
    var total = 0
    var solutionTimeMs: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_score_card, container, false)

        val tvBackground = view.findViewById<TextView>(R.id.tv_background)
        val tvScore = view.findViewById<TextView>(R.id.tv_score)
        val btnRestart = view.findViewById<ImageButton>(R.id.btn_restart)
        val btnShareResults = view.findViewById<ImageButton>(R.id.btn_share_results)

        val normalizedScore = fullScore.filter { target[it.key] == it.value && it.value > 1 }
        val langScore = normalizedScore.keys.size
        val langList = normalizedScore.keys
        val isPolyglot = langScore >= POLYGLOT_THRESHOLD

        val languageToLearn: String = target.keys.shuffled().first()

        val colors = listOf<Int>(
            resources.getColor(R.color.red_light_0f),
            resources.getColor(R.color.orange_light_0f),
            resources.getColor(R.color.yellow_light_0f),
            resources.getColor(R.color.green_light_0f),
            resources.getColor(R.color.blue_light_0f),
            resources.getColor(R.color.indigo_light_0f),
            resources.getColor(R.color.violet_light_0f),
        )
        tvBackground.setTextColor(colors.get(Random.nextInt(0, colors.size - 1)))
        val languagePattern: String = if (langScore == 0) {
            (target.keys.toList() + target.keys.toList() + target.keys.toList() + target.keys.toList())
                .reduce { acc, s ->
                    val l = s + "?"
                    val i = when (Random.nextInt(0, 5)) {
                        0 -> ""
                        1 -> " "
                        2 -> "  "
                        3 -> "."
                        4 -> "-"
                        5 -> "_"
                        else -> ""
                    }
                    acc + i + l
                }
        } else {
            (langList.toList() + langList.toList() + langList.toList() + langList.toList() +
                    langList.toList() + langList.toList() + langList.toList() + langList.toList() +
                    langList.toList() + langList.toList() + langList.toList() + langList.toList() +
                    langList.toList() + langList.toList() + langList.toList() + langList.toList() +
                    langList.toList() + langList.toList() + langList.toList() + langList.toList() +
                    langList.toList() + langList.toList() + langList.toList() + langList.toList() +
                    langList.toList() + langList.toList() + langList.toList() + langList.toList())
                .reduce { acc, s ->

                    val i = when (Random.nextInt(0, 5)) {
                        0 -> ""
                        1 -> " "
                        2 -> "  "
                        3 -> "."
                        4 -> "-"
                        5 -> "_"
                        else -> ""
                    }
                    acc + i + s
                }
        }
        tvBackground.text = languagePattern

        val isCheated = solutionTimeMs / 1000L > 3600 * 24

        tvScore.text = if (isCheated) {
            resources.getString(R.string.score_cheated)
        } else {
            val minutes = solutionTimeMs / 1000L / 60L
            val seconds = solutionTimeMs / 1000L - minutes * 60
            resources.getString(R.string.score_stats, minutes, seconds, langScore) +
                    if (langScore == 0) {
                        resources.getString(R.string.score_zero, languageToLearn)
                    } else if (isPolyglot) {
                        resources.getString(R.string.score_polyglot)
                    } else {
                        resources.getString(R.string.score_not_polyglot)
                    }
        } + if (BuildConfig.DEBUG && false) {
            val prepTime = QuizCardFragment.preparationTime / 1000f
            QuizCardFragment.preparationTime = 0
            " PREP: $prepTime s"
        } else {
            ""
        }

        btnRestart.setOnClickListener {
            activity?.run {
                (application as PGApplication).serviceLocator.mainCoordinator
                    .gameMode()
            }
        }
        btnShareResults.setOnClickListener {
            val content = if (isCheated) {
                resources.getString(R.string.score_cheated_share)
            } else if (langScore == 0) {
                resources.getString(R.string.score_zero_share, languageToLearn, languageToLearn)
            } else if (isPolyglot) {
                val languages: String = langList.reduce { acc, s -> "$acc, $s" }
                resources.getString(R.string.score_polyglot_share, languages)
            } else {
                resources.getString(R.string.score_not_polyglot_share)
            } + " " + resources.getString(R.string.play_market_url)
            shareContent(content)
        }

        return view
    }

    private fun shareContent(content: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = SHARE_INTENT_TYPE
        intent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(intent, SHARE_DIALOG_TITLE))
    }

    companion object {
        const val POLYGLOT_THRESHOLD = 4
        const val SHARE_INTENT_TYPE = "text/plain"
        const val SHARE_DIALOG_TITLE = "Share via"
    }

}