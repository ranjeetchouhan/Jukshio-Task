package com.ranjeet.jukshio_task.data.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.ranjeet.jukshio_task.domain.entity.ImageDataModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUtils @Inject constructor(
    private val storageRef: StorageReference,
    private val imagesRef: CollectionReference,
) {

    suspend fun uploadPhotos(photosUri: ArrayList<Uri>): List<String> {
        val photosUrls = ArrayList<String>()
        val uploadedPhotosUriLink = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            (photosUri.indices).map { index ->
                async(Dispatchers.IO) {
                    uploadPhoto(storageRef, photosUri[index])
                }
            }
        }.awaitAll()

        uploadedPhotosUriLink.forEach { photoUriLink -> photosUrls.add(photoUriLink.toString()) }
        return photosUrls
    }

    private suspend fun uploadPhoto(storageRef: StorageReference, photoUri: Uri): Uri {
        val fileName = UUID.randomUUID().toString()
        val fileUri = photoUri

        return storageRef.child(fileName)
            .putFile(fileUri)
            .await()
            .storage
            .downloadUrl
            .await()
    }

    suspend fun addDetails(photosUrl: kotlin.collections.List<String>): ArrayList<String> {
        val details = ArrayList<String>()
        val uploadedDetails = withContext(Dispatchers.IO) {
            var map = HashMap<String, String>()
            async {
                val imageDataModel = ImageDataModel(photosUrl.get(0), photosUrl.get(1))
                imagesRef.document().set(imageDataModel)
            }
        }.await()

        details.add(uploadedDetails.toString())
        return details
    }

    suspend fun getImagesDetails(): ArrayList<ImageDataModel> = suspendCoroutine { continuation ->
        imagesRef.get().addOnSuccessListener { querySnapshots ->
            if (!querySnapshots.isEmpty) {
                val imagesList = ArrayList<ImageDataModel>()
                if (querySnapshots != null) {
                    val documents = querySnapshots.documents
                    documents.forEach {document ->
                        document.toObject(ImageDataModel::class.java)?.let { it ->
                            imagesList.add(
                                it
                            )
                        }
                    }
                }
                continuation.resume(imagesList)
            }
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}
