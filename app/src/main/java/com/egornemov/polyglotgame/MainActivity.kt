package com.egornemov.polyglotgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.FrameLayout
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Handler(mainLooper).postDelayed(
            {
                val container = findViewById<FrameLayout>(R.id.fragment_container)
                container.isVisible = true
                (application as PGApplication)
                    .serviceLocator
                    .mainCoordinator.run {
                        context(this@MainActivity)
                        gameMode()
                    }
            },
            1600L
        )
    }
}