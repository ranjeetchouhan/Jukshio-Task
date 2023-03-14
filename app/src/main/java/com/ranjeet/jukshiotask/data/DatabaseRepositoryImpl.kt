package com.ranjeet.jukshio_task.data

import com.ranjeet.jukshio_task.data.utils.FirebaseUtils
import com.ranjeet.jukshio_task.domain.DatabaseRepository
import com.ranjeet.jukshio_task.domain.entity.ImageDataModel

class DatabaseRepositoryImpl(
    private val firebaseUtils: FirebaseUtils,
) : DatabaseRepository {
    override suspend fun getImagesFromFirestore(): ArrayList<ImageDataModel> {
        return firebaseUtils.getImagesDetails()
    }
}
