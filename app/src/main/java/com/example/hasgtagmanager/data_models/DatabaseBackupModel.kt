package com.example.hasgtagmanager.data_models
import java.io.Serializable;

data class DatabaseBackupModel(val hashtagsModel:MutableList<HashtagsTableModel>,
                               val groupModel: MutableList<GroupsTableModel>): Serializable
