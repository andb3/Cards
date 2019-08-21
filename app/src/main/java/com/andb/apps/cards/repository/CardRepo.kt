package com.andb.apps.cards.repository

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.andb.apps.cards.objects.*
import com.andb.apps.cards.utils.newIoThread
import kotlin.random.Random

object CardRepo {

    val cards = cardsDao().getCards()
    var currentCard = DefaultLiveData(0)

    val card = Transformations.switchMap(currentCard) { current ->
        Log.d("liveDataCheck", "currentCard switchMap")
        return@switchMap Transformations.map(cards) { cardList ->
            Log.d("liveDataCheck", "cards map")
            return@map cardList[current]
/*            return@switchMap Transformations.map(expensesDao().getExpensesFromParent(base.id)){ expenses->
                val new = Card(base.id, base.name, base.amount, expenses, base.type)
                return@map
            }*/
        }
    }

    val expenses = Transformations.switchMap(card) { expensesDao().getExpensesFromParent(it.id) }

    fun addExpense(expense: Expense, pos: Int? = null) {
        if (pos != null) {
            expenses.value?.forEach {
                if (it.index >= pos) {
                    it.index++
                    editExpense(it)
                }
            }
            expense.index = pos
            newIoThread {
                expensesDao().insertExpense(expense)
            }
        } else {
            expense.index = expenses.value?.size ?: 0
            newIoThread {
                expensesDao().insertExpense(expense)
            }
        }
    }

    fun editExpense(expense: Expense) {
        newIoThread {
            expensesDao().updateExpense(expense)
        }
    }

    fun removeExpense(expense: Expense) {
        newIoThread {
            expensesDao().deleteExpense(expense)
        }
    }

    fun findExpenseByID(id: Int): Expense? {
        return expenses.value?.find { it.id == id }
    }

    fun generateID(): Int {
        val keyList: List<Int> = (cards.value?.map { it.id } ?: listOf()) + (expenses.value?.map { it.id } ?: listOf())
        var random = Random.nextInt()
        while (keyList.contains(random)) {
            random = Random.nextInt()
        }
        return random
    }
}

open class DefaultLiveData<T>(val initialValue: T) : MediatorLiveData<T>() {
    override fun getValue(): T {
        return super.getValue() ?: initialValue
    }
}

/**MediatorLiveData of List<T> with better sync to backing list and better modification methods**/
open class ListLiveData<T>(initialList: List<T> = emptyList()) : MediatorLiveData<List<T>>() {
    private val backingList: MutableList<T> = initialList.toMutableList()

    val size
        get() = backingList.size

    operator fun get(pos: Int): T {
        return backingList[pos]
    }

    fun set(pos: Int, item: T) {
        backingList[pos] = item
        postValue(backingList)
    }

    fun add(item: T) {
        backingList.add(item)
        postValue(backingList)
    }

    fun add(item: T, index: Int = backingList.size) {
        backingList.add(index, item)
        postValue(backingList)
    }

    fun addAll(items: Collection<T>) {
        backingList.addAll(items)
        postValue(backingList)
    }

    fun remove(item: T) {
        backingList.remove(item)
        postValue(backingList)
    }

    fun removeAt(index: Int) {
        backingList.removeAt(index)
        postValue(backingList)
    }

    fun drop(by: Int) {
        for (i in 0 until by) {
            backingList.removeAt(backingList.size - i)
        }
        postValue(backingList)
    }

    fun last(): T {
        return backingList.last()
    }


    fun lastOrNull(): T? {
        return backingList.lastOrNull()
    }

    override fun postValue(value: List<T>?) {
        if (value !== backingList) {
            backingList.clear()
            backingList.addAll(value.orEmpty())
        }
        super.postValue(backingList)
    }

    override fun setValue(value: List<T>?) {
        if (value !== backingList) {
            backingList.clear()
            backingList.addAll(value.orEmpty())
        }
        super.setValue(backingList)
    }

    override fun getValue(): List<T> {
        return backingList
    }

}

fun <T> MutableLiveData<T>.refresh() {
    this.value = this.value
}
