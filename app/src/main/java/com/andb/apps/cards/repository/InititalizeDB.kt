package com.andb.apps.cards.repository

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.andb.apps.cards.objects.*
import com.andb.apps.cards.utils.newIoThread

private val card0 = BaseCard(0, "Visa", Money(50.00), type = CARD_TYPE_GENERIC)
private val card1 = BaseCard(1, "Amazon", Money(25.00), type = CARD_TYPE_GIFT)
private val card2 = BaseCard(2, "Best Buy", Money(30.00), type = CARD_TYPE_GIFT)
private val card3 = BaseCard(3, "Google Play", Money(15.00), type = CARD_TYPE_GIFT)


fun inititalizeDB(ctxt: Context){
    val prefs = ctxt.getSharedPreferences("Once", Context.MODE_PRIVATE)
    val alreadyRan = prefs.contains("prepopulateDB")
    if(!alreadyRan){
        Log.d("Once", "prepopulating database, db: ${cardsDao()}")
        prefs.edit { putBoolean("prepopulateDB", true) }
        newIoThread {
            cardsDao().insertCard(card0)
            cardsDao().insertCard(card1)
            cardsDao().insertCard(card2)
            cardsDao().insertCard(card3)
            Log.d("Once", "prepop done, cards: ${cardsDao().getCardsDead().size}")
        }
    }
}