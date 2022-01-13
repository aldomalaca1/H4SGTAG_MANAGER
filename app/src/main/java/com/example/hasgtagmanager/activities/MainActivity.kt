package com.example.hasgtagmanager.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hasgtagmanager.adapters.MainMenuAdapter
import com.example.hasgtagmanager.data_models.MainMenuModel
import com.example.hasgtagmanager.Decorators.RecyclerViewVerticalSeparator
import com.example.hasgtagmanager.Interfaces.OnClickRecyclerViewItemListener
import com.example.hasgtagmanager.R
import com.example.hasgtagmanager.utils.DatabaseHelper
import com.example.hasgtagmanager.utils.PermissionsHelper

class MainActivity: AppCompatActivity(), OnClickRecyclerViewItemListener {

    //Widgets
    private var mainMenu: RecyclerView?=null
    //Adapters
    private var mainMenuAdapter : MainMenuAdapter? = null
    //Decorators
    private var separateItem: RecyclerViewVerticalSeparator? = null
    //Menu
    private var opciones: MutableList<MainMenuModel>? = arrayListOf()
    //Activities
    private var addHashtagsActivity: Intent? = null
    private var dynamicHashtagQueryActivity: Intent? = null
    private var addGroupActivity: Intent? = null
    private var dynamicGroupQueryActivity: Intent? = null
    private var dataBackupActivity: Intent? = null
    //Utils
    private var databaseHelper: DatabaseHelper? = null
    private var permissionsHelper: PermissionsHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        widgetsConfiguration()
    }

    private fun initComponents(){

        //Widgets
        mainMenu = findViewById(R.id.MainMenu)
        //Decorators
        separateItem = RecyclerViewVerticalSeparator(25)
        //Activities
        addHashtagsActivity = Intent(this, AddHashtagsActivity::class.java)
        dynamicHashtagQueryActivity = Intent(this, DynamicHashtagQueryActivity::class.java)
        addGroupActivity = Intent(this, AddGroupActivity::class.java)
        dynamicGroupQueryActivity = Intent(this, DynamicGroupQueryActivity::class.java)
        dataBackupActivity = Intent(this, DataBackupActivity::class.java)
        //Utils
        databaseHelper = DatabaseHelper(this)
        databaseHelper?.insertDefaultData()
        permissionsHelper = PermissionsHelper(this,this)
        //Menu
        fillMainMenuOptions()
        //Adapters
        mainMenuAdapter = MainMenuAdapter(opciones!!, this, this)

    }

    private fun widgetsConfiguration(){
        //Widgets
        mainMenu?.hasFixedSize()
        mainMenu?.layoutManager = LinearLayoutManager(this)
        mainMenu?.adapter = mainMenuAdapter
        mainMenu?.addItemDecoration(separateItem!!)
        //Permissions
        permissionsHelper?.requestSDPermissions()
    }

    private fun fillMainMenuOptions(){
          //Add menu options
          opciones?.add(MainMenuModel(R.drawable.addhashtagsicon, "Add hashtags") )
          opciones?.add(MainMenuModel(R.drawable.hashtagsentriesicon, "Hashtags Entries") )
          opciones?.add(MainMenuModel(R.drawable.addgroupicon, "Add group") )
          opciones?.add(MainMenuModel(R.drawable.groupsentriesicon, "Groups entries") )
          opciones?.add(MainMenuModel(R.drawable.sdbackupicon, "Backup data") )
    }

    private fun selector(option:String){
           //Menu options
           when(option){
              "Add hashtags" -> startActivity(addHashtagsActivity)
              "Hashtags Entries" -> startActivity(dynamicHashtagQueryActivity)
              "Add group" -> startActivity(addGroupActivity)
              "Groups entries" -> startActivity(dynamicGroupQueryActivity)
              "Backup data"->
                  //Check if the permissions are accepted
                  if(permissionsHelper?.permissionAreAccepted()!!)
                     startActivity(dataBackupActivity)
                  else
                      Toast.makeText(this,"Yo must accept the permissions first!!",
                                     Toast.LENGTH_SHORT).show()
           }
    }

    override fun OnClickElement(itemTitleText: String) {
        //Input selected option
        selector(itemTitleText)
    }
}