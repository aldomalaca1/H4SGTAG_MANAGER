package com.example.hasgtagmanager.activities

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.hasgtagmanager.utils.ChipHelper
import com.example.hasgtagmanager.data_models.HashtagsTableModel
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.utils.DatabaseHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson


//Widgets
private var hashtagsGroup: ChipGroup? = null
private var addHashtagInput : EditText? = null
private var saveHashtags: ImageButton? = null
//Extra
var gson: Gson?=null
//Utils
private var chipHelper: ChipHelper? = null
private var dbHelper: DatabaseHelper? = null
//Dialogs
private var addTagDialog: Dialog? = null
//Flags
private var tagAdded:Boolean = false
//Data
private var tag:String? = null
private val hashtags: MutableList<String> = mutableListOf()

class AddHashtagsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_hashtags)
        initComponents()
        widgetsConfiguration()
        events()
    }

    //Init global variables with their respective values
    private fun initComponents(){
        //Widgets
         hashtagsGroup = findViewById(R.id.HashtagsGroup)
         addHashtagInput = findViewById(R.id.AddHashtagInput)
         saveHashtags = findViewById(R.id.SaveHashtags)
        //Utils
         chipHelper = ChipHelper(this, hashtagsGroup!!, R.drawable.minihashtag,
                                true, true, false)
         dbHelper = DatabaseHelper(this)
        //Extra
         gson = Gson()
        //Dialogs
         addTagDialog = Dialog(this)
    }

    //Set necessary configuration values to the objets that needed
    private fun widgetsConfiguration(){
        //Dialogs
        addTagDialog?.setContentView(R.layout.add_tag_dialog)
        addTagDialog?.setCancelable(false)
        addTagDialog?.window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.stroke_shape_bg))
    }

    //Events of widgets from this view
    private fun events(){
       addHashtagInput?.setOnKeyListener { view, keyCode, keyEvent ->
           if(keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP){
               val hashtagName:String = addHashtagInput?.text.toString().replace(' ','-')
               addChipToChipGroup(hashtagName)
               return@setOnKeyListener true
           }
          false
       }

        saveHashtags?.setOnClickListener {
            saveHashtags()
        }

    }

    //Internal helper functions
    private fun addChipToChipGroup(hashtagName:String){
        if(!hashtagName.isNullOrEmpty()) {
            chipHelper?.addChip(hashtagName)
            addHashtagInput?.text?.clear()
        }else{
            Toast.makeText(this, "Text is empty", Toast.LENGTH_SHORT).show();
        }
    }
    private fun saveHashtags(){
        //Clear list
        hashtags.clear()
        val chips: MutableList<Chip>? = chipHelper?.getChips()
        if (chips?.size!! > 0) {
           if(!tagAdded) {
               openAddTagDialog()
           }
           else {
               //Save the data bellow
               hashtagsGroup?.removeAllViews()
               tagAdded = false
               for (c in chips){ hashtags.add(c.text.toString()) }
               val jsonHashtags:String? = gson?.toJson(hashtags)
               writeHashtagsIntoDB( jsonHashtags!!)
           }
        }else{
            Toast.makeText(this, "No hashtags added", Toast.LENGTH_SHORT).show()
        }
    }

    //Dialogs
    private fun openAddTagDialog(){
        addTagDialog?.show()
        val inputTag: EditText? = addTagDialog?.findViewById(R.id.InputTag)
        val saveTag: ImageButton? = addTagDialog?.findViewById(R.id.SaveTag)

        saveTag?.setOnClickListener{
            tag = inputTag?.text.toString()
            if(!tag.isNullOrBlank()){
                tag = tag?.replace(' ','-')
                if(!dbHelper?.tagExistenceChecker(tag!!)!!){
                    inputTag?.text?.clear()
                    tagAdded=true
                    addTagDialog?.dismiss()
                }else{
                    Toast.makeText(addTagDialog?.context, "Tag already exist!!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(addTagDialog?.context, "No tag!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //DB
    private fun writeHashtagsIntoDB(jsonHashtags:String){
        dbHelper?.insertDataInTableHashtags(HashtagsTableModel(null, tag!!,jsonHashtags,0))
        Toast.makeText(this,"Hashtags saved", Toast.LENGTH_SHORT).show()
    }

    //Action bar from footer
    override fun onBackPressed() {
       super.onBackPressed()
        tagAdded = false
    }

}