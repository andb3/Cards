package com.andb.apps.cards

import androidx.lifecycle.*
import com.andb.apps.cards.objects.BaseCard
import com.andb.apps.cards.objects.Card
import com.andb.apps.cards.repository.CardRepo


class ExpenseFragmentViewModel : ViewModel() {
    val card: LiveData<BaseCard> = CardRepo.card
    val expenses = CardRepo.expenses
}

/*
class ExpenseFragmentViewModelFactory(private val position: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExpenseFragmentViewModel(position) as T
    }

}*/
