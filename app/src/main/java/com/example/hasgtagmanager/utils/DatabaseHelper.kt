package com.example.hasgtagmanager.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.hasgtagmanager.data_models.GroupsTableModel
import com.example.hasgtagmanager.data_models.HashtagsTableModel
import kotlin.math.abs


class DatabaseHelper(context: Context): SQLiteOpenHelper(context,databaseName, null, databaseVersion) {

    companion object{

        //Database basic data
        private const val databaseVersion: Int = 1
        private const val databaseName: String = "hashtag_data.db"

        //Tables

        //<---------------Headers---------------------------->
        private const val hashtagsTableH : String = "Hashtags"
        private const val groupsTableH: String = "Groups"

        //<----------------Atrributes----------------------->

        //Table hashtags

        private const val hashtagID:String = "hashtagID"
        private const val tag:String = "tag"
        private const val content:String = "content"
        private const val faction:String = "faction"

        //Table Groups

        private const val groupID:String = "groupID"
        private const val name:String =  "name"

        //Create Tables

        private const val hashtagsTableC: String = "CREATE TABLE IF NOT EXISTS $hashtagsTableH (" +
                                                  "$hashtagID INTEGER NOT NULL UNIQUE," +
                                                  "$tag VARCHAR(140) NOT NULL UNIQUE," +
                                                  "$content TEXT NOT NULL," +
                                                  "$faction INTEGER," +
                                                  "PRIMARY KEY($hashtagID AUTOINCREMENT)," +
                                                  "FOREIGN KEY($faction) REFERENCES $groupsTableH ($groupID));"

        private const val groupTableC: String = "CREATE TABLE IF NOT EXISTS $groupsTableH (" +
                                                "$groupID INTEGER NOT NULL UNIQUE," +
                                                "$name VARCHAR(140) UNIQUE," +
                                                "PRIMARY KEY($groupID AUTOINCREMENT));"

        //Query

        const val hashtagsTableQ:String = "SELECT * FROM $hashtagsTableH"
        const val groupsTableQ:String = "SELECT * FROM $groupsTableH"

        //DROP TABLE

        private const val hashtagsTableD: String = "DROP TABLE IF EXISTS $hashtagsTableH"
        private const val groupsTableD: String = "DROP TABLE IF EXISTS $groupsTableH"

        //SELECT
        private const val hashtagsTableS = "SELECT * FROM $hashtagsTableH"
        private const val groupsTableS = "SELECT * FROM $groupsTableH"
        private const val hashtagsByGroupQ = "SELECT $tag,$content FROM $hashtagsTableH INNER JOIN " +
                                             "$groupsTableH ON $faction = $groupID"

        //Truncate
        private const val hashtagsTableT:String = "DELETE FROM $hashtagsTableH"
        private const val groupsTableT:String = "DELETE FROM $groupsTableH"
    }

    override fun onCreate(db: SQLiteDatabase?) {
         //Create tables
         createTables(db!!)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        //Drop tables
        dropTables(db)
        //Create tables
        onCreate(db!!)
    }

    private fun  createTables(db:SQLiteDatabase){
           try {
               //Create groups table
               db.execSQL(groupTableC)
               //Create hashtag table
               db.execSQL(hashtagsTableC)
           }catch (e: SQLException){e.printStackTrace()}
    }

    fun insertDefaultData(){
        try{
          //Insert General/Main group into table groups
          insertDataInTableGroups(GroupsTableModel(0,"General"))
        }catch (e:SQLException){e.printStackTrace()}
    }

    private fun dropTables(db: SQLiteDatabase?){
        try {
            //Drop groups table
            db?.execSQL(groupsTableD)
            //Drop hashtags table
            db?.execSQL(hashtagsTableD)
        }catch (e: SQLException){e.printStackTrace()}
    }

    fun deleteAllDataInAllTables(){
        //Get database
        val db = writableDatabase
        try{
            //Delete data in groups table
            db.execSQL(groupsTableT)
            //Delete data in hashtags table
            db.execSQL(hashtagsTableT)

        }catch (e:SQLException){e.printStackTrace()}
        db.close()
    }

    fun insertDataInTableHashtags(value: HashtagsTableModel): Long{
        //Set a long variable
        var succes: Long? = 0
        //Get database
        val db = this.writableDatabase
        try {
            //Get values that are not null
            val hashtagsData: ContentValues = tableHashtagValues(value)
            //Check if the array has data
            if (hashtagsData.size() > 0) {
                //Insert data intro hashtag table
                succes = db.insert(hashtagsTableH, null, hashtagsData)
            }
        }catch (e:SQLException){e.printStackTrace()}
        db.close()
        return succes!!
    }

    fun insertDataInTableGroups(value: GroupsTableModel): Long{
        //Set a long variable
        var succes: Long? = 0
        //Get database
        val db = this.writableDatabase
        try {
            //Get values that are not null
            val groupData: ContentValues = tableGroupsValues(value)
            //Check if the array has data
            if (groupData.size() > 0) {
                //Insert data into groups table
                succes = db.insert(groupsTableH, null, groupData)
            }

        }catch (e:SQLException){e.printStackTrace()}
        db.close()
        return succes!!
    }

    fun getAllDataInTableHashtags(): MutableList<HashtagsTableModel>{
        //Get database
        val db = readableDatabase
        //Declare hashtags data list
        var outputHashtagsData: MutableList<HashtagsTableModel>? = null
        //Declare a cursor
        var cursor: Cursor? = null
        try{
           //Get all data from hashtags table
           cursor = db.rawQuery(hashtagsTableS, null)
        }catch(e : SQLException){
            e.printStackTrace()
            //Execute the sql same sqlcode
            db.execSQL(hashtagsTableS)
            return ArrayList()
        }
        //Get data from cursor
        outputHashtagsData = tableHashtagsCursorOutput(cursor)
        db.close()
        return outputHashtagsData
    }

    fun getDataInTableHashtags(tagName:String): MutableList<HashtagsTableModel>{
        //Get database
        val db = readableDatabase
        //Declare hashtags data list
        var outputHashtagsData: MutableList<HashtagsTableModel>? = null
        //Declare a cursor
        var cursor: Cursor? = null
        //Create a query using the general query for hashtags
        var query: String = "$hashtagsTableQ WHERE $tag = '$tagName'"
        try{
            //Execute the sql query
            cursor = db.rawQuery(query, null)
        }catch(e : SQLException){
            e.printStackTrace()
            //Execute the same sql query
            db.execSQL(query)
            return ArrayList()
        }
        //Get data from cursor
        outputHashtagsData = tableHashtagsCursorOutput(cursor!!)
        db.close()
        return outputHashtagsData
    }

    fun getHashtagsByGroup(groupName:String): MutableList<HashtagsTableModel>{
        //Get database
        val db = readableDatabase
        //Declare hashtags data list
        var outputHashtagsData:MutableList<HashtagsTableModel>? = null
        //Declare a cursor
        var cursor:Cursor? = null
        //Create a query using the general query for hashtags
        val query: String = "$hashtagsByGroupQ WHERE $name = '$groupName'"
        try {
            //Execute the sql query
            cursor = db.rawQuery(query, null)
        }catch (e:SQLException){
            e.printStackTrace()
            //Execute the same sql query
            db.execSQL(query)
            return ArrayList()
        }
        //Get data from cursor
        outputHashtagsData = tableHashtagsCursorOutput(cursor)
        return  outputHashtagsData
    }

    fun getAllDataInTableGroups(): MutableList<GroupsTableModel>{
        //Get database
        val db = readableDatabase
        //Declare hashtags data list
        var outputGroupsData: MutableList<GroupsTableModel>? = null
        //Declare a cursor
        var cursor: Cursor? = null
        try{
            //Execute the sql query
            cursor = db.rawQuery(groupsTableS, null)
        }catch(e : SQLException){
            e.printStackTrace()
            //Execute the same sql query
            db.execSQL(groupsTableS)
            return ArrayList()
        }
        //Get data from cursor
        outputGroupsData = tableGroupsCursorOutput(cursor)
        db.close()
        return outputGroupsData
    }

    fun getDataInTableGroups(groupName:String): MutableList<GroupsTableModel>{
        //Get database
        val db = readableDatabase
        //Declare groups data list
        var outputGroupsData: MutableList<GroupsTableModel>? = null
        //Create a query using groups general query
        var query:String = "$groupsTableS WHERE $name='$groupName'"
        //Declare a cursor
        var cursor: Cursor? = null
        try{
            //Execute the sql query
            cursor = db.rawQuery(query, null)
        }catch(e : SQLException){
            e.printStackTrace()
            //Execute the same sql query
            db.execSQL(groupsTableS)
            return ArrayList()
        }
        //Get data from cursor
        outputGroupsData = tableGroupsCursorOutput(cursor)
        db.close()
        return outputGroupsData
    }

    fun updateDataInTableHashtags(value: HashtagsTableModel, tagName: String){
        //Get Database
        val db = writableDatabase
        try {
             //Get data from cursor
             val hashtagsDataOutput: ContentValues = tableHashtagValues(value)
             //Check if hashtags list has data
             if (hashtagsDataOutput.size() > 0) {
                 //Update table hashtags data
                 db.update(hashtagsTableH, hashtagsDataOutput, "$tag=?", arrayOf(tagName))
             }
        }catch (e:SQLException){e.printStackTrace()}
        db.close()
    }

    fun updateDataInTableGroups(value: GroupsTableModel, groupName:String){
        //Get database
        val db = writableDatabase
        try {
            //Get data with not null values
            val groupsDataOutput: ContentValues = tableGroupsValues(value)
            //Check if groups list has data
            if (groupsDataOutput.size() > 0) {
                //Update data in table groups
                db.update(groupsTableH, groupsDataOutput, "$name=?", arrayOf(groupName))
            }
        }catch (e: SQLException){e.printStackTrace()}
        db.close()
    }

    fun deleteDataInTableHashtags(tagName: String){
        //Get database
        val db = writableDatabase
        try {
            //Delete data from hashtags table
            db.delete(hashtagsTableH, "$tag = ?", arrayOf(tagName))
        }catch (e:SQLException){e.printStackTrace()}
        db.close()
    }


    fun deleteDataInTableGroups(groupName: String){
        //Declare general group id
        val generalGroupID: Int = 0
        //Get all data from table hashtags
        val hashtagsDataOutput: MutableList<HashtagsTableModel> = getAllDataInTableHashtags()
        //Get data in table groups
        val groupsDataOutput: MutableList<GroupsTableModel> = getDataInTableGroups(groupName)
        //Get group id
        val groupID:Int = groupsDataOutput[0].groupID!!

        for (data in hashtagsDataOutput) {
            //Check if factionID is different to general group ID and are registered in the current group
            if (data.faction != generalGroupID && data.faction == groupID) {
                //Get tag name from data
                var tagName = data.tag
                //Change faction to general group id
                updateDataInTableHashtags(
                    HashtagsTableModel(null, null, null, generalGroupID),
                    tagName!!
                )
            }
        }

        //Get database
        val db = writableDatabase

        try {
            //Delete data from groups table
            db.delete(groupsTableH, "$name = ?", arrayOf(groupName))
        }catch (e:SQLException){
            e.printStackTrace()
        }
        db.close()

    }

    //Null data filters
    private fun tableHashtagValues(value: HashtagsTableModel): ContentValues{
        //Declare content values
        val data:ContentValues = ContentValues()
        //Check if data in hashtags table model is not null
        if (value.hashtagID != null){data.put(hashtagID, value.hashtagID)}
        if(value.tag != null){data.put(tag, value.tag)}
        if(value.content != null){data.put(content, value.content)}
        if(value.faction != null){data.put(faction, value.faction)}
        return data
    }

    private fun tableGroupsValues(value: GroupsTableModel): ContentValues{
        //Declare content values
        val data:ContentValues = ContentValues()
        //Check if data in groups model is not null
        if(value.groupID != null){data.put(groupID, value?.groupID!!)}
        if(value.name != null){data.put(name, value?.name!!)}
        return data
    }

    //Cursor processors
    private fun tableHashtagsCursorOutput(cursor: Cursor): MutableList<HashtagsTableModel>{
        //Declare hashtags data list
        val hashtagsList: MutableList<HashtagsTableModel> = mutableListOf()
        if(cursor!=null){
            if (cursor.moveToFirst()){
                do {
                    //Get all values from table hashtags content in the cursor
                    val hashtagIDValue:Int = cursor.getInt(abs(cursor.getColumnIndex(hashtagID)))
                    val tagName:String = cursor.getString(abs(cursor.getColumnIndex(tag)))
                    val contentData:String = cursor.getString(abs(cursor.getColumnIndex(content)))
                    val factionValue:Int = cursor.getInt(abs(cursor.getColumnIndex(faction)))
                    //Add data into list
                    hashtagsList.add(HashtagsTableModel(hashtagIDValue, tagName, contentData, factionValue))
                }while (cursor.moveToNext())

            }
        }
        return  hashtagsList!!
    }

    private fun tableGroupsCursorOutput(cursor: Cursor): MutableList<GroupsTableModel>{
        //Declare groups list
        val groupsList:MutableList<GroupsTableModel> = mutableListOf()
        if (cursor.moveToFirst()){
            do {
                //Get all data from groups table model
                val groupIDValue:Int = cursor.getInt(abs(cursor.getColumnIndex(groupID)))
                val groupName:String = cursor.getString(abs(cursor.getColumnIndex(name)))
                //Add data into list
                groupsList.add(GroupsTableModel(groupIDValue, groupName))
            }while (cursor.moveToNext())
        }
        return  groupsList
    }

    //Data checkers
    fun tagExistenceChecker(tagName: String): Boolean {
        //Get database
        val db = readableDatabase
        //Create a sql query for the length
        val query: String = "SELECT COUNT($tag) as Length FROM $hashtagsTableH WHERE $tag = '$tagName'"
        //Execute query
        val cursor: Cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        //Get length from query
        val colLength:Int = cursor.getInt(abs(cursor.getColumnIndex("Length")))
        //Return true if the length is more than zero
        return colLength > 0
    }

    fun groupExistenceChecker(groupName: String): Boolean {
        //Get database
        val db = readableDatabase
        //Create a sql query for the length
        val query: String = "SELECT COUNT($name) as Length FROM $groupsTableH WHERE $name = '$groupName'"
        //Execute query
        val cursor: Cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        //Get length from query
        val colLength:Int = cursor.getInt(abs(cursor.getColumnIndex("Length")))
        //Return true fi the length is more than zero
        return colLength > 0
    }

    fun isMainGroup(groupName: String): Boolean{
        //Get database
        val db = readableDatabase
        //Create sql query for the length
        val query: String = "SELECT COUNT($name) as Length FROM $groupsTableH WHERE " +
                                 "$groupID = 0 AND $name = '$groupName'"
        //Execute query
        val cursor: Cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        //Get the length of the query
        val colLength:Int = cursor.getInt(abs(cursor.getColumnIndex("Length")))
        db.close()
        //Return true if the length is more than zero
        return colLength > 0
    }

}