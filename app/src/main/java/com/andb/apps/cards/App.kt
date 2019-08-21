package com.andb.apps.cards

import android.app.Application
import com.andb.apps.cards.repository.Database

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Database.setDB(this)
    }
}