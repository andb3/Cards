package com.andb.apps.cards

import android.app.Application
import com.andb.apps.cards.repository.Database
import com.andb.apps.cards.repository.cardsDao
import com.andb.apps.cards.repository.inititalizeDB

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Database.setDB(this)
        inititalizeDB(this)
    }
}