package com.ranjeet.jukshio_task.domain

data class UseCases(
    val uploadPictureUseCase: UploadPictureUseCase,
    val addDetailsIntoFirestore: AddDetailsIntoFirestore,
)