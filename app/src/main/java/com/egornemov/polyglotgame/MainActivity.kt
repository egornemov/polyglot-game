package com.egornemov.polyglotgame

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        (application as PGApplication)
            .serviceLocator
            .mainCoordinator.run {
                context(this@MainActivity)
            }

        (application as PGApplication)
            .serviceLocator.prepareEuropeData {
                runOnUiThread {
                    val container = findViewById<FrameLayout>(R.id.fragment_container)
                    container.isVisible = true
                    (application as PGApplication)
                        .serviceLocator
                        .mainCoordinator
                        .gameMode()
                }
            }
    }
}