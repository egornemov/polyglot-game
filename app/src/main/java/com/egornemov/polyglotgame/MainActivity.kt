package com.egornemov.polyglotgame

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.egornemov.polyglotgame.domain.Track
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        (application as PGApplication)
            .serviceLocator
            .mediaPlayer = MediaPlayer()

        (application as PGApplication)
            .serviceLocator
            .mainCoordinator
            .quizCard(
                this,
                (application as PGApplication)
                    .serviceLocator.data
            )
    }

    override fun onDestroy() {
        (application as PGApplication)
            .serviceLocator
            .mediaPlayer.release()
        super.onDestroy()
    }
}