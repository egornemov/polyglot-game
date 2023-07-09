package com.egornemov.polyglotgame.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.egornemov.polyglotgame.PGApplication
import com.egornemov.polyglotgame.R

class ScoreCardFragment : Fragment() {
    lateinit var fullScore: Map<String, Int>
    var score = 0
    var total = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_score_card, container, false)

        val tvScore = view.findViewById<TextView>(R.id.tv_score)
        val btnRestart = view.findViewById<Button>(R.id.btn_restart)
        val btnShareResults = view.findViewById<Button>(R.id.btn_share_results)

        val langScore = fullScore.keys.size
        val langList = fullScore.keys
        val isPolyglot = langScore >= POLYGLOT_THRESHOLD
        tvScore.text = "You score is $score of $total with $langScore languages detected ($langList)\nYou are ${if (isPolyglot)  "a NATURAL" else "NOT a"} polyglot"

        btnRestart.setOnClickListener {
            activity?.run {
                (application as PGApplication).serviceLocator.mainCoordinator
                    .restart(
                        this,
                        (application as PGApplication).serviceLocator.data
                    )
            }
        }
        btnShareResults.setOnClickListener {
            Toast.makeText(context, "USE SCREENSHOT FOR SHARING", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        const val POLYGLOT_THRESHOLD = 4
    }

}