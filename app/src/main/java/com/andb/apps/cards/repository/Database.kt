package com.andb.apps.cards.repository

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andb.apps.cards.objects.BaseCard
import com.andb.apps.cards.objects.Expense
import com.andb.apps.cards.objects.MoneyTypeConverter

@androidx.room.Database(entities = [BaseCard::class, Expense::class], version = 1, exportSchema = false)
@TypeConverters(value = [MoneyTypeConverter::class])
abstract class Database : RoomDatabase() {
    abstract fun cardsDao(): CardsDao
    abstract fun expensesDao(): ExpenseDao

    companion object {
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