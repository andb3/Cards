package com.andb.apps.cards.objects

import androidx.room.*
import com.andb.apps.cards.R

const val EXPENSE_TYPE_OTHER = 100
const val EXPENSE_TYPE_FOOD = 101
const val EXPENSE_TYPE_TRANSPORT = 102

@Entity(foreignKeys = [ForeignKey(entity = BaseCard::class,
    parentColumns = ["card_id"],
    childColumns = ["expense_parentID"],
    onDelete = ForeignKey.CASCADE)])
data class Expense(
    @PrimaryKey
    @ColumnInfo(name = "expense_id")
    val id: Int,
    @ColumnInfo(name = "expense_type")
    var type: Int,
    @ColumnInfo(name = "expense_amount")
    var amount: Money,
    @ColumnInfo(name = "expense_parentID", index = true)
    val parentID: Int,
    @ColumnInfo(name = "expense_index")
    var index: Int = -1) {

    fun typeIconRes(): Int {
        return typeIconRes(type)
    }

    fun typeNameRes(): Int {
        return typeNameRes(type)
    }

    companion object {
        fun typeIconRes(type: Int): Int {
            return when (type) {
                EXPENSE_TYPE_FOOD -> R.drawable.ic_restaurant_black_24dp
                EXPENSE_TYPE_TRANSPORT -> R.drawable.ic_transportation_black_24dp
                else -> R.drawable.ic_money_black_24dp
            }
        }

        fun typeNameRes(type: Int): Int {
            return when (type) {
                EXPENSE_TYPE_FOOD -> R.string.category_food
                EXPENSE_TYPE_TRANSPORT -> R.string.category_transport
                else -> R.string.category_other
            }
        }
    }
}