package com.example.hasgtagmanager.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.data_models.GroupsTableModel
import com.example.hasgtagmanager.utils.DatabaseHelper

class AddGroupActivity:AppCompatActivity() {

    //Widgets
    private var inputGroup:EditText? = null
    private var saveGroup:ImageButton? = null
    //DB
    private var dbHelper:DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)
        initComponents()
        widgetsConfiguration()
        events()
    }

    private fun initComponents(){
        //Widgets
        inputGroup = findViewById(R.id.InputGroup)
        saveGroup = findViewById(R.id.SaveGroup)
        //DB
        dbHelper = DatabaseHelper(this)
    }

    private fun widgetsConfiguration(){

    }

    private fun events(){
        //Set onclick listener
        saveGroup?.setOnClickListener{
            //Get group name
            val groupName:String = inputGroup?.text.toString()
            //Save group name
            saveGroupName(groupName)
        }
    }

    //Internal helper methods
    private fun saveGroupName(groupText:String){
        //Check if group text is not null or empty
        if (!groupText.isNullOrEmpty()){
            //Turn blank spaces into a character
            var groupName = groupText.replace(' ','-')
            //Check if group doesn't exist
            if(!dbHelper?.groupExistenceChecker(groupName)!!){
                //Write group into DB
                writeGroupIntoDB(groupName)
                inputGroup?.text?.clear()
            }else{
                Toast.makeText(this, "Group already exist!!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Group is empty!!", Toast.LENGTH_SHORT).show()
        }
    }


    //DB
    private fun writeGroupIntoDB(groupName:String){
        //Save groupName into db
        dbHelper?.insertDataInTableGroups(GroupsTableModel(null, groupName))
        Toast.makeText(this, "Group saved!!", Toast.LENGTH_SHORT).show()
    }

}
