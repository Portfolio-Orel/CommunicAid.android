package com.orelzman.mymessages.data.repository.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.orelzman.mymessages.data.repository.Repository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor() : Repository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getMessages(uid: String): List<Map<String, Any>?> =
        Collections.Messages
            .get(uid)
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .documents
            .map { it.data }

    override suspend fun getFolders(uid: String): List<Map<String, Any>?> =
        Collections.Folders
            .get(uid)
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .documents
            .map { it.data }

    override suspend fun addMessage(uid: String, data: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    override suspend fun addFolder(uid: String, data: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    override suspend fun addMessagesToFolder(
        uid: String,
        folderId: String,
        messageId: List<String>
    ) {
        TODO("Not yet implemented")
    }


    private fun Collections.get(uid: String): CollectionReference =
        db.collection("users").document(uid).collection(value)
}

private enum class Collections(val value: String) {
    Messages("messages"),
    Folders("folders"),
}
