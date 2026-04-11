package com.example.soen345_winter2026.confirmation

interface EmailService {
    fun send(message: ConfirmationMessage, callback: (Boolean, String?) -> Unit)
}
