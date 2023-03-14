package com.ranjeet.jukshio_task.data

import android.net.Uri
import com.ranjeet.jukshio_task.data.utils.FirebaseUtils
import com.ranjeet.jukshio_task.domain.PicturesRepository
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PicturesRepositoryImpl @Inject constructor(
    private val firebaseUtils: FirebaseUtils,
) : PicturesRepository {

    override suspend fun uploadPicture(photosUri: ArrayList<Uri>): List<String> {
        return firebaseUtils.uploadPhotos(photosUri)
    }

    override suspend fun addDetailsFirestore(photosUrl: kotlin.collections.List<String>): ArrayList<String> {
        return firebaseUtils.addDetails(photosUrl)
    }
}
