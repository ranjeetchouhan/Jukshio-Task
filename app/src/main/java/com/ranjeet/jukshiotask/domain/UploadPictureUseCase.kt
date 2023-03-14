package com.ranjeet.jukshio_task.domain

import android.net.Uri
import java.io.File

class UploadPictureUseCase(
    private val picturesRepository: PicturesRepository
) {

    suspend operator fun invoke(photosUri: ArrayList<Uri>) = picturesRepository.uploadPicture(photosUri = photosUri)
}