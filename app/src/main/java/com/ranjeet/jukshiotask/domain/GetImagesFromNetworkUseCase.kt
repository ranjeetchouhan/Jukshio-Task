package com.ranjeet.jukshio_task.domain

class GetImagesFromNetworkUseCase(
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke() = databaseRepository.getImagesFromFirestore()
}