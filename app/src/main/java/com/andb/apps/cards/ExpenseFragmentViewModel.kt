package com.andb.apps.cards

import androidx.lifecycle.*
import com.andb.apps.cards.objects.Card
import com.andb.apps.cards.repository.CardRepo


class ExpenseFragmentViewModel : ViewModel() {
    val card: LiveData<Card> = CardRepo.card
    val expenses = Transformations.map(card){
        return@map it.expenses
    }
}

/*
class ExpenseFragmentViewModelFactory(private val position: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExpenseFragmentViewModel(position) as T
    }

}*/
