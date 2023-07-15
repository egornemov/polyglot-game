package com.egornemov.polyglotgame.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.egornemov.polyglotgame.PGApplication
import com.egornemov.polyglotgame.R

class GameModeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_mode, container, false)

        val btnEasy = view.findViewById<Button>(R.id.btn_easy)
        val btnEurope = view.findViewById<Button>(R.id.btn_europe)

        btnEasy.setOnClickListener {
            activity?.run {
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator
                    .quizCard(
                        (application as PGApplication)
                            .serviceLocator.easyData
                    )
            }
        }
        btnEurope.setOnClickListener {
            activity?.run {
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator
                    .quizCard(
                        (application as PGApplication)
                            .serviceLocator.europeData
                    )
            }
        }

        return view
    }
}