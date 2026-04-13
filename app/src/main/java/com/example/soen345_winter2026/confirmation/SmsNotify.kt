package com.example.soen345_winter2026.confirmation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

object SmsNotify {

    fun send(context: Context, message: ConfirmationMessage, callback: (Boolean, String?) -> Unit) {
        if (message.recipientPhone.isEmpty()) {
            callback(false, "No phone number on file")
            return
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            callback(false, "SMS permission not granted")
            return
        }

        try {
            @Suppress("DEPRECATION") // hides deprecation warnings
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(message.body)
            smsManager.sendMultipartTextMessage(
                message.recipientPhone,
                null,
                parts, // send in parts if message is too long
                null,
                null
            )
            callback(true, null)
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
}