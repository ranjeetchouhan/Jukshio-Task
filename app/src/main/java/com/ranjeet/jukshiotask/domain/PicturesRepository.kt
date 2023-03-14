package com.ranjeet.jukshio_task.domain

import android.net.Uri

interface PicturesRepository {

    suspend fun uploadPicture(photosUri: ArrayList<Uri>): List<String>
    suspend fun addDetailsFirestore(photosUrl: List<String>): ArrayList<String>
}