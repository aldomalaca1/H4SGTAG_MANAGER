package com.example.hasgtagmanager.activities

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hasgtagmanager.Decorators.RecyclerViewVerticalSeparator
import com.example.hasgtagmanager.Interfaces.OnClickHashtagDynamicQueryItemsListener
import com.example.hasgtagmanager.adapters.DynamicHashtagQueryAdapter
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.data_models.DynamicHashtagQueryModel
import com.example.hasgtagmanager.data_models.GroupsTableModel
import com.example.hasgtagmanager.data_models.HashtagsTableModel
import com.example.hasgtagmanager.utils.DatabaseHelper
import com.example.hasgtagmanager.utils.ChipHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken


class DynamicHashtagQueryActivity: AppCompatActivity(), OnClickHashtagDynamicQueryItemsListener{

    //Query items
    private var queryItems: MutableList<DynamicHashtagQueryModel>? = mutableListOf()
    //Utils
    private var separateItem: RecyclerViewVerticalSeparator? = null
    private var dbHelper: DatabaseHelper? = null
    //Adapters
    private var dynamicQueryAdapter: DynamicHashtagQueryAdapter? = null
    private var dynamicQueryInstantiatedAdapter: DynamicHashtagQueryAdapter? = null
    //Widgets
    private var searchHashtagsByGroup: TextInputLayout? = null
    private var autoCompleteHashtagsOptions: AutoCompleteTextView? = null
    private var hashtagsList: RecyclerView? =  null
    //Data
    private val options:ArrayList<String> = arrayListOf()
    //Dialogs
    private var updateTagDialog: Dialog? = null
    private var previewHashtagsDialog: Dialog? = null
    private var updateHashtagsDialog: Dialog? = null
    private var deleteWarningDialog: Dialog? = null
    //Json
    private var gson: Gson? = null
    private var jsonParser: JsonParser? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_hashtag_query)
        initComponents()
        widgetsConfiguration()
        events()
    }

    //Init global variables with their respective values
    private fun initComponents(){
          //Utils
          separateItem = RecyclerViewVerticalSeparator(25)
          dbHelper = DatabaseHelper(this)
          //Adapter
          fillQueryItems()
          dynamicQueryAdapter = DynamicHashtagQueryAdapter(queryItems!!,this, this)
          //Widgets
          searchHashtagsByGroup = findViewById(R.id.SearchHashtagsByGroup)
          autoCompleteHashtagsOptions = searchHashtagsByGroup?.findViewById(R.id.AutoCompleteHashtagsOptions)
          hashtagsList = findViewById(R.id.HashtagsList)
          //Options
          fillQueryOptions()
          val optionsAdapter = ArrayAdapter(this,R.layout.item_element_list_drop, options)
          autoCompleteHashtagsOptions?.setAdapter(optionsAdapter)
          //Dialogs
          updateTagDialog = Dialog(this)
          previewHashtagsDialog = Dialog(this)
          updateHashtagsDialog = Dialog(this)
          deleteWarningDialog = Dialog(this)
          //Json
          gson = Gson()
          jsonParser = JsonParser()

    }

    private fun widgetsConfiguration(){
        //Recycler view
        hashtagsList?.hasFixedSize()
        hashtagsList?.layoutManager = LinearLayoutManager(this)
        //Set adapter
        hashtagsList?.adapter = dynamicQueryAdapter
        //Get instantiated adapter
        dynamicQueryInstantiatedAdapter = hashtagsList?.adapter as DynamicHashtagQueryAdapter
        //Set decoration
        hashtagsList?.addItemDecoration(separateItem!!)
        //Autocomplete
        if (options.size!! > 0){autoCompleteHashtagsOptions?.hint = "All"}
        //Dialogs
        updateTagDialog?.setContentView(R.layout.add_tag_dialog)
        previewHashtagsDialog?.setContentView(R.layout.hashtags_preview_dialog)
        updateHashtagsDialog?.setContentView(R.layout.update_hashtags_dialog)
        deleteWarningDialog?.setContentView(R.layout.warning_dialog)

    }

    private fun events(){
        //Set item click listener
        autoCompleteHashtagsOptions?.setOnItemClickListener { adapterView, view, i, l ->
            //Get widgets internal component
            val groupOption:TextView = view.findViewById(R.id.GroupOption)
            //Get option from the 'spinner'
            val groupName = groupOption.text.toString().replace(' ', '-')
            //Change query by the group name
            changeQueryInternalData(groupName)
        }
    }

    //Internal helper functions

    private fun fillQueryItems(){
        //Get data from database
        val outputHashtagsData: MutableList<HashtagsTableModel> = dbHelper?.getAllDataInTableHashtags()!!
        //Check if data exist
        if (outputHashtagsData?.size!! > 0) {
            for (item in outputHashtagsData!!) {
                //Get tag name from data
                val tagName = item.tag.toString().replace('-', ' ')
                //Add the tag name to the items list
                queryItems?.add(DynamicHashtagQueryModel(tagName))
            }
        }else{
            queryItems = ArrayList()
        }

    }

    private fun fillQueryOptions(){
        //Add default option into options array
        options.add("All")

        //Add groups from database
        var groupsDataOutput: MutableList<GroupsTableModel> = dbHelper?.getAllDataInTableGroups()!!
        //Check if data exist
        if (groupsDataOutput?.size!! > 0){
            for (group in groupsDataOutput){
                //Get group name from data
                val groupName:String = group.name!!.replace('-',' ')
                //Add group name into options array
                options.add(groupName)
            }
        }
    }

    //Dialog functions

    private fun openUpdateTagDialog(oldTag:String, tagView: TextView){
        //Open dialog
        updateTagDialog?.show()
        //Get dialog widgets
        var inputTag:EditText? = updateTagDialog?.findViewById(R.id.InputTag)
        val updateTag: ImageButton? = updateTagDialog?.findViewById(R.id.SaveTag)
        //Set click listener
        updateTag?.setOnClickListener {
            //Get new tag
            val newTag:String = inputTag?.text.toString()
            //Save new tag
            saveTagFromUpdateTagDialog(newTag, oldTag,inputTag!!,tagView!!)
        }
    }

    private fun openPreviewHashtagsDialog(tagText:String){
        //Open dialog
        previewHashtagsDialog?.show()
        //Get dialog widgets
        val hashtagsPreview: ChipGroup? = previewHashtagsDialog?.findViewById(R.id.HashtagsPreview)
        val copyHashtags:ImageButton? = previewHashtagsDialog?.findViewById(R.id.CopyHashtags)
        //Declare chipHelper for chipGroup
        val chipHelper: ChipHelper? = ChipHelper(this,hashtagsPreview!!,R.drawable.minihashtag,
                                                 true, false, false)
        //Turn spaces into another character
        val tagName = tagText.replace(' ', '-')
        //Get hashtags table data by tag name
        val hashtagsDataOutput:MutableList<HashtagsTableModel> = dbHelper?.getDataInTableHashtags(tagName)!!
        val hashtagsString:String = hashtagsDataOutput[0].content!!
        //Turn hashtags data into a json text
        val hashtags = gson?.fromJson<MutableList<String>>(hashtagsString, object : TypeToken<MutableList<String>>() {}.type)
        chipHelper?.addAllChips(hashtags as MutableList<String>)

        //Set click listener
        copyHashtags?.setOnClickListener {
            //Copy hashtags to the bin
            copyHashtags(hashtags!!)
            previewHashtagsDialog?.dismiss()
        }
    }

    private fun openUpdateHashtagsDialog(tagText:String){
        //Open Dialog
        updateHashtagsDialog?.show()
        //Get dialog widgets
        val inputHashtags: EditText? = updateHashtagsDialog?.findViewById(R.id.InputNewHashtag)
        val hashtagsPreview: ChipGroup? = updateHashtagsDialog?.findViewById(R.id.HashtagsUpdatePreview)
        val updateHashtags:ImageView? = updateHashtagsDialog?.findViewById(R.id.UpdateHashtags)
        //Declare chipHelper for chipGroup
        val chipHelper: ChipHelper? = ChipHelper(this,hashtagsPreview!!,R.drawable.minihashtag,
            true, true, false)
        //Turn spaces into a character
        val tagName = tagText.replace(' ', '-')
        //Get data from hashtags table
        val hashtagsDataOutput:MutableList<HashtagsTableModel> = dbHelper?.getDataInTableHashtags(tagName)!!
        val hashtagsString:String = hashtagsDataOutput[0].content!!
        //Turn json hashtags into hashtags list
        val hashtags:MutableList<String>? = gson?.fromJson<MutableList<String>>(hashtagsString, object : TypeToken<MutableList<String>>() {}.type)
        //Add all hashtags names into chipGroup as Chips
        chipHelper?.addAllChips(hashtags as MutableList<String>)

        //Set key listener
        inputHashtags?.setOnKeyListener { view, keyCode, keyEvent ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP){
                //Get hashtag name
                val hashtagName:String = inputHashtags?.text.toString().replace(' ','-')
                //Add hashtag into hashtag group
                addChipToChipGroupFromHashtagsDialog(hashtagName,inputHashtags,chipHelper!!)
                return@setOnKeyListener true
            }
            false
        }

        //Set click listener
        updateHashtags?.setOnClickListener {
            //Save hashtags from dialog
            saveHashtagsFromHashtagsDialog(chipHelper!!, tagName)
        }

    }

    private fun  openDeleteWarning(position:Int, tagText:String){
        //Open Dialog
        deleteWarningDialog?.show()
        //Get dialog widgets
        val warningLabel:TextView? = deleteWarningDialog?.findViewById(R.id.DeleteWarningLabel)
        val accept:Button? = deleteWarningDialog?.findViewById(R.id.AcceptDeleteWarning)
        val decline:Button? = deleteWarningDialog?.findViewById(R.id.DeclineDeleteWarning)
        warningLabel?.text = "Want to Delete?"

        //Set click listener for both buttons
        accept?.setOnClickListener {
            //Delete hashtags from db
            deleteHashtagsFromDB(position, tagText)
            deleteWarningDialog?.dismiss()
        }

        decline?.setOnClickListener{
            //Exit from dialog
            deleteWarningDialog?.dismiss()
        }

    }

    //Internal helper functions
    private fun addChipToChipGroupFromHashtagsDialog(hashtagName:String,inputHashtags: EditText,chipHelper: ChipHelper){
        //Check if text is null or empty
        if(!hashtagName.isNullOrEmpty()) {
            //Add chips to chip group
            chipHelper?.addChip(hashtagName)
            //clear text of inputHashtags
            inputHashtags?.text?.clear()
        }else{
            Toast.makeText(this, "No tag on field!!", Toast.LENGTH_SHORT).show();
        }
    }

    private fun saveTagFromUpdateTagDialog(newTag:String, oldTag:String, inputTag:EditText, tagView:TextView){
        //Check if newTag is null or empty
        if (!newTag.isNullOrEmpty()){
            //Change blank spaces with a character
            var newTagName = newTag.replace(' ', '-')
            var oldTagName = oldTag.replace(' ', '-')
            if(!(dbHelper?.tagExistenceChecker(newTagName)!! && oldTagName.compareTo(newTagName)!=0)){
                tagView.text = newTag
                //Update new tag into Hashtags table
                updateTagIntoDB(newTagName, oldTagName!!)
                //Clear input tag text
                inputTag?.text?.clear()
                //Close dialog
                updateTagDialog?.dismiss()
            }else{
                Toast.makeText(updateTagDialog?.context,"Another entry has the same tag!!!",
                    Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(updateTagDialog?.context, "You must to put a tag", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveHashtagsFromHashtagsDialog(chipHelper: ChipHelper, tagName:String){
        //Get chips from chip group
        val chips: MutableList<Chip>? = chipHelper?.getChips()
        //Declare a list of hashtags
        val hashtagsList:MutableList<String> = mutableListOf()

        //Check if list of chips is not empty
        if (chips?.size!! > 0){
            //Add chip names into the array
            for (c in chips){ hashtagsList?.add(c.text.toString()) }
            //Turn hashtags list into a json
            val jsonHashtags:String = gson?.toJson(hashtagsList)!!
            //Update hashtags into the db
            updateHashtags(jsonHashtags!!,tagName)
            updateHashtagsDialog?.dismiss()
        }else{
            Toast.makeText(this, "Text empty!!", Toast.LENGTH_SHORT).show()
        }
    }

    //DB
    private fun updateTagIntoDB(newTag:String, oldTag:String){
        //Set new tag into DB
        dbHelper?.updateDataInTableHashtags(HashtagsTableModel(null,newTag,null,null),oldTag)
        Toast.makeText(this, "Tag updated!!", Toast.LENGTH_SHORT).show()
    }

    private fun updateHashtags(jsonHashtags:String, _tag:String){
        dbHelper?.updateDataInTableHashtags(HashtagsTableModel(null,null,jsonHashtags,null), _tag)
        Toast.makeText(this, "Hashtags updated!!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteHashtagsFromDB(position:Int, tagName:String){
        //Delete item from current position
        dynamicQueryInstantiatedAdapter?.deleteItem(position)
        //Delete hashtags data in db
        dbHelper?.deleteDataInTableHashtags(tagName)
        Toast.makeText(this, "Hashtags deleted!!", Toast.LENGTH_SHORT).show()
    }

    //Other utils
    private fun copyHashtags(hashtags:MutableList<String>){
        var hashtagsString:String? = ""
        var comma:String? = ""
        //Check if hashtags list is not null
        if (hashtags != null){
             for(index in 0 until hashtags.size){
                 //Get tag name from list
                 val tagName = hashtags[index]
                 //Set comma into concatenation if the index is more than 0
                 if(index > 0){ comma = "," }
                 //Create a string line from hashtags names
                 hashtagsString += "$comma #$tagName"
             }
         }
         //Create a clipboardManager
         val clipboard:ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
         //Create a clip data
         val clip = ClipData.newPlainText("hashtags",hashtagsString)
         //Set clip into the clipboard
         clipboard.setPrimaryClip(clip)
         Toast.makeText(this, "copied!!", Toast.LENGTH_SHORT).show()

    }

    private fun changeQueryInternalData(groupName:String){
        //Open Dialog
        queryItems?.clear()

        //val adapter: DynamicHashtagQueryAdapter = hashtagsList?.adapter as DynamicHashtagQueryAdapter
        var hashtagsDataOutput: MutableList<HashtagsTableModel>? = null

        //Check if the default option is selected
        if(groupName.compareTo("All")==0){
            //Fill list with all data from hashtags table
            hashtagsDataOutput = dbHelper?.getAllDataInTableHashtags()
        }else{
            //Fill list from hashtags data by group name
            hashtagsDataOutput = dbHelper?.getHashtagsByGroup(groupName)
        }

        for (item in hashtagsDataOutput!!){
            //Get tag name from hashtags list
            val tagName = item.tag
            //Add tag name into query items list
            queryItems?.add(DynamicHashtagQueryModel(tagName!!))
        }

        //Change the data set using for the query items list
        dynamicQueryInstantiatedAdapter?.changeDataSet(queryItems!!)
    }

    //Interface methods for query elements
    override fun OnClickTagLabel(tagText: String, tagView: TextView) {
        openUpdateTagDialog(tagText, tagView)
    }

    override fun OnClickHashtagPreviewButton(tagText:String) {
        openPreviewHashtagsDialog(tagText)
    }

    override fun OnClickEditHashtagsButton(tagText:String) {
        openUpdateHashtagsDialog(tagText)
    }

    override fun OnClickDeleteHashtagsButton(position:Int, tagText:String) {
        openDeleteWarning(position, tagText)
    }
}


