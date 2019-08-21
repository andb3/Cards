package com.andb.apps.cards.objects

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

data class Money(val value: BigDecimal, val currency: Currency = Currency.getInstance(Locale.getDefault())) {

    constructor(value: Double, currency: Currency = Currency.getInstance(Locale.getDefault())) : this(BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP), currency)

    operator fun plus(money: BigDecimal): Money = this.copy(value = this.value + money)

    operator fun plus(money: Money) = this.doWhenCompatible(money) { this + it.value }

    operator fun minus(money: BigDecimal): Money = this.copy(value = this.value - money)

    operator fun minus(money: Money) = this.doWhenCompatible(money) { this + it.value }

    operator fun times(money: BigDecimal): Money = this.copy(value = this.value * money)

    operator fun times(money: Money) = this.doWhenCompatible(money) { this * it.value }

    private fun <T> doWhenCompatible(money: Money, block: (Money) -> T): T {
        return if (this.currency != money.currency)
            throw IncompatibleCurrencyException("Currencies are not compatible: ${this.currency} ${money.currency}")
        else block(money)
    }

    override fun toString(): String {
        return "$value $currency"
    }
}

class IncompatibleCurrencyException(override val message: String) : RuntimeException()

class MoneyTypeConverter {

    @TypeConverter
    fun stringToMoney(data: String?): Money {
        return Money(data?.toDouble() ?: 0.00)
    }

    @TypeConverter
    fun moneyToString(money: Money): String {
        return money.value.toString()
    }
}