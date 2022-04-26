package com.orelzman.mymessages.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder.encode

object Whatsapp {
    fun Context.sendWhatsapp(phoneNumber: String, body: String): Boolean {
        val i = Intent(Intent.ACTION_SEND)
        try {
            val url =
                "https://api.whatsapp.com/send?phone=${
                    changePhoneNumberPrefix(
                        convertContactNumberToNumbersOnly(phoneNumber)
                    )
                }&text=" + encode(
                    body,
                    "UTF-8"
                )
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(i)
            } catch (exception: Exception) {
                val intentWebWhatsapp = Intent(Intent.ACTION_VIEW)
                intentWebWhatsapp.data = Uri.parse(url)
                intentWebWhatsapp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentWebWhatsapp)
            }
            return true
        } catch (e: Exception) {
            throw e
        }
    }

    private fun changePhoneNumberPrefix(number: String): String {
        var editedNumber = number
        if (editedNumber[0] == '0') {
            editedNumber = editedNumber.removePrefix("0")
            editedNumber = "+972$editedNumber"
        }
        return editedNumber
    }

    private fun convertContactNumberToNumbersOnly(number: String): String {
        var editedNumber: String = number
        if (editedNumber[1] == '9') {//+972
            editedNumber = '0' + editedNumber.substring(4, editedNumber.length)
        }
        editedNumber = editedNumber.replace(
            '(',
            ' '
        ).replace(
            ')',
            ' '
        ).replace(
            '-',
            ' '
        ).replace(
            "\\s".toRegex(),
            ""
        )
        return editedNumber
    }
}