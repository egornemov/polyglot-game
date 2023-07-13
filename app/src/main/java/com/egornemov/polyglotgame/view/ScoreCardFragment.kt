package com.egornemov.polyglotgame.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.egornemov.polyglotgame.PGApplication
import com.egornemov.polyglotgame.R

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

        val normalizedScore = fullScore.filter { target[it.key] == it.value }
        val langScore = normalizedScore.keys.size
        val langList = normalizedScore.keys
        val isPolyglot = langScore >= POLYGLOT_THRESHOLD

        val languageToLearn: String = target.keys.shuffled().first()

        val languagePattern: String = if (langScore == 0) {
            target.keys.reduce { acc, s -> acc + " " + s + "?" }
        } else {
            langList.reduce { acc, s -> acc + " " + s }
        }

        tvBackground.text = languagePattern + languagePattern + languagePattern + languagePattern + languagePattern + languagePattern + languagePattern + languagePattern + languagePattern + languagePattern

        val isCheated = solutionTimeMs / 1000L > 3600 * 24

        tvScore.text = if (isCheated) {
            resources.getString(R.string.score_cheated)
        } else {
            val minutes = solutionTimeMs / 1000L / 60L
            val seconds = (solutionTimeMs - (solutionTimeMs / 1000L / 60L) * 60) / 1000L
            resources.getString(R.string.score_stats, minutes, seconds, langScore) +
                    if (langScore == 0) {
                        resources.getString(R.string.score_zero, languageToLearn)
                    } else if (isPolyglot) {
                        resources.getString(R.string.score_polyglot)
                    } else {
                        resources.getString(R.string.score_not_polyglot)
                    }
        }

        btnRestart.setOnClickListener {
            activity?.run {
                (application as PGApplication).serviceLocator.mainCoordinator
                    .restart(
                        (application as PGApplication).serviceLocator.data
                    )
            }
        }
        btnShareResults.setOnClickListener {
            val content = if (isCheated) {
                resources.getString(R.string.score_cheated_share)
            } else if (langScore == 0) {
                resources.getString(R.string.score_zero_share, languageToLearn, languageToLearn)
            } else if (isPolyglot) {
                resources.getString(R.string.score_polyglot_share, langList.reduce { acc, s -> "$acc, $s" })
            } else {
                resources.getString(R.string.score_not_polyglot_share)
            }
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