package com.example.hasgtagmanager.Decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewVerticalSeparator(val spaceSize: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        //Get rect from recyclerview
        outRect: Rect, view: View,
       //Get parent of recyclerview
        parent: RecyclerView,
        //Get state of recycler view
        state: RecyclerView.State
    )
    {
        with(outRect) {
            //Check if the views inside the recycler view exist
            if (parent.getChildAdapterPosition(view) == 0) {
                //Set a space size to the top
                top = spaceSize
            }
            //Set space size to the bottom
            bottom = spaceSize
        }
    }
}