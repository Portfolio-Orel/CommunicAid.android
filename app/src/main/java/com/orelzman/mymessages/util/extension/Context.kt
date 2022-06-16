package com.orelzman.mymessages.util.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun Context.copyToClipboard(label: String, value: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, value)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
}