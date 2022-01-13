package com.example.hasgtagmanager.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.hasgtagmanager.Interfaces.OnClickRecyclerViewItemListener
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.data_models.MainMenuModel


class MainMenuAdapter(private val menuOptions: MutableList<MainMenuModel>,
                      val context: Context,
                      private val onClickRecyclerViewItemListener: OnClickRecyclerViewItemListener): RecyclerView.Adapter<MainMenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_menu_option, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get item parameter from list
        var parameter = menuOptions[position]

        holder.mainMenuIcon?.setImageResource(parameter.optionIconID)
        holder.mainMenuOptionText?.text = parameter.optionLabelText

        //Get option
        val option:String = holder.mainMenuOptionText?.text.toString()

        holder.itemView.setOnClickListener {onClickRecyclerViewItemListener?.
        OnClickElement(option)}
    }

   inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)  {
        //Widgets variables
        var mainMenuIcon: ImageView? = null
        var mainMenuOptionText: TextView? = null
        //Get the view elements
        init {
            mainMenuIcon = view.findViewById(R.id.MainMenuIcon)
            mainMenuOptionText = view.findViewById(R.id.MainMenuOptionText)
        }
    }

    //Set number of items
    override fun getItemCount() = menuOptions.size
}