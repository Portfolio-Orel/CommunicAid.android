package com.orelzman.mymessages.util.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.orelzman.mymessages.R

fun Context.copyToClipboard(label: String, value: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, value)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
}