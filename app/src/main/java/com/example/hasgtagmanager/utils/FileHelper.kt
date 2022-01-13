package com.example.hasgtagmanager.utils

import android.content.Context
import android.os.Environment
import android.util.JsonReader
import android.util.Log
import com.example.hasgtagmanager.data_models.DatabaseBackupModel
import com.example.hasgtagmanager.data_models.HashtagsTableModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import kotlin.reflect.typeOf


class FileHelper(val context: Context) {

    //Declare the name of the file
    private val fileName:String = "HashtagsDataBackup.json"
    //Declare the folder name
    private val folderName = "HashtagsBackup"
    //Declare a Gson
    private var gson:Gson? = null

    init {
        //Set gson
        gson = Gson()
    }

    fun saveFile(backupModel:DatabaseBackupModel){
        //Get the sd file
        val sdFile = getSDFile()
        //Delete current content of sd file
        sdFile.delete()
        //Create a json string using the  database backup model
        val jsonBackup:String? = gson?.toJson(backupModel as DatabaseBackupModel)
        //Add the json into the file
        sdFile.appendText(jsonBackup!!)
    }

    fun getFile(): DatabaseBackupModel? {
        //Get sd file
        val sdFile = getSDFile()
        //Get data in file
        val file = BufferedReader(InputStreamReader(FileInputStream(sdFile)))
        //Turn file intro a string
        var backupText:String = file.use(BufferedReader::readText)
        //Turn string intro database backup model
        val outputBackupData:DatabaseBackupModel? = gson?.fromJson<DatabaseBackupModel>(backupText, object : TypeToken<DatabaseBackupModel>() {}.type)
        return outputBackupData
    }

    fun checkIfSDIsAvailable():Boolean{
        //Get sd current state
        val state:String = Environment.getExternalStorageState()
        //Declare a boolean init by a false statement
        var available:Boolean = false
        //Check if the data is mounted and is readable or is a known memory
        if(state == Environment.MEDIA_MOUNTED && state != Environment.MEDIA_MOUNTED_READ_ONLY){
            available = true
        }else if(state == Environment.MEDIA_MOUNTED && state != Environment.MEDIA_UNKNOWN){
            available = false
        }
        return available
    }

    private fun getSDFile():File{
        //Get sd path string
        val sdPath:String? = context.getExternalFilesDir(null)?.absolutePath
        //Create a folder for sd
        val myFolder = File(sdPath,folderName)
        //Get folder dir
        if (!myFolder.exists()){myFolder.mkdir()}
        return File(myFolder,fileName)
    }

}