package com.andb.apps.cards.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andb.apps.cards.R

@Entity
open class BaseCard(
    @PrimaryKey
    @ColumnInfo(name = "card_id")
    val id: Int,
    @ColumnInfo(name = "card_name")
    var name: String,
    @ColumnInfo(name = "card_amount")
    var amount: Money,
    @ColumnInfo(name = "card_type")
    var type: Int = CARD_TYPE_GENERIC
) {

    fun getDrawableID(): Int {
        return when (type) {
            CARD_TYPE_GIFT -> R.drawable.ic_card_gift_black_24dp
            else -> R.drawable.ic_card_generic_black_24dp
        }
    }
}