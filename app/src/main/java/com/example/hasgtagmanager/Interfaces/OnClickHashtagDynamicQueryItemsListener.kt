package com.example.hasgtagmanager.Interfaces

import android.widget.TextView


interface OnClickHashtagDynamicQueryItemsListener {
    fun OnClickTagLabel(tagText: String, tagView: TextView)
    fun OnClickHashtagPreviewButton(tagText: String)
    fun OnClickEditHashtagsButton(tagText: String)
    fun OnClickDeleteHashtagsButton(index:Int, tagText:String)
}