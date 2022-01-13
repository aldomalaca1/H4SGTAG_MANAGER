package com.example.hasgtagmanager.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.hasgtagmanager.Interfaces.OnClickHashtagDynamicQueryItemsListener
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.data_models.DynamicHashtagQueryModel
import java.lang.Exception
import java.lang.IllegalArgumentException


class DynamicHashtagQueryAdapter(private var queryItems: MutableList<DynamicHashtagQueryModel>,
                                 val context: Context,
                                 private val onClickQueryItemsListener:
                                 OnClickHashtagDynamicQueryItemsListener): RecyclerView.Adapter<DynamicHashtagQueryAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_hashtag_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get item parameter from list
        var parameter = queryItems?.get(position)
        //Set tag text from the list
        holder.hashtagTag?.text = parameter?.tagText

        //Get tag name from selected view
        val tagName:String = holder.hashtagTag?.text.toString()
        //Get tag label from selected view
        val tagLabel:TextView? = holder.hashtagTag!!

        //Set onclick listeners for view elements
        holder.hashtagTag?.setOnClickListener {
            onClickQueryItemsListener?.OnClickTagLabel(tagName,
                tagLabel!!
            )
        }

        holder.showHashtagsPreview?.setOnClickListener{
            onClickQueryItemsListener.OnClickHashtagPreviewButton(tagName) }

        holder.editHashtags?.setOnClickListener{
            onClickQueryItemsListener.OnClickEditHashtagsButton(tagName)
        }

        holder.deleteHashtags?.setOnClickListener{
            onClickQueryItemsListener.OnClickDeleteHashtagsButton(position, tagName)
        }


    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view)  {
        //Widgets variables
        var hashtagTag: TextView? = null
        var showHashtagsPreview: ImageButton? = null
        var editHashtags: ImageButton? = null
        var deleteHashtags: ImageButton? =null
        var tagContainer: RelativeLayout? = null
        //Get the view elements
        init {
            hashtagTag = view.findViewById(R.id.HashtagTag)
            showHashtagsPreview = view.findViewById(R.id.ShowHashtagsPreview)
            editHashtags = view.findViewById(R.id.EditHashtags)
            deleteHashtags = view.findViewById(R.id.DeleteHashtags)
            tagContainer = view.findViewById(R.id.TagContainer)
        }
    }

    //Set number of items
    override fun getItemCount() = queryItems?.size!!

    fun changeDataSet(newDataSet:MutableList<DynamicHashtagQueryModel>){
         //Set new data to main list
         queryItems = newDataSet
         //Change the internal items by the new ones
         notifyDataSetChanged()
    }

    fun deleteItem(position:Int){
        //Remove item element by position
        queryItems?.removeAt(position)
        //Delete view from recyclerview by position
        notifyItemRemoved(position)
    }


}