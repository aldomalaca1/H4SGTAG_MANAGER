# H4SGTAG_MANAGER  

CRUD that saves hashtags for social media made using android studio and kotlin

## Table of contents

* [General info](#general-info)
* [Technologies](#technologies)
* [Download](#download)
* [Setup](#setup)
* [User_manual](#user_manual)


## General info
   This project is a CRUD that saves hashtags into a database
## Technologies
* android studio 2021.3.1 Patch 4
* gson:2.8.5
* kotlin
* sqlite

## Download
Link soon

## Setup
[Installing apk in android](https://www.youtube.com/watch?v=vK_jgnEe_8w)

## User manual
* [Main_menu_options](#main_menu_options)
* [Functions_description](#functions_description)
* [Details](#details)

## Main menu options
 
 ### Add Hashtags 
 You can register your hashtags in it.
 
 ### Hashtags Entries 
 You can query by groups and modify the data related to hashtags and delete the data.
 
 ### Add group 
 You can register groups here.
 
 ### Groups entries 
 All groups are shown here; However, you can also assign each group to hashtags as well as delete each group.
 
 ### Backup data
 You can create a backup for all your data here, but it requires that the permissions have been accepted first, 
 if not, this option will be blocked, not to mention that once the application is deleted, the backup will also be deleted too.

## Functions description

### Add Hashtags 
The first thing that you will see is going to be a window with a data entry that says <your hashtags> and a save button. 
You must to add a name and give enter from the virtual keyboard, do this with each of the hashtags that you want to register.
Once you have registered what you need, you need to click the save button, this will bring up a sub-window that will ask you for a tag, 
write a tag of your choice and simply click on save.
You will return to the previous window, where you will simply give it to save. 
This should save the record within the system database. 
You can check if the registration was correct by pressing the menu option "Hashtags entries".
  
 ### Hashtags Entries: 
 The top has a selector which will have several options depending on the number of groups that have registered, however, 
 it has a default option which is "ALL", as its name indicates, its main function is to show all hashtags registered in the database regardless of their association, 
 which is the first thing that is usually shown when you enter this view.
 If you have entered data in the hashtags registry, a small rectangular-shaped letter should appear which should show the records by the tag you entered to identify the hashtags. 
 This card has different functions apart from just showing you the entries, since these allow you to modify, preview and delete the data.
 For example, you can change the assigned tag simply by clicking on it, and writing the new tag in the window that will appear,
 you can also preview the registered tags, you just have to click on the eye symbol, which will show a window that should show the hashtags related to the registry, 
 which will also allow you to copy them to temporary memory to use them elsewhere. 
 Other things that it allows you to do, is to modify the hashtags by pressing the pencil button and doing practically the same thing as in the hashtag registry, 
 only with a modifiable preview and without having to put another tag and permanently delete the data from the registry; 
 to this degree the option you have for that is extremely obvious so is pointless explain it.
 
  ### Add Group 
  This is a easy one, you unly have to grite a group name and save it pressing the button, that's it.

  ### Groups Entries 
  This option is similar to Hashtags entries. The cards contain the group name that you can change doing exactly the same as in the previous case,
  also you can associate a grop using the a > z option, a window should appear with two entries, the first one shows you the tags that are in the general hidden group, 
  and the other option on the right shows all the entries related to the group, you just have to press each tag and use the button that aim to the group to which you are going to associate each element, 
  remember that you have to press save for the changes to be registered. The last option of the card delete the group.
  
  ### Backup data 
  Fist you must to accept de permissions that the apk ask to accept if you deccline it, the option get blocked until you accept the permissions.
  Is simple, the view has two buttons the first is the backup button that creates a json will all data in database(Yes, yes, I know it is not encrypted, 
  but I doubt that a hacker would care to look at your phone just to delete the hashtags that you are going to make public in your social networks anyway),
  and the other option is Restore data, tha the name suggest restore all previous saved data. 

  ## Details
  * Is this a program made by a newbie to made portafolio so the program can have some bugs that maybe a professional program don't has. 
    However I try to correct the most bugs as soon as posible I noticed. 
    
  * Blank spaces doen't exist in the register, the program is designed to sustitue all blanks with a '-', all the blanks tha you saw is because the program show the entries
    with not blanks but internaly de database has all registers with blanks like this 'tag-space' instead 'tag space', this is to prevent issues with the database, you should be preocupied?
    not really, But if you found that all data dat you put a '-' on purpose with a blank in the entries, is because of this, that's include the hashtags, even when you copied the hashtags all '-'
    will changed by a ' ', so if must to use another character for separation like '_' for example.
    
   * If you have some issue with my program, you write your problem in the the comments section.

