package com.andb.apps.cards.repository

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andb.apps.cards.objects.BaseCard
import com.andb.apps.cards.objects.Card
import com.andb.apps.cards.objects.Expense

@androidx.room.Database(entities = [BaseCard::class, Expense::class], version = 1, exportSchema = true)
abstract class Database : RoomDatabase() {
    abstract fun cardsDao(): CardsDao
    abstract fun expensesDao(): ExpenseDao

    companion object{
        lateinit var db: Database
        fun setDB(ctxt: Context) {
            db = Room.databaseBuilder(ctxt, Database::class.java, "CardsDatabase")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

fun db() = Database.db
fun cardsDao() = Database.db.cardsDao()
fun expensesDao() = Database.db.expensesDao()