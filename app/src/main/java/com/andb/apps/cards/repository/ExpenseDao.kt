package com.andb.apps.cards.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.andb.apps.cards.objects.Expense

@Dao
interface ExpenseDao {

    @Insert
    fun insertExpense(expense: Expense)

    @Update
    fun updateExpense(expense: Expense)

    @Delete
    fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM Expense WHERE expense_parentID = :parentID")
    fun getExpensesFromParent(parentID: Int): LiveData<List<Expense>>

}