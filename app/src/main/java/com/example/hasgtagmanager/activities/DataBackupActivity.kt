package com.example.hasgtagmanager.activities

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.data_models.DatabaseBackupModel
import com.example.hasgtagmanager.data_models.GroupsTableModel
import com.example.hasgtagmanager.data_models.HashtagsTableModel
import com.example.hasgtagmanager.utils.FileHelper
import com.example.hasgtagmanager.utils.DatabaseHelper
import java.io.FileNotFoundException
import java.lang.Exception

class DataBackupActivity: AppCompatActivity() {

    //Widgets
    private var saveBackup:ImageButton? = null
    private var restoreData:ImageButton? = null
    //Utils
    private var dbHelper: DatabaseHelper? = null
    private var fileHelper:FileHelper? = null
    //Dialogs
    private var restoreDataWarningDialog:Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_backup)
        initComponents()
        widgetsConfiguration()
        events()
    }

    private fun events() {
        //Set on click listener for both buttons
        saveBackup?.setOnClickListener { createBackup() }
        restoreData?.setOnClickListener { openRestoreDataWarningDialog() }
    }

    private fun initComponents() {
        //Widgets
        saveBackup = findViewById(R.id.SaveBackup)
        restoreData = findViewById(R.id.RestoreData)
        //Utils
        dbHelper = DatabaseHelper(this)
        fileHelper = FileHelper(this)
        //Dialogs
        restoreDataWarningDialog = Dialog(this)
    }

    private fun widgetsConfiguration(){
        //Dialogs
        restoreDataWarningDialog?.setContentView(R.layout.restore_data_warning_dialog)
    }

    //Dialogs
    private fun openRestoreDataWarningDialog(){
        //Open dialog
        restoreDataWarningDialog?.show()
        //Get dialog widgets
        val acceptedRestoreWarning:Button? = restoreDataWarningDialog?.findViewById(R.id.AcceptedRestoreWarning)
        val declineRestoreWarning:Button? = restoreDataWarningDialog?.findViewById(R.id.DeclineRestoreWarning)
        //Set onclick for two buttons
        acceptedRestoreWarning?.setOnClickListener {
            restoreData()
            restoreDataWarningDialog?.dismiss()
        }
        declineRestoreWarning?.setOnClickListener { restoreDataWarningDialog?.dismiss() }
    }

    //Internal helper functions
    private fun createBackup(){
        //Check if SD exist
        if(fileHelper?.checkIfSDIsAvailable()!!){
            //Get all data from database
            val hashtagsDataOutput:MutableList<HashtagsTableModel>? = dbHelper?.getAllDataInTableHashtags()
            val groupsDataOutput:MutableList<GroupsTableModel>? = dbHelper?.getAllDataInTableGroups()
            //Save file in sd
            fileHelper?.saveFile(DatabaseBackupModel(hashtagsDataOutput!!, groupsDataOutput!!))
            //Advise that the database backup was created
            Toast.makeText(this,"Backup created!!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"SD unavailable!!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun restoreData(){

        try {
            //Check if SD Is avaiable
            if (fileHelper?.checkIfSDIsAvailable()!!) {
                //Get all data from backup
                val backupDataOutput: DatabaseBackupModel? = fileHelper?.getFile()
                //Seperate data into lists
                val hashtagsDataOutput: MutableList<HashtagsTableModel>? =
                    backupDataOutput?.hashtagsModel!!
                val groupsDataOutput: MutableList<GroupsTableModel>? =
                    backupDataOutput?.groupModel!!
                //Delete data in tables
                dbHelper?.deleteAllDataInAllTables()
                //Upload data into db
                for (data in groupsDataOutput!!) {
                    dbHelper?.insertDataInTableGroups(data)
                }
                for (data in hashtagsDataOutput!!) {
                    dbHelper?.insertDataInTableHashtags(data)
                }
                //Advise that data is restored
                Toast.makeText(this, "Data is restored", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SD unavailable!!", Toast.LENGTH_SHORT).show()
            }
        }catch(e:FileNotFoundException){
            Toast.makeText(this,"Can't restore check if a backup was created!!", Toast.LENGTH_SHORT).show()
        }
    }
}