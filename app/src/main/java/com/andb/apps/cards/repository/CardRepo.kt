package com.andb.apps.cards.repository

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.andb.apps.cards.objects.*
import kotlin.random.Random

object CardRepo {
    private val card0 = Card(1, "Visa", Money(50.00), type = CARD_TYPE_GENERIC, expenses = arrayListOf(Expense(0, EXPENSE_TYPE_FOOD, Money(19.59), 1), Expense(1, EXPENSE_TYPE_OTHER, Money(15.00), 1)))
    private val card1 = Card(0, "Amazon", Money(25.00), type = CARD_TYPE_GIFT)
    private val card2 = Card(2, "Best Buy", Money(30.00), type = CARD_TYPE_GIFT)
    private val card3 = Card(3, "Google Play", Money(15.00), type = CARD_TYPE_GIFT)


    val cards = ListLiveData(listOf(card0, card1, card2, card3))
    var currentCard = DefaultLiveData(0)

    val card = DefaultLiveData(cards[currentCard.value])
        .also { ld->
        ld.addSource(currentCard){
            ld.value = cards[it]
        }
        ld.addSource(cards){
            ld.value = it[currentCard.value]
        }
    }

    fun addExpense(expense: Expense, pos: Int? = null){
        if (pos != null) {
            cards[currentCard.value].expenses.add(pos, expense)
        }else{
/*            val alreadyIndex = cards[currentCard.value].expenses.indexOfFirst{it.id == expense.id}
            Log.d("addExpense", "alreadyIndex: $alreadyIndex")
            if(alreadyIndex > -1){
                cards[currentCard.value].expenses[alreadyIndex] = expense
            }else{*/
                cards[currentCard.value].expenses.add(expense)
            /*}*/
        }
        cards.refresh()
    }

    fun editExpense(expense: Expense){
        cards.value.forEach { card->
            val index = card.expenses.indexOfFirst { it.id == expense.id }
            if(index > -1){
                card.expenses[index] = expense
            }
        }
        cards.refresh()
    }

    fun removeExpense(expense: Expense){
        cards[currentCard.value].expenses.remove(expense)
        cards.refresh()
    }

    fun findExpenseByID(id: Int): Expense?{
        return cards.value.flatMap { it.expenses }.find { it.id == id }
    }

    fun generateID(): Int{
        val keyList: List<Int> = cards.value.map { it.id } + cards.value.flatMap { it.expenses.map { expense ->  expense.id } }
        var random = Random.nextInt()
        while (keyList.contains(random)){
            random = Random.nextInt()
        }
        return random
    }
}

open class DefaultLiveData<T>(val initialValue: T): MediatorLiveData<T>(){
    override fun getValue(): T {
        return super.getValue() ?: initialValue
    }
}

/**MediatorLiveData of List<T> with better sync to backing list and better modification methods**/
open class ListLiveData<T>(initialList: List<T> = emptyList()) : MediatorLiveData<List<T>>() {
    private val backingList: MutableList<T> = initialList.toMutableList()

    val size
        get() = backingList.size

    operator fun get(pos: Int): T{
        return backingList[pos]
    }

    fun set(pos: Int, item: T){
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
        for(i in 0 until by){
            backingList.removeAt(backingList.size-i)
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
