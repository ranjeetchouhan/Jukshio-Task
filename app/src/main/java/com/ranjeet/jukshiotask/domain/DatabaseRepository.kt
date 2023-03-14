package com.ranjeet.jukshio_task.domain

import com.ranjeet.jukshio_task.domain.entity.ImageDataModel

interface DatabaseRepository {

    suspend fun getImagesFromFirestore(): ArrayList<ImageDataModel>
}
