package com.example.hasgtagmanager.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.example.hasgtagmanager.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ChipHelper(
    val context: Context, private val chipGroup: ChipGroup, private val icon: Int,
    private val iconVisible: Boolean, private val closeIconVisible: Boolean, private val checkable: Boolean) {


    //Create the chip
    private fun chip(title: String): Chip{
        //Create chip and added the atributtes
        val chip = Chip(this.context!!)
        chip.text = title
        chip.chipIcon = ContextCompat.getDrawable(context!!, icon)
        chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.hint_blue))
        chip.isChipIconVisible = iconVisible
        chip.isCloseIconVisible = closeIconVisible
        chip.isCheckable = checkable
        return chip
    }

    //Add the one chip peer time to recyclerView
    fun addChip(title: String){
        //Get created chip
        val chip = chip(title)
        chipGroup?.addView(chip)
        //Close the chip when is clicked
        chip.setOnCloseIconClickListener{
            chipGroup?.removeView(chip as View)
        }
    }

    //Create chips according to a String List
    fun addAllChips(titles: MutableList<String>){
        //Remove current chips
        chipGroup?.removeAllViews()
        //Check if title text is not null
        if (titles!=null){
            for (title:String in titles){
                 //Add the chip to group
                 addChip(title)
            }
        }
    }

    //Remove the chip by title
    fun removeChip(title:String){
        //Declare a list
        val chips: MutableList<Chip> = getChips()
        for(chip in chips){
            //Get chip title
            val compareTitle:String = chip.text.toString()
            //compare the title of the chip in the list is the same of the chip that you want to remove
            if(compareTitle.compareTo(title)==0){
                //Remove the chip from group
                chipGroup?.removeView(chip as View)
            }
        }
    }

    //Return all chips inside in a recyclerView
    fun getChips(): MutableList<Chip> {
        //Declare a list
        val chips:MutableList<Chip> = mutableListOf()
        //Access to the chips in group by index
        for( index in 0 until chipGroup?.childCount!!){
            //Get the chip in group
            val chip:Chip = chipGroup?.getChildAt(index) as Chip
            //Add chips to the list
            chips.add(chip)
        }
        return chips
    }

    fun getSelectedChips(): MutableList<Chip> {
        //Get chips from group
        val chips:MutableList<Chip> = getChips()
        //Declare a list
        val selectedChips:MutableList<Chip> = arrayListOf()
        for( chip in chips){
            //Check if a chip in group si checked
            if(chip.isChecked){
                //Add checked chips into the list
                selectedChips.add(chip)
            }
        }
        return selectedChips
    }
}