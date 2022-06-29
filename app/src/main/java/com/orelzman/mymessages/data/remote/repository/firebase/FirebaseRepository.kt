package com.orelzman.mymessages.data.remote.repository.firebase

import com.google.firebase.firestore.*
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.data.dto.Message
import com.orelzman.mymessages.data.remote.repository.FolderExistsException
import com.orelzman.mymessages.data.remote.repository.Repository
import com.orelzman.mymessages.util.extension.Log
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirebaseRepository @Inject constructor() : Repository {
    private val db = FirebaseFirestore.getInstance()

    private fun attachID(docs: List<DocumentSnapshot>): List<Map<String, Any>?> {
        val data = ArrayList<Map<String, Any>?>()
        docs.map {
            val map = it.data
            map?.set("id", it.id)
            data.add(map)
            data
        }
        return data
    }

    override val folderExistsException: FolderExistsException
        get() = FolderExistsException()

    override suspend fun getMessages(userId: String): List<Map<String, Any>?> {
        val result = Collections.Messages
            .get(userId)
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .documents
        return attachID(result)
    }

    override suspend fun getFolders(userId: String): List<Map<String, Any>?> {
        val result = Collections.Folders
            .get(userId)
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .documents
        return attachID(result)
    }

    override suspend fun saveMessage(
        userId: String,
        data: Map<String, Any>,
        folderId: String
    ): String {
        val messageDocRef = Collections.Messages
            .get(userId)
            .document()
        val folderDocRef = Collections.Folders.get(userId).document(folderId)
        val folderData =
            mapOf(FIELD_ARRAY_MESSAGES_IN_FOLDERS to FieldValue.arrayUnion(messageDocRef.id))
        db.runBatch {
            it.set(messageDocRef, data)
            it.set(folderDocRef, folderData, SetOptions.merge())
        }.await()
        return messageDocRef.id
    }

    override suspend fun saveFolder(userId: String, data: Map<String, Any>): String {
        val existFolder = Collections.Folders
            .get(userId)
            .whereEqualTo("folderTitle", data["folderTitle"])
            .get()
            .await()
        if (!existFolder.isEmpty) {
            throw folderExistsException
        }
        val docRef = Collections.Folders
            .get(userId)
            .document()
        docRef.set(data).await()
        return docRef.id
    }

    override suspend fun deleteMessage(userId: String, id: String) {
        Collections.Messages
            .get(userId)
            .document(id)
            .deleteDocument()
    }

    override suspend fun deleteFolder(userId: String, id: String) {
        Collections.Folders
            .get(userId)
            .document(id)
            .deleteDocument()
    }


    override suspend fun addPhoneCalls(userId: String, dataList: List<Map<String, Any>>) {
        val colRef = Collections.PhoneCalls
            .get(userId)
        val batch = db.batch()
        dataList.forEach {
            val docRef = colRef.document()
            batch.set(docRef, it, SetOptions.merge())
        }
        batch.commit().await()
        dataList.forEach {
            updateStatistics(userId, it)
        }
    }

    override suspend fun editMessage(
        userId: String,
        message: Message,
        oldFolderId: String,
        newFolderId: String
    ) {
        try {
            val batch = db.batch()
            Collections.Messages.get(userId, message.id).update(message.data)
            val folderColRef = Collections.Folders.get(userId)
            Log.vCustom("MyMessages")
            val folderWithMessageDocument =
                folderColRef.document(oldFolderId).get().await()
            val newFolderDocument = folderColRef.document(newFolderId).get().await()
            val oldFolder = Folder(folderWithMessageDocument.data, folderWithMessageDocument.id)
            val newFolder = Folder(newFolderDocument.data, newFolderDocument.id)
            val oldFoldersMessages = ArrayList(oldFolder.messageIds)
            val newFoldersMessages = ArrayList(newFolder.messageIds)
            oldFoldersMessages.remove(message.id)
            newFoldersMessages.add(message.id)
            oldFolder.messageIds = oldFoldersMessages
            newFolder.messageIds = newFoldersMessages
            batch.update(folderColRef.document(newFolder.id), newFolder.data)
            batch.update(folderColRef.document(oldFolder.id), oldFolder.data)
            batch.commit().await()
        } catch (ex: Exception) {
            Log.vCustom(ex.stackTraceToString())
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun updateStatistics(userId: String, data: Map<String, Any>) {
        val cal = Calendar.getInstance()
        val startDate =
            Date(data["startDate"] as String)
        val number = data["phoneNumber"] as String
        cal.time = startDate
        val formattedDate =
            "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
        val messagesSent = (data["messagesSent"] as? List<String> ?: emptyList())
        updateCallsPerDayStatistics(userId, startDate, formattedDate)
        if (messagesSent.isNotEmpty()) {
            updatePotentialClientsStatistics(userId, number, startDate, formattedDate)
            updateMessagesSentPerHour(
                userId,
                messagesSent,
                startDate,
                formattedDate,
            )
        }
    }

    private suspend fun updateCallsPerDayStatistics(
        userId: String,
        startDate: Date,
        formattedDate: String,
    ) {
        val col = Collections.PhoneCallsPerDay.get(userId)
        val res = col.whereEqualTo("formattedDate", formattedDate).get().await()
        if (res.isEmpty) { // First statistics update for today.
            val statisticsData = hashMapOf(
                "formattedDate" to formattedDate,
                "date" to startDate,
                "callsCount" to 1
            )
            col.document().set(statisticsData).await()
        } else {
            col.document(res.documents[0].id)
                .update("callsCount", FieldValue.increment(1)).await()
        }
    }

    private suspend fun updatePotentialClientsStatistics(
        userId: String,
        number: String,
        startDate: Date,
        formattedDate: String,
    ) {
        val col = Collections.PotentialClients.get(userId)
        val statisticsData: MutableMap<String, Any?> = mutableMapOf(
            "formattedDate" to formattedDate,
            "date" to startDate, // The actual date it is added on is the endDate
            "clientsCount" to 1,
            "phoneNumbers" to number
        )
        val potentialClientAddedRes =
            col.whereArrayContains(
                "phoneNumbers",
                number.formatNoSignsOrPrefix()
            ).get()
                .await()
        if (!potentialClientAddedRes.isEmpty) return
        val res = col.whereEqualTo("formattedDate", formattedDate).get().await()
        if (res.isEmpty) { // First statistics update for today.
            col.document().set(statisticsData).await()
        } else {
            col.document(res.documents[0].id)
                .update("clientsCount", FieldValue.increment(1)).await()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun updateMessagesSentPerHour(
        userId: String,
        messagesSent: List<String>,
        date: Date,
        formattedDate: String
    ) {
        val col = Collections.MessagesSentPerDay.get(userId)
        val data = hashMapOf(
            "date" to date,
            "formattedDate" to formattedDate,
            "messageIDs" to messagesSent,
        )
        val res = col.whereEqualTo("formattedDate", formattedDate).get().await()
        if (res.isEmpty) {
            col.document().set(data).await()
        } else {
            val messageIdsFromDB =
                res.documents[0]["messageIDs"] as? ArrayList<String> ?: ArrayList<String>()
            messageIdsFromDB.addAll(messagesSent)
            col.document(res.documents[0].id)
                .update("messageIDs", messageIdsFromDB).await()
        }
    }

    private fun Collections.get(userId: String): CollectionReference =
        if (this == Collections.Users) db.collection(value)
        else db.collection(Collections.Users.value).document(userId).collection(value)

    private fun Collections.get(userId: String, id: String): DocumentReference =
        db.collection(Collections.Users.value).document(userId).collection(value).document(id)

    companion object {
        private const val COLLECTION_MESSAGES_SENT = "messagesSent"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_FOLDERS = "folders"
        private const val COLLECTION_MESSAGES = "messages"
        private const val COLLECTION_MODULES = "modules"
        private const val COLLECTION_FILES = "files"
        private const val COLLECTION_PHONE_CALLS = "phoneCalls"
        private const val COLLECTION_COMPLETED_CALLS = "completedCalls"
        private const val COLLECTION_PHONE_CALLS_PER_DAY = "phoneCallsPerDay"
        private const val COLLECTION_MESSAGES_SENT_PER_DAY = "messagesSentPerHour"
        private const val COLLECTION_POTENTIAL_CLIENTS = "potentialClients"
        private const val COLLECTION_HIGH_POTENTIAL_CLIENTS = "highPotentialClients"
        private const val FIELD_ARRAY_MESSAGES_IN_FOLDERS = "messageIDs"
        private const val FIELD_START_DATE = "startDate"
        private const val FIELD_PHONE_CALL_MESSAGES_SENT = "messagesSent"
        private const val FIELD_PHONE_NUMBER_POTENTIAL_CLIENTS = "phoneNumber"
        private const val FIELD_PHONE_NUMBER = "phoneNumber"
    }
}

private enum class Collections(val value: String) {
    Messages("messages"),
    Folders("folders"),
    PhoneCalls("phoneCalls"),
    PhoneCallsPerDay("phoneCallsPerDay"),
    MessagesSentPerDay("messagesSentPerHour"),
    PotentialClients("potentialClients"),
    Users("users"),
}

suspend fun DocumentReference.deleteDocument(): Void =
    update("isActive", false).await()

private fun String.formatNoSignsOrPrefix() = removeNumberPrefix().removeSigns()
private fun String.removeNumberPrefix() = replace("+972", "0")
private fun String.removeSigns() = replace("\\D+", "")