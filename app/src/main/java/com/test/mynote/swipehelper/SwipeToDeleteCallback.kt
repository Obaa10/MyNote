package com.test.mynote.swipehelper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.test.mynote.MainActivity
import com.test.mynote.R
import com.test.mynote.viewmodel.NoteViewModel

abstract class SwipeToDeleteCallback(
    val context: Context,
    val noteViewModel: NoteViewModel,
    val activity: MainActivity
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val backgroundColor = Color.parseColor("#f44336")
    private val background = ColorDrawable()
    var deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_24)!!
    private val intrinsicWidth = deleteIcon.intrinsicWidth
    private val intrinsicHeight = deleteIcon.intrinsicHeight

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        noteViewModel.isCompleted.observe(activity) {
            if (it)
                deleteIcon =
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_filter_alt_24)!!
        }
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top - 40
        // Draw the red delete background
        background.color = backgroundColor
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top + 40,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        // Calculate position of delete icon
        val iconTop = itemView.top + 40 + (itemHeight - intrinsicHeight) / 2
        val iconMargin = (itemHeight - intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + intrinsicHeight

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        deleteIcon.draw(c)


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}