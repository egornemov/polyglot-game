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
import com.egornemov.polyglotgame.view.GameModeFragment
import com.egornemov.polyglotgame.view.QuizCardFragment
import com.egornemov.polyglotgame.view.ScoreCardFragment

class MainCoordinator {

    private val containerId = R.id.fragment_container

    private lateinit var fragmentManager: FragmentManager

    lateinit var context: Context

    fun context(activity: FragmentActivity) {
        fragmentManager = activity.supportFragmentManager
        context = activity.baseContext
    }

    fun gameMode() {
        navigateToGameMode()
    }

    private fun navigateToGameMode() {
        val gameMode = GameModeFragment()
        replaceFragment(gameMode)
    }

    fun quizCard(tracks: List<Track>) {
        val target = calculateTarget(tracks)
        navigateToQuizCard(tracks, target, 1, 0, emptyMap(), tracks.size, 0)
    }

    fun nextStep(tracks: List<Track>, target: Map<String, Int>, step: Int, score: Int, fullScore: Map<String, Int>, total: Int, solutionTimeMs: Long) {
        navigateToQuizCard(tracks, target, step, score, fullScore, total, solutionTimeMs)
    }

    private fun navigateToQuizCard(tracks: List<Track>, target: Map<String, Int>, step: Int, score: Int, fullScore: Map<String, Int>, total: Int, solutionTimeMs: Long) {
        if (tracks.isEmpty()) {
            val scoreCard = ScoreCardFragment()
            scoreCard.target = target
            scoreCard.score = score
            scoreCard.fullScore = fullScore
            scoreCard.total = total
            scoreCard.solutionTimeMs = solutionTimeMs
            replaceFragment(scoreCard)
        } else {
            val quizCard = QuizCardFragment()
            quizCard.targetTrack = tracks[0]
            quizCard.restOfTracks = tracks.filterIndexed { index, _ -> index > 0 }
            quizCard.step = step
            quizCard.target = target
            quizCard.score = score
            quizCard.fullScore = fullScore
            quizCard.total = total
            quizCard.solutionTimeMs = solutionTimeMs

            replaceFragment(quizCard)
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

    fun restart(tracks: List<Track>) {
        val target = calculateTarget(tracks)
        navigateToQuizCard(tracks, target, 1, 0, emptyMap(), tracks.size, 0)
    }

    private fun calculateTarget(tracks: List<Track>): Map<String, Int> {
        return mutableMapOf<String, Int>().apply {
            tracks.forEach {
                val item: Int = get(it.answer) ?: 0
                this[it.answer] = item + 1
            }
        }
    }
}