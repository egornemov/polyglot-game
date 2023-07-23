package com.egornemov.polyglotgame.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isVisible
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
        val btnSlavic = view.findViewById<Button>(R.id.btn_slavic)
        val btnGermanic = view.findViewById<Button>(R.id.btn_germanic)
        val btnRomance = view.findViewById<Button>(R.id.btn_romance)

        val pbEasy = view.findViewById<ProgressBar>(R.id.pb_easy)
        val pbEurope = view.findViewById<ProgressBar>(R.id.pb_europe)
        val pbSlavic = view.findViewById<ProgressBar>(R.id.pb_slavic)
        val pbGermanic = view.findViewById<ProgressBar>(R.id.pb_germanic)
        val pbRomance = view.findViewById<ProgressBar>(R.id.pb_romance)

        btnEasy.setOnClickListener {
            activity?.run {
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator
                    .quizCard(
                        (application as PGApplication)
                            .serviceLocator
                            .easyTracks
                    )
            }
        }
        activity?.run {
            (application as PGApplication)
                .serviceLocator.prepareEasyUrls {
                    btnEasy.post {
                        btnEasy.isEnabled = true
                        btnEasy.isClickable = true
                        pbEasy.isVisible = false
                    }
                }
        }

        btnEurope.setOnClickListener {
            activity?.run {
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator
                    .quizCard(
                        (application as PGApplication)
                            .serviceLocator
                            .europeanTracks
                    )
            }
        }
        activity?.run {
            (application as PGApplication)
                .serviceLocator.prepareEuropeUrls {
                    btnEurope.post {
                        btnEurope.isEnabled = true
                        btnEurope.isClickable = true
                        pbEurope.isVisible = false
                    }
                }
        }

        btnSlavic.setOnClickListener {
            activity?.run {
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator
                    .quizCard(
                        (application as PGApplication)
                            .serviceLocator
                            .slavicTracks
                    )
            }
        }
        activity?.run {
            (application as PGApplication)
                .serviceLocator.prepareSlavicUrls {
                    btnSlavic.post {
                        btnSlavic.isEnabled = true
                        btnSlavic.isClickable = true
                        pbSlavic.isVisible = false
                    }
                }
        }

        btnGermanic.setOnClickListener {
            activity?.run {
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator
                    .quizCard(
                        (application as PGApplication)
                            .serviceLocator
                            .germanicTracks
                    )
            }
        }
        activity?.run {
            (application as PGApplication)
                .serviceLocator.prepareGermanicUrls {
                    btnGermanic.post {
                        btnGermanic.isEnabled = true
                        btnGermanic.isClickable = true
                        pbGermanic.isVisible = false
                    }
                }
        }

        btnRomance.setOnClickListener {
            activity?.run {
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator
                    .quizCard(
                        (application as PGApplication)
                            .serviceLocator
                            .romanceTracks
                    )
            }
        }
        activity?.run {
            (application as PGApplication)
                .serviceLocator.prepareRomanceUrls {
                    btnRomance.post {
                        btnRomance.isEnabled = true
                        btnRomance.isClickable = true
                        pbRomance.isVisible = false
                    }
                }
        }

        return view
    }
}