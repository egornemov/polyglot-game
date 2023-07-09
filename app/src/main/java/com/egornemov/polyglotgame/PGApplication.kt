package com.egornemov.polyglotgame

import android.app.Application
import com.egornemov.polyglotgame.di.ServiceLocator

class PGApplication : Application() {
    val serviceLocator = ServiceLocator()
}