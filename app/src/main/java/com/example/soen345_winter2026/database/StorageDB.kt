package com.example.soen345_winter2026.database

import com.google.firebase.storage.FirebaseStorage

class StorageDB {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun uploadEventPageImage(imageData: ByteArray, eventId: String, callback: (Boolean, String?) -> Unit) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("event_images/$eventId.jpg")

        val uploadTask = imageRef.putBytes(imageData)

        uploadTask.addOnSuccessListener {
            // Once the upload is successful, request the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                // Return true and the actual URL
                callback(true, downloadUrl)
            }.addOnFailureListener { exception ->
                // Failed to retrieve the URL specifically
                callback(false, "Upload succeeded but failed to get URL: ${exception.message}")
            }
        }.addOnFailureListener { exception ->
            // Failed the initial upload
            callback(false, exception.message)
        }
    }
}