package com.example.hasgtagmanager.activities

import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hasgtagmanager.Decorators.RecyclerViewVerticalSeparator
import com.example.hasgtagmanager.Interfaces.OnClickGroupDynamicQueryItemsListener
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.adapters.DynamicGroupQueryAdapter
import com.example.hasgtagmanager.data_models.GroupsTableModel
import com.example.hasgtagmanager.data_models.HashtagsTableModel
import com.example.hasgtagmanager.utils.ChipHelper
import com.example.hasgtagmanager.utils.DatabaseHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


//Widgets
private var groupsList: RecyclerView? = null
//Utils
private var separateItem: RecyclerViewVerticalSeparator? = null
private var dbHelper: DatabaseHelper? = null
//Adapters
private var dynamicQueryGroupAdapter: DynamicGroupQueryAdapter?=null
private var dynamicQueryInstantiatedAdapter: DynamicGroupQueryAdapter? = null
//Dialogs
private var updateGroupDialog:Dialog?=null
private var linkGroupToHashtagsDialog:Dialog?=null
private var deleteWarningDialog:Dialog?=null
//Data
private var queryItems:MutableList<GroupsTableModel> = mutableListOf()

class DynamicGroupQueryActivity: AppCompatActivity(), OnClickGroupDynamicQueryItemsListener {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_group_query)
        initComponents()
        widgetsConfiguration()
    }

    private fun initComponents(){
        //Widgets
        groupsList = findViewById(R.id.GroupsList)
        //Utils
        separateItem = RecyclerViewVerticalSeparator(25)
        dbHelper = DatabaseHelper(this)
        //Fill items
        fillQueryItems()
        //Adapters
        dynamicQueryGroupAdapter = DynamicGroupQueryAdapter(queryItems,this,this)
        //Dialogs
        updateGroupDialog = Dialog(this)
        linkGroupToHashtagsDialog = Dialog(this)
        deleteWarningDialog = Dialog(this)

    }

    private  fun widgetsConfiguration(){
        //Recycler view
        groupsList?.hasFixedSize()
        groupsList?.layoutManager = LinearLayoutManager(this)
        //Set adapter to recycler view
        groupsList?.adapter = dynamicQueryGroupAdapter!!
        dynamicQueryInstantiatedAdapter = groupsList?.adapter as DynamicGroupQueryAdapter
        //Set decorator
        groupsList?.addItemDecoration(separateItem!!)
        //Set dialogs
        updateGroupDialog?.setContentView(R.layout.update_group_dialog)
        linkGroupToHashtagsDialog?.setContentView(R.layout.link_group_to_hashtags_dialog)
        deleteWarningDialog?.setContentView(R.layout.warning_dialog)
    }

    //Fill
    private fun fillQueryItems(){
        //Clear groups list
        queryItems.clear()

        //Get data from database
        var hashtagsDataOutput: MutableList<GroupsTableModel>? = dbHelper?.getAllDataInTableGroups()

        //Check if the data is not empty
        if (hashtagsDataOutput?.size!! > 0){
            for(item in hashtagsDataOutput){
                //Get the group name from data list
                val groupName = item.name?.replace('-',' ')
                //Check if the group name is not the main
                val isMain:Boolean = dbHelper?.isMainGroup(groupName!!)!!
                if (!isMain) {
                    queryItems.add(GroupsTableModel(null,groupName))
                }else{
                    //Return a empty array list if is the main
                    queryItems = ArrayList()
                }
            }
        }else{
            //Return an empty array list if is empty
            queryItems = ArrayList()
        }
    }

    //Dialogs
    private fun openUpdateGroupDialog(oldGroup:String, textView: TextView){
        //Open dialog
        updateGroupDialog?.show()
        //Get dialog widgets
        val inputGroup:EditText? = updateGroupDialog?.findViewById(R.id.InputNewGroup)
        val updateGroup:ImageButton? = updateGroupDialog?.findViewById(R.id.UpdateGroup)
        //Set click listener
        updateGroup?.setOnClickListener {
            //Get new group name
            var newGroup:String = inputGroup?.text.toString()
            //Save group name
            saveGroupNameFromUpdateGroupDialog(newGroup,oldGroup,textView,inputGroup!!)
        }
    }

    private fun openLinkGroupToHashtagsDialog(groupText:String){
        //Open dialog
        linkGroupToHashtagsDialog?.show()
        //Get dialog widgets
        val noAssignedHashtags:ChipGroup? = linkGroupToHashtagsDialog?.findViewById(R.id.NoAssignedHashtags)
        val assignedHashtags:ChipGroup? = linkGroupToHashtagsDialog?.findViewById(R.id.AssignedHashtags)
        val setHashtagsToGroup: ImageButton? = linkGroupToHashtagsDialog?.findViewById(R.id.SetHashtagsToGroup)
        val generalToGroup:ImageButton? = linkGroupToHashtagsDialog?.findViewById(R.id.GeneralToGroup)
        val groupToGeneral:ImageButton? = linkGroupToHashtagsDialog?.findViewById(R.id.GroupToGeneral)
        //Internal variables
        //Chip helper for the first chip group
        val chipHelperForAssignedToGeneral: ChipHelper? = ChipHelper(this, noAssignedHashtags!!, R.drawable.ic_select,
        false, false,true)
        //Chip helper for the second chip group
        val chipHelperForAsssignedToGroup: ChipHelper? = ChipHelper(this, assignedHashtags!!, R.drawable.ic_select,
            false, false,true)
        //Get data from group general/main
        val dataInGeneral:MutableList<HashtagsTableModel> = dbHelper?.getHashtagsByGroup("General")!!
        //Get group name
        val groupName = groupText?.replace(' ', '-')
        //Get data from hashtags by the group name
        val dataInGroup: MutableList<HashtagsTableModel> = dbHelper?.getHashtagsByGroup(groupName)!!
        //Declare a list to save tags in general
        val inGeneralTags: MutableList<String> = mutableListOf()
        for(data in dataInGeneral){
            //Get name from list
            val tagName:String = data.tag?.replace('-', ' ')!!
            // Add the tag name into the list
            inGeneralTags.add(tagName)
        }
        //Declare a list to save tags by group name
        val inGroupTags: MutableList<String> = mutableListOf()
        for(data in dataInGroup){
            //Get the name from list
            val tagName:String  = data.tag?.replace('-',' ')!!
            //Add the tag name into the list
            inGroupTags.add(tagName)
        }
        //Add tag names into chip groups
        chipHelperForAssignedToGeneral?.addAllChips(inGeneralTags)
        chipHelperForAsssignedToGroup?.addAllChips(inGroupTags)
        //Set onclick listeners for three buttons
        generalToGroup?.setOnClickListener {
            //Pass chips from the second chip group into the first
            passToGroup(chipHelperForAsssignedToGroup!!, chipHelperForAssignedToGeneral!!)
        }

        groupToGeneral?.setOnClickListener {
            //Pass chips from the first chip group into the second
            passToGeneral(chipHelperForAsssignedToGroup!!, chipHelperForAssignedToGeneral!!)
        }

        setHashtagsToGroup?.setOnClickListener {
            //Update hashtags into the chips groups into their respective groups
            updateHashtagsGroupDB(groupName,chipHelperForAsssignedToGroup!!, chipHelperForAssignedToGeneral!!)
            linkGroupToHashtagsDialog?.dismiss()
        }
    }

    private fun openWarningDialog(position: Int, groupText: String){
        //Open dialog
        deleteWarningDialog?.show()
        //Get dialog widgets
        val warningLabel:TextView? = deleteWarningDialog?.findViewById(R.id.DeleteWarningLabel)
        val accept: Button? = deleteWarningDialog?.findViewById(R.id.AcceptDeleteWarning)
        val decline: Button? = deleteWarningDialog?.findViewById(R.id.DeclineDeleteWarning)
        //Change label title
        warningLabel?.text = "Want to delete?"
        //Set click listeners from both buttons
        accept?.setOnClickListener {
            //Delete group from database
            deleteGroupFromDB(position, groupText)
             deleteWarningDialog?.dismiss()
        }

        decline?.setOnClickListener {
             deleteWarningDialog?.dismiss()
        }

    }

    //Internal helper functions

    private fun saveGroupNameFromUpdateGroupDialog(newGroup: String, oldGroup: String, textView: TextView, inputGroup:EditText){
        //check if new group name is not null or empty
        if(!newGroup.isNullOrEmpty()) {
            //Turn black spaces into a character
            val newGroupName = newGroup.replace(' ', '-')
            val oldGroupName = oldGroup.replace(' ','-')
            //Check if another query element hasn't the same group name
            if (!(dbHelper?.groupExistenceChecker(newGroupName)!! && oldGroupName.compareTo(newGroupName)!=0)){
                //Update new group into the db
                updateGroupIntoDB(oldGroupName,newGroupName)
                //Assign the new group name into the textview
                textView.text = newGroupName
                //Clear the text in input group
                inputGroup?.text.clear()
                //Close window
                updateGroupDialog?.dismiss()
            }else{
                Toast.makeText(this, "Another has the same group name!!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "The Group is empty!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun passToGeneral(toGroup:ChipHelper, generalGroup:ChipHelper){
        //Get selected chips in chips group
        val selectedInGroup:MutableList<Chip> = toGroup.getSelectedChips()

        for(selectedChip in selectedInGroup){
            //Get title in tag name in group
            val tagName = selectedChip.text.toString()
            //Add tag names into the first chip group
            generalGroup.addChip(tagName)
            //Remove chips from from the second group by name
            toGroup.removeChip(tagName)
        }
    }

    private fun passToGroup(toGroup:ChipHelper, generalGroup:ChipHelper){
        //Get selected chips in chips group
        val selectedInGeneral: MutableList<Chip> = generalGroup.getSelectedChips()

        for(selectedChip in selectedInGeneral){
            //Get title in tag name in group
            val tagName = selectedChip.text.toString()
            //Add tag names into the second chip group
            toGroup.addChip(tagName)
            //remove chips from fist group by name
            generalGroup.removeChip(tagName)
        }
    }

    //DB
    private fun updateGroupIntoDB(oldGroup:String, newGroup:String){
        //Add new group name into the db
        dbHelper?.updateDataInTableGroups(GroupsTableModel(null,newGroup),oldGroup)
        Toast.makeText(this, "Group updated!!", Toast.LENGTH_SHORT).show()
    }

    private fun updateHashtagsGroupDB(assignedGroupName:String, toGroup:ChipHelper, generalGroup: ChipHelper){
        //Get data from table groups using the group name from selected query
        val assignedGroupData: MutableList<GroupsTableModel>? = dbHelper?.getDataInTableGroups(assignedGroupName)
        //Get group id in group data
        val assignedGroupId:Int? = assignedGroupData?.get(0)?.groupID!!
        //Set a variable for general/main group id
        val generalGroupID:Int  = 0
        //Create a list for second chips group
        val groupChips:MutableList<Chip> = toGroup.getChips()
        //Create a list for first chips group
        val generalChips:MutableList<Chip> = generalGroup.getChips()

        for (chip in generalChips){
            //Get tag name from chip list
            val tagName = chip.text.toString().replace(" ", "-")
            //Change group of hashtags to General/Main
            dbHelper?.updateDataInTableHashtags(HashtagsTableModel(null,null,null, generalGroupID), tagName)
        }

        for (chip in groupChips){
            //Get tag name from chip list
            val tagName = chip.text.toString().replace(" ","-")
            //Change group of hashtags to assigned group
            dbHelper?.updateDataInTableHashtags(HashtagsTableModel(null,null,null,assignedGroupId),tagName)
        }
        Toast.makeText(this, "Group assignation updated!!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteGroupFromDB(position:Int, groupText:String){
        //val adapter: DynamicGroupQueryAdapter? = groupsList?.adapter as DynamicGroupQueryAdapter
        //Turn blank spaces into a character
        val groupName = groupText.replace(' ', '-')
        //Delete group by groupName
        dbHelper?.deleteDataInTableGroups(groupName)
        //Remove item from current position
        dynamicQueryInstantiatedAdapter?.deleteItem(position)
        Toast.makeText(this, "Group deleted!!", Toast.LENGTH_SHORT).show()
    }

    //Interface methods for query elements
    override fun OnClickGroupLabel(groupText: String, textView: TextView) {
        openUpdateGroupDialog(groupText, textView)
    }

    override fun OnClickLinkGroupToHashtags(groupText: String) {
        openLinkGroupToHashtagsDialog(groupText)
    }

    override fun OnClickDeleteGroup(groupName: String, position: Int) {
        openWarningDialog(position, groupName)
    }



}