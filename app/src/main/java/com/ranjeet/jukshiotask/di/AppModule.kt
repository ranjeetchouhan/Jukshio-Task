package com.ranjeet.jukshio_task.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.ranjeet.jukshio_task.data.DatabaseRepositoryImpl
import com.ranjeet.jukshio_task.data.PicturesRepositoryImpl
import com.ranjeet.jukshio_task.data.utils.FirebaseUtils
import com.ranjeet.jukshio_task.domain.*
import com.ranjeet.jukshio_task.utils.Constant.PICTURES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideImagesRef() = Firebase.firestore.collection(PICTURES)

    @Provides
    fun provideStorageRef() = Firebase.storage.reference

    @Provides
    fun provideFirebaseUtils(storageReference: StorageReference, imagesRef: CollectionReference): FirebaseUtils = FirebaseUtils(storageReference, imagesRef)

    @Provides
    fun providePictureRepository(firebaseUtils: FirebaseUtils): PicturesRepository = PicturesRepositoryImpl(firebaseUtils)

    @Provides
    fun provideDatabaseRepository(firebaseUtils: FirebaseUtils): DatabaseRepository = DatabaseRepositoryImpl(firebaseUtils)

    @Provides
    fun provideUseCases(
        repo: PicturesRepository,
    ) = UseCases(
        uploadPictureUseCase = UploadPictureUseCase(repo),
        addDetailsIntoFirestore = AddDetailsIntoFirestore(repo),
    )

    @Provides
    fun provideGetImagesFromNetworkUseCase(
        databaseRepository: DatabaseRepository,
    ) = GetImagesFromNetworkUseCase(databaseRepository)
}
