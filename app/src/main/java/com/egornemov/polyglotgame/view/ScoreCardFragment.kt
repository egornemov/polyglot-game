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
    var solutionTimeMs = 0

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

        tvScore.text = "You score is $score of $total with $langScore languages is ${solutionTimeMs / 1000}  seconds\n" +
                "${if (langScore == 0) "Just start with $languageToLearn." else "You are ${if (isPolyglot)  "a NATURAL" else "NOT a"} polyglot"}"

        btnRestart.setOnClickListener {
            activity?.run {
                (application as PGApplication).serviceLocator.mainCoordinator
                    .restart(
                        (application as PGApplication).serviceLocator.data
                    )
            }
        }
        btnShareResults.setOnClickListener {
            val content = if (langScore == 0) {
                "I will learn $languageToLearn this year! Do you know $languageToLearn?"
            } else if (isPolyglot) {
                "I'm a NATURAL polyglot. I know ${langList.reduce { acc, s -> acc + ", " + s }}.\nBet that you're not?"
            } else {
                "I'm NOT a polyglot.\nBet that you are NOT a polyglot too?"
            }
            shareContent(content)
        }

        return view
    }

    private fun shareContent(content: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    companion object {
        const val POLYGLOT_THRESHOLD = 4
    }

}