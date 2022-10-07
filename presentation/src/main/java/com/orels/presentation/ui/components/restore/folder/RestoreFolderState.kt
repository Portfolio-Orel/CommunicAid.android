package com.orels.presentation.ui.components.restore.folder

import com.orels.domain.model.entities.Folder
import com.orels.presentation.ui.components.util.CRUDResult

/**
 * @author Orel Zilberman
 * 08/09/2022
 */
data class RestoreFolderState(
    val deletedFolders: List<Folder> = emptyList(),
    val result: CRUDResult<Folder>? = null
)