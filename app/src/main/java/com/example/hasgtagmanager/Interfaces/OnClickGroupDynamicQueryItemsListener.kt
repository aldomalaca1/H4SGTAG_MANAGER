package com.example.hasgtagmanager.Interfaces

import android.widget.TextView

interface OnClickGroupDynamicQueryItemsListener {
    fun OnClickGroupLabel(groupText:String, textView:TextView)
    fun OnClickLinkGroupToHashtags(groupText:String)
    fun OnClickDeleteGroup(groupText:String, index:Int)
}