package com.example.hasgtagmanager.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionsHelper(val context: Context, val activity:Activity):AppCompatActivity() {

    //Declare de readable code
    private val readableCode:Int = 943
    //Declare de writable code
    private val writableCode:Int = 944

    fun permissionAreAccepted():Boolean{
        //Get readable permissions
        val readablePermissions = ContextCompat.checkSelfPermission(context!!,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        //Get writable permissions
        val writablePermissions = ContextCompat.checkSelfPermission(context!!,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //Declare a boolean
        var permissionStatus:Boolean = true
        //Check if writable an readable permissions are not accepted
        if(readablePermissions != PackageManager.PERMISSION_GRANTED && writablePermissions != PackageManager.PERMISSION_GRANTED){
            permissionStatus = false
        }
        return permissionStatus
    }

    fun requestSDPermissions(){
        //Check if the permissions are not accepted
        if(!permissionAreAccepted()){
            //Throw the dialogs to request permissions
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),readableCode)
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),writableCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Check if the request code is the readable code
        if(requestCode == readableCode ){
           //Check if the permissions are no granted
           if(!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
              Toast.makeText(context, "Readable permissions are rejected!!", Toast.LENGTH_SHORT).show()
           }
        }

        //Check if the request code is the writable code
        if(requestCode == writableCode ){
            //Check if the permissions are not granted
            if(!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(context, "Writable permissions are rejected!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}