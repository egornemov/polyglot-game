package com.egornemov.polyglotgame.presenter.coordinator

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.egornemov.polyglotgame.MainActivity
import com.egornemov.polyglotgame.R
import com.egornemov.polyglotgame.domain.Track
import com.egornemov.polyglotgame.view.QuizCardFragment
import com.egornemov.polyglotgame.view.ScoreCardFragment

class MainCoordinator {

    private val containerId = R.id.fragment_container

    private lateinit var fragmentManager: FragmentManager
    fun quizCard(activity: FragmentActivity, tracks: List<Track>) {
        fragmentManager = activity.supportFragmentManager
        navigateToQuizCard(tracks, 1, 0, emptyMap(), tracks.size, true)
    }

    fun nextStep(tracks: List<Track>, step: Int, score: Int, fullScore: Map<String, Int>, total: Int) {
        navigateToQuizCard(tracks, step, score, fullScore, total)
    }

    private fun navigateToQuizCard(tracks: List<Track>, step: Int, score: Int, fullScore: Map<String, Int>, total: Int, isFirst: Boolean = false) {
        if (tracks.isEmpty()) {
            val scoreCard = ScoreCardFragment()
            scoreCard.score = score
            scoreCard.fullScore = fullScore
            scoreCard.total = total
            replaceFragment(scoreCard)
        } else {
            val quizCard = QuizCardFragment()
            quizCard.targetTrack = tracks[0]
            quizCard.restOfTracks = tracks.filterIndexed { index, _ -> index > 0 }
            quizCard.step = step
            quizCard.score = score
            quizCard.fullScore = fullScore
            quizCard.total = total

            if (isFirst) {
                addFragment(quizCard)
            } else {
                replaceFragment(quizCard)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun addFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .add(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun popFragment() {
        fragmentManager.popBackStack()
    }

    private fun swapFragment(fragment: Fragment) {
        popFragment()
        addFragment(fragment)
    }

    fun restart(activity: FragmentActivity, tracks: List<Track>) {
        navigateToQuizCard(tracks, 1, 0, emptyMap(), tracks.size, false)
    }
}