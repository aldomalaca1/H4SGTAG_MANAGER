package com.example.hasgtagmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hasgtagmanager.Interfaces.OnClickGroupDynamicQueryItemsListener
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.data_models.GroupsTableModel

class DynamicGroupQueryAdapter(
    private val queryItems: MutableList<GroupsTableModel>,
    val context: Context,
    private val onClickGroupDynamicQueryItemsListener: OnClickGroupDynamicQueryItemsListener):
    RecyclerView.Adapter<DynamicGroupQueryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DynamicGroupQueryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_group_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DynamicGroupQueryAdapter.ViewHolder, position: Int) {
        var parameter = queryItems?.get(position)

        //Set data
        holder.groupText?.text = parameter.name

        // Get group name from selected view
        val groupName:String = holder.groupText?.text.toString()
        //Get group label from view
        val groupLabel:TextView? = holder.groupText!!

        //Set onclick listeners for view elements
        holder.groupText?.setOnClickListener {
            onClickGroupDynamicQueryItemsListener.OnClickGroupLabel(groupName,
            groupLabel!!) }

        holder.linkGroupToHashtags?.setOnClickListener {
            onClickGroupDynamicQueryItemsListener.OnClickLinkGroupToHashtags(groupName) }

        holder.deleteGroup?.setOnClickListener {
            onClickGroupDynamicQueryItemsListener.OnClickDeleteGroup(groupName, position) }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Widgets variables
        var groupText:TextView?=null
        var linkGroupToHashtags:ImageButton?=null
        var deleteGroup:ImageButton?=null
        //Get the view elements
        init {
            groupText = view.findViewById(R.id.GroupText)
            linkGroupToHashtags = view.findViewById(R.id.LinkGroupToHashtags)
            deleteGroup = view.findViewById(R.id.DeleteGroup)
        }
    }
    //Set number of items
    override fun getItemCount() = queryItems?.size!!

    fun deleteItem(position: Int) {
        //Remove item element by position
        queryItems?.removeAt(position)
        //Delete view from recyclerview by position
        notifyItemRemoved(position)
    }


}