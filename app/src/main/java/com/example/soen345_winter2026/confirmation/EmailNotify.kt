package com.example.soen345_winter2026.confirmation

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

    private val senderEmail: String = BuildConfig.SENDER_EMAIL
    private val senderPassword: String = BuildConfig.SENDER_PASSWORD

    private fun hasCredentials(): Boolean {
        return senderEmail.isNotBlank() && senderPassword.isNotBlank()
    }

    override fun send(message: ConfirmationMessage, callback: (Boolean, String?) -> Unit) {
        if (!hasCredentials()) {
            callback(false, "Email credentials not configured")
            return
        }

        if (message.recipientEmail.contains("@phone.com")) {
            callback(false, "No real email address on file")
            return
        }

        Thread {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
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
                callback(true, null)
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }.start()
    }
}
