package com.andb.apps.cards.utils

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

fun RecyclerView.swipeWith(block: Swiper.() -> Unit) {
    val callback = Swiper()
    callback.apply(block)
    ItemTouchHelper(callback).attachToRecyclerView(this)
}

class Swiper : ItemTouchHelper.Callback() {
    var swipeX = 0f
    var swipeThreshold: (RecyclerView.ViewHolder) -> Float = { vh -> vh.itemView.width.toFloat() + 1f }

    private val left = SwipeDirection(SwipeDirection.MULTIPLIER_LEFT)
    private val right = SwipeDirection(SwipeDirection.MULTIPLIER_RIGHT)

    fun left(block: SwipeDirection.() -> Unit) {
        left.apply(block)
        left.list.sortBy { it.endX }
    }

    fun right(block: SwipeDirection.() -> Unit) {
        right.apply(block)
        right.list.sortBy { it.endX }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) =
        false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //TODO: parametrize swipe vs return
        (viewHolder.itemView.parent as RecyclerView).adapter?.notifyItemChanged(viewHolder.adapterPosition)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val direction = if (swipeX > 0) ItemTouchHelper.RIGHT else ItemTouchHelper.LEFT
        when (direction) {
            ItemTouchHelper.LEFT -> {
                Log.d("ith", "clearView direction: left, swipeX: $swipeX > threshold: ${left.threshold}?")
                if (abs(swipeX) >= left.threshold) {
                    Log.d("ith", "swipe triggered left, swipeX: $swipeX, options: ${left.list.map { it.endX }} ")
                    val currentStep = left.list.filter { abs(swipeX) <= it.endX }.minBy { it.endX }
                    currentStep?.action?.invoke(viewHolder.adapterPosition)
                }
            }
            ItemTouchHelper.RIGHT -> {
                Log.d("ith", "clearView direction: right")
                if (swipeX >= right.threshold) {
                    val currentStep = right.list.filter { swipeX <= it.endX }.minBy { it.endX }
                    currentStep?.action?.invoke(viewHolder.adapterPosition)
                }
            }
        }

        super.clearView(recyclerView, viewHolder)
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 400
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dXIn: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val direction = if (dXIn > 0) right else left

        //get friction-adjusted x-value if not bigger than max for this direction
        val dXNew = min(
            abs(dXIn * direction.friction),
            direction.list.map { it.endX }.maxBy { it }?.toFloat() ?: 0f
        )

        val step = direction.list.filter { dXNew <= it.endX }.minBy { it.endX }
            ?: throw Exception("If this line is called, dXNew should be below or equal for minBy (dXNew = $dXNew, steps are ${right.list.map { it.endX }})")

        createBackgroundDrawable(step.colorFun(dXNew), viewHolder.itemView).draw(c)

        step.getBoundedIcon(viewHolder.itemView, dXNew, if(dXIn > 0) ItemTouchHelper.RIGHT else ItemTouchHelper.LEFT)?.draw(c)

        if (isCurrentlyActive) {
            swipeX = dXNew * direction.multiplier
        }
        //Log.d("ith", "swipeX: $swipeX")

        super.onChildDraw(c, recyclerView, viewHolder, dXNew * direction.multiplier, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold(viewHolder)
    }

    private fun createBackgroundDrawable(color: Int, itemView: View): GradientDrawable {
        val background = GradientDrawable()
        background.setColor(color)
        itemView.apply {
            background.setBounds(left, top, right, bottom)
        }
        return background
    }

    //defines the enabled move directions in each state (idle, swiping, dragging).
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeFlag(
            ItemTouchHelper.ACTION_STATE_SWIPE,
            getDirectionFlags()
        )
    }

    private fun getDirectionFlags(): Int {
        val left = left.list.isNotEmpty()
        val right = right.list.isNotEmpty()
        return when {
            left && right -> ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            left -> ItemTouchHelper.LEFT
            right -> ItemTouchHelper.RIGHT
            else -> 0
        }
    }
}

class SwipeStep(val endX: Int) {

    companion object {
        const val SIDE_EDGE = 7834
        const val SIDE_VIEW = 8283
    }

    var colorFun: (dX: Float) -> Int = { Color.BLACK }
    var color: Int = Color.BLACK
        set(value) {
            field = value
            colorFun = { value }
        }

    fun color(value: (Float) -> Int) {
        colorFun = value
    }

    var icon: Drawable? = null
    var iconColor = Color.WHITE
    var action: ((Int) -> Unit)? = null
    var marginSide = dpToPx(16)
    var side = SIDE_EDGE
    var iconPositioning: (View, Float, Int) -> Drawable? = { itemView, dX, direction ->
        icon?.mutate()?.also { icon ->
            icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP)
            val top = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
            val bottom = top + icon.intrinsicHeight
            when (direction) {
                ItemTouchHelper.RIGHT -> {
                    when (side) {
                        SIDE_EDGE -> {
                            val left = itemView.left + marginSide
                            icon.setBounds(left, top, left + icon.intrinsicWidth, bottom)
                        }
                        else -> {
                            val right = (itemView.left + dX - marginSide).toInt()
                            icon.setBounds(right - icon.intrinsicWidth, top, right, bottom)
                        }
                    }
                }
                else -> {
                    when (side) {
                        SIDE_EDGE -> {
                            val right = itemView.right - marginSide
                            icon.setBounds(right - icon.intrinsicWidth, top, right, bottom)
                        }
                        else -> {
                            val left = (itemView.right - dX + marginSide).toInt()
                            icon.setBounds(left, top, left + icon.intrinsicWidth, bottom)
                        }
                    }
                }
            }
        }
    }


    fun getBoundedIcon(itemView: View, dX: Float, direction: Int): Drawable? {
        return iconPositioning.invoke(itemView, dX, direction)
    }
}

class SwipeDirection(val multiplier: Int) {
    val list = mutableListOf<SwipeStep>()

    /**Set the view to either be swiped across the screen when an action is run or to return to its original position**/
    var fullySwipeable: (Int) -> Boolean = { true }

    /**Length (in dp) a swipe needs to travel to trigger the action**/
    var threshold = dpToPx(16)
        set(value) {
            field = dpToPx(value)
        }
    var friction = 1f

    /**Add a step within the swipe, going from whatever the next lowest step position is until the position (in dp) of this**/
    fun step(pos: Int, block: SwipeStep.() -> Unit) {
        val step = SwipeStep(dpToPx(pos))
        step.apply(block)
        list.add(step)
    }

    /**Add a step within the swipe, going from the highest step position is until the end of the swipe**/
    fun endStep(block: SwipeStep.() -> Unit) {
        val step = SwipeStep(Resources.getSystem().displayMetrics.widthPixels)
        step.apply(block)
        list.add(step)
    }

    companion object {
        const val MULTIPLIER_LEFT = -1
        const val MULTIPLIER_RIGHT = 1
    }
}