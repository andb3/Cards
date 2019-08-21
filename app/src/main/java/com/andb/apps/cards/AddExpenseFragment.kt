package com.andb.apps.cards

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.andb.apps.cards.objects.Expense
import com.andb.apps.cards.objects.Money
import com.github.rongi.klaster.Klaster
import kotlinx.android.synthetic.main.add_expense_fragment.*
import kotlinx.android.synthetic.main.category_item.view.*


class AddExpenseFragment : Fragment() {

    private lateinit var viewModel: AddExpenseViewModel
    private val categoryAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> by lazy { categoryAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_expense_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddExpenseViewModel::class.java)

        val expenseID = arguments?.getInt("expenseID", -1) ?: -1
        if(expenseID != -1){
            viewModel.edit(expenseID)
            addExpenseAmount.setText(viewModel.amount.value.toEngineeringString())
        }

        addExpenseSymbol.text = viewModel.amount.currency.symbol

        addExpenseAmount.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.amount = Money(
                    p0.toString().toDoubleOrNull() ?: 0.00
                )
                if(viewModel.isEmpty()){
                    addExpenseDone.alpha = .54f
                }else{
                    addExpenseDone.alpha = 1f
                }
            }

        })

        addExpenseDone.setOnClickListener {
            if(!viewModel.isEmpty()){
                viewModel.save()
                requireActivity().supportFragmentManager.popBackStack()
                (activity as MainActivity).showBottomBar()
            }
        }

        addExpenseCategoryRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

    }

    private fun categoryAdapter() = Klaster.get()
        .itemCount { 3 }
        .view(R.layout.category_item, layoutInflater)
        .bind { pos->
            val categoryID = pos + 100
            itemView.apply {
                categoryItemType.setText(Expense.typeNameRes(categoryID))

                if(categoryID==viewModel.category){
                    categoryItemIconBackground.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    categoryItemIcon.setImageResource(R.drawable.ic_done_black_24dp)
                    categoryItemIcon.setColorFilter(Color.WHITE)
                }else{
                    categoryItemIconBackground.color = ContextCompat.getColor(context, R.color.iconBackground)
                    categoryItemIcon.setImageResource(Expense.typeIconRes(categoryID))
                    categoryItemIcon.setColorFilter(Color.BLACK)
                }

                setOnClickListener {
                    val oldCategoryID = viewModel.category
                    viewModel.category = categoryID
                    Log.d("categoryIDPersistence", "oldID: $oldCategoryID, newID: ${viewModel.category}")
                    categoryAdapter.notifyItemChanged(oldCategoryID-100)

                    categoryItemIconBackground.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    categoryItemIcon.setImageResource(R.drawable.ic_done_black_24dp)
                    categoryItemIcon.setColorFilter(Color.WHITE)
                }
            }
        }
        .build()

}
