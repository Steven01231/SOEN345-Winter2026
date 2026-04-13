package com.example.soen345_winter2026.confirmation

import android.util.Log
import java.security.Security
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import com.example.soen345_winter2026.BuildConfig

object EmailNotify : EmailService {

    private const val TAG = "EmailNotify"
    private val senderEmail: String = BuildConfig.SENDER_EMAIL
    private val senderPassword: String = BuildConfig.SENDER_PASSWORD

    private fun hasCredentials(): Boolean {
        return senderEmail.isNotBlank() && senderPassword.isNotBlank()
    }

    override fun send(message: ConfirmationMessage, callback: (Boolean, String?) -> Unit) {
        if (!hasCredentials()) {
            Log.w(TAG, "Email credentials not configured — skipping email send")
            callback(false, "Email credentials not configured")
            return
        }

        if (message.recipientEmail.contains("@phone.com")) {
            callback(false, "No real email address on file")
            return
        }

        Thread {
            try {
                Log.d(TAG, "Attempting to send email to ${message.recipientEmail}")

                // Remove Conscrypt as the default SSL provider — it conflicts
                // with JavaMail's TLS handshake on Android
                try {
                    Security.removeProvider("GmsCore_OpenSSL")
                } catch (_: Exception) {}

                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                    put("mail.smtp.ssl.trust", "smtp.gmail.com")
                    put("mail.smtp.ssl.protocols", "TLSv1.2")
                    put("mail.smtp.connectiontimeout", "15000")
                    put("mail.smtp.timeout", "15000")
                    put("mail.smtp.writetimeout", "15000")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, senderPassword)
                    }
                })

                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.recipientEmail))
                    subject = message.subject
                    setText(message.body)
                }

                Transport.send(mimeMessage)

                Log.d(TAG, "Email sent successfully to ${message.recipientEmail}")
                callback(true, null)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send email", e)
                callback(false, e.message)
            }
        }.start()
    }
}
