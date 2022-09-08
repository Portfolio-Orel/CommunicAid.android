package com.orelzman.mymessages.presentation.components.restore.folder

import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.presentation.components.util.CRUDResult

/**
 * @author Orel Zilberman
 * 08/09/2022
 */
data class RestoreFolderState(
    val deletedFolders: List<Folder> = emptyList(),
    val result: CRUDResult<Folder>? = null
)