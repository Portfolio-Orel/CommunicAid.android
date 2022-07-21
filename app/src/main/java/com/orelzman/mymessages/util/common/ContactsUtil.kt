package com.orelzman.mymessages.util.common

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.orelzman.mymessages.util.extension.log

object ContactsUtil {
    fun getContactName(number: String, context: Context): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        var contactName = number
        try {
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0)
                }
                cursor.close()
            }
            return contactName
        } catch(exception: IllegalArgumentException) {
            // Number not found
            return number
        } catch (exception: Exception) {
            exception.log()
            return number
        }
    }
}