package com.orelzman.mymessages.data.repository.firebase

import com.google.firebase.firestore.*
import com.orelzman.mymessages.data.repository.FolderExistsException
import com.orelzman.mymessages.data.repository.Repository
import com.orelzman.mymessages.data.repository.firebase.exceptions.BadStartDateFromDataException
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

    override suspend fun getMessages(uid: String): List<Map<String, Any>?> {
        val result = Collections.Messages
            .get(uid)
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .documents
        return attachID(result)
    }

    override suspend fun getFolders(uid: String): List<Map<String, Any>?> {
        val result = Collections.Folders
            .get(uid)
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .documents
        return attachID(result)
    }

    override suspend fun saveMessage(
        uid: String,
        data: Map<String, Any>,
        folderId: String
    ): String {
        val messageDocRef = Collections.Messages
            .get(uid)
            .document()
        val folderDocRef = Collections.Folders.get(uid).document(folderId)
        val folderData =
            mapOf(FIELD_ARRAY_MESSAGES_IN_FOLDERS to FieldValue.arrayUnion(messageDocRef.id))
        db.runBatch {
            it.set(messageDocRef, data)
            it.set(folderDocRef, folderData, SetOptions.merge())
        }.await()
        return messageDocRef.id
    }

    override suspend fun addFolder(uid: String, data: Map<String, Any>): String {
        val existFolder = Collections.Folders
            .get(uid)
            .whereEqualTo("folderTitle", data["folderTitle"])
            .get()
            .await()
        if (!existFolder.isEmpty) {
            throw folderExistsException
        }
        val docRef = Collections.Folders
            .get(uid)
            .document()
        docRef.set(data).await()
        return docRef.id
    }

    override suspend fun addPhoneCalls(uid: String, dataList: List<Map<String, Any>>) {
        val colRef = Collections.PhoneCalls
            .get(uid)
        val batch = db.batch()
        dataList.forEach {
            val docRef = colRef.document()
            batch.set(docRef, it, SetOptions.merge())
        }
        batch.commit().await()
        dataList.forEach {
            updateStatistics(uid, it)
        }
//            .set(SetOptions.merge())
//            .await()
    }

    private suspend fun updateStatistics(uid: String, data: Map<String, Any>) {
        try {
            val cal = Calendar.getInstance()
            cal.time = data["startDate"] as? Date ?: throw BadStartDateFromDataException()
            val formattedDate =
                "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
            updateCallsPerDayStatistics(uid, data, formattedDate)

//            if ((data["messagesSent"] as? List<String>)?.size > 0) {
//                updatePotentialClientsStatistics(uid, phoneCall, formattedDate)
//            }
//            if (phoneCall.messagesSent.size > 0) {
//                updateMessagesSentPerHour(
//                    uid,
//                    phoneCall.messagesSent,
//                    phoneCall.startDate,
//                    formattedDate,
//                )
//            }
        } catch (exception: Exception) {
//            AppUtils.reportCrash(exception)
        }
    }

    private suspend fun updateCallsPerDayStatistics(
        uid: String,
        data: Map<String, Any>,
        formattedDate: String,
    ) {
        val col = Collections.PhoneCallsPerDay.get(uid)
        val res = col.whereEqualTo("formattedDate", formattedDate).get().await()
        if (res.isEmpty) { // First statistics update for today.
            val statisticsData = hashMapOf(
                "formattedDate" to formattedDate,
                "date" to data["startDate"],
                "callsCount" to 1
            )
            col.document().set(statisticsData).await()
        } else {
            col.document(res.documents[0].id)
                .update("callsCount", FieldValue.increment(1)).await()
        }
    }

    private fun Collections.get(uid: String): CollectionReference =
        if (this == Collections.Users) db.collection(value)
        else db.collection(Collections.Users.value).document(uid).collection(value)

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
