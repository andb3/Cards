package com.andb.apps.cards

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andb.apps.cards.objects.Expense
import com.andb.apps.cards.objects.Money
import com.andb.apps.cards.repository.CardRepo
import com.andb.apps.cards.utils.SwipeStep
import com.andb.apps.cards.utils.observe
import com.andb.apps.cards.utils.swipeWith
import com.github.rongi.klaster.Klaster
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.expense_fragment.*
import kotlinx.android.synthetic.main.expense_item.view.*

@SuppressLint("SetTextI18n")
class ExpenseFragment : Fragment() {

    private lateinit var viewModel: ExpenseFragmentViewModel
    private val expenseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> by lazy { expenseAdapter() }

    var name: String = ""
    var cardId = -1
    var balance: Money = Money(0.00)
    var total: Money = Money(0.00)
    private var expenses: List<Expense> = listOf()
    private var iconRes: Int = R.drawable.ic_card_generic_black_24dp

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(ExpenseFragmentViewModel::class.java)
        viewModel.card.observe(viewLifecycleOwner){
            name = it.name
            balance = it.balance()
            total = it.amount
            iconRes = it.getDrawableID()

            refreshBalance()
            refreshCard()

            if (it.id == cardId) {//diffutil
                val diff = DiffUtil.calculateDiff(ExpenseDiffCallback(expenses, it.expenses))
                expenses = it.expenses.toList()
                diff.dispatchUpdatesTo(expenseAdapter)
            } else {
                expenses = it.expenses.toList()
                expenseAdapter.notifyDataSetChanged()
                expenseRecycler.scheduleLayoutAnimation()
            }
            cardId = it.id

        }
/*        viewModel.expenses.observe(viewLifecycleOwner){

        }*/

        return inflater.inflate(R.layout.expense_fragment, container, false)
    }



    class ExpenseDiffCallback(private val oldList: List<Expense>, private val newList: List<Expense>) :
        DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshBalance()

        expenseRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
            swipeWith {
                left {
                    step(124) {
                        color = ContextCompat.getColor(context, R.color.colorPrimary)
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_edit_black_24dp)
                        side = SwipeStep.SIDE_VIEW
                        action = { pos ->
                            (activity as MainActivity).apply {
                                val id = card.expenses[pos].id
                                showAddExpenseFragment(id)
                            }


                        }
                    }
                    endStep {
                        color = Color.RED
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp)
                        side = SwipeStep.SIDE_VIEW
                        action = { pos ->

                            val expense = expenses[pos]
                            CardRepo.removeExpense(expense)
                            Snackbar.make(expenseRecycler, "Deleted", Snackbar.LENGTH_LONG)
                                .setAction("Undo") {
                                    CardRepo.addExpense(expense, pos)
                                }.show()
                        }
                    }
                }
            }
        }
    }

    private fun refreshBalance() {

        expenseCurrentBalance.text = "${balance.currency.symbol}${balance.value}"
        expenseOriginalBalance.text = "/ ${total.currency.symbol}${total.value}"
        val newMax = (total.value * 100.toBigDecimal()).toInt()
        val newProgress = (balance.value * 100.toBigDecimal()).toInt()
        ObjectAnimator.ofInt(expenseProgress, "max", expenseProgress.max, newMax)
            .also { it.duration = 250 }.start()
        ObjectAnimator.ofInt(expenseProgress, "progress", expenseProgress.progress, newProgress)
            .also { it.duration = 250 }.start()

    }

    private fun refreshCard() {
        expenseCardIcon.setImageResource(iconRes)
        expenseCardName.text = name
    }

    private fun expenseAdapter() = Klaster.get()
        .itemCount { expenses.size }
        .view(R.layout.expense_item, layoutInflater)
        .bind { pos ->
            val expense = expenses[pos]
            itemView.apply {
                expenseItemAmount.text = "- ${expense.amount.currency.symbol}${expense.amount.value}"
                expenseItemType.setText(expense.typeNameRes())
                expenseItemIcon.setImageResource(expense.typeIconRes())
            }

        }
        .build()

}
