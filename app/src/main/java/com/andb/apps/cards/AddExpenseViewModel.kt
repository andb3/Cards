package com.andb.apps.cards

import androidx.lifecycle.ViewModel
import com.andb.apps.cards.objects.EXPENSE_TYPE_OTHER
import com.andb.apps.cards.objects.Expense
import com.andb.apps.cards.objects.Money
import com.andb.apps.cards.repository.CardRepo
import java.math.RoundingMode

class AddExpenseViewModel : ViewModel() {
    var id = CardRepo.generateID()
    var amount = Money(0.00)
    var category: Int = EXPENSE_TYPE_OTHER
    var parentID: Int = CardRepo.card.value.id
    var editing = false

    fun isEmpty(): Boolean = amount.value.setScale(2, RoundingMode.HALF_UP).toDouble()==0.00

    fun edit(id: Int){
        val expense = CardRepo.findExpenseByID(id)
        this.id = id
        expense?.apply {
            this@AddExpenseViewModel.amount = amount
            category = type
            this@AddExpenseViewModel.parentID = parentID
        }
        editing = true
    }

    fun save() {
        val expense = Expense(id, category, amount, parentID)
        if(editing){
            CardRepo.editExpense(expense)
        }else{
            CardRepo.addExpense(expense)
        }
    }
}
