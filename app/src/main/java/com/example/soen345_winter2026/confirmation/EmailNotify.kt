package com.example.soen345_winter2026.confirmation

// StrictMode overrides default android behaviour for network calls
import android.os.StrictMode
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import com.example.soen345_winter2026.BuildConfig

object EmailNotify {
// hardcode this or other storing? Repo not private
    private const val SENDER_EMAIL = BuildConfig.SENDER_EMAIL
    private const val SENDER_PASSWORD = BuildConfig.SENDER_PASSWORD
    fun send(message: ConfirmationMessage, callback: (Boolean, String?) -> Unit) {
        if (message.recipientEmail.contains("@phone.com")) {
            callback(false, "No real email address on file")
            return
        }

        // Allow network on main thread for emulator testing only
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            // smtp = simple mail transfer protocol
            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
                }
            })

            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.recipientEmail))
                subject = message.subject
                setText(message.body)
            }

            Transport.send(mimeMessage)
            callback(true, null)

        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
}