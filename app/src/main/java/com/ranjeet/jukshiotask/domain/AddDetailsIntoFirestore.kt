package com.ranjeet.jukshio_task.domain

class AddDetailsIntoFirestore(
    private val picturesRepository: PicturesRepository,
) {

    suspend operator fun invoke(photosUrl: List<String>) = picturesRepository.addDetailsFirestore(photosUrl = photosUrl)
}
