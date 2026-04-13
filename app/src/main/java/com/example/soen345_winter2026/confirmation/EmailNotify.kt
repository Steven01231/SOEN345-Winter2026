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
import javax.net.ssl.SSLContext
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

                // GMS Conscrypt registers as "GmsCore_OpenSSL" and hijacks the default
                // SSLSocketFactory. Firebase's earlier TLS usage caches a GmsCore socket
                // factory, so removeProvider alone isn't enough — android-mail still gets
                // a GmsCore socket whose handshake dies ("Socket is closed"). Build a
                // fresh SSLContext from the platform provider and hand android-mail its
                // socket factory explicitly.
                try { Security.removeProvider("GmsCore_OpenSSL") } catch (_: Exception) {}

                val sslContext = SSLContext.getInstance("TLSv1.2")
                sslContext.init(null, null, null)
                val socketFactory = sslContext.socketFactory

                // Use SSL on port 465 (implicit TLS) — no mid-connection socket upgrade.
                val props = Properties().apply {
                    put("mail.smtps.auth", "true")
                    put("mail.smtps.host", "smtp.gmail.com")
                    put("mail.smtps.port", "465")
                    put("mail.smtps.ssl.protocols", "TLSv1.2 TLSv1.3")
                    put("mail.smtps.ssl.socketFactory", socketFactory)
                    put("mail.smtps.ssl.socketFactory.fallback", "false")
                    put("mail.smtps.socketFactory.fallback", "false")
                    put("mail.smtps.connectiontimeout", "15000")
                    put("mail.smtps.timeout", "15000")
                    put("mail.smtps.writetimeout", "15000")
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

                val transport = session.getTransport("smtps")
                transport.connect(senderEmail, senderPassword)
                transport.sendMessage(mimeMessage, mimeMessage.allRecipients)
                transport.close()

                Log.d(TAG, "Email sent successfully to ${message.recipientEmail}")
                callback(true, null)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send email", e)
                callback(false, e.message)
            }
        }.start()
    }
}
