package com.andb.apps.cards.objects

import androidx.room.Entity
import androidx.room.Relation
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.andb.apps.cards.R
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

const val CARD_TYPE_GENERIC = 0
const val CARD_TYPE_GIFT = 1

class Card(id: Int,
           name: String,
           amount: Money,
           val expenses: ArrayList<Expense> = ArrayList(),
           type: Int = CARD_TYPE_GENERIC) : BaseCard(id, name, amount, type) {

    fun balance() = Money(amount.value - expenseTotal())

    fun expenseTotal() = expenses.sumBy { it.amount }

}

fun Collection<Expense>.sumBy(block: (Expense) -> Money): BigDecimal {
    var sum = Money(BigDecimal(0), Currency.getInstance(Locale.getDefault()))
    this.forEach {
        sum += block.invoke(it)
    }
    return sum.value
}