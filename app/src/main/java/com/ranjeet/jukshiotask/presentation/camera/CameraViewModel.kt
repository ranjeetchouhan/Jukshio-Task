package com.ranjeet.jukshiotask.presentation.camera

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjeet.jukshio_task.domain.GetImagesFromNetworkUseCase
import com.ranjeet.jukshio_task.domain.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val useCase: UseCases
) : ViewModel(){
    var _photosUrl = MutableLiveData<List<String>>()
    val photosUrl: LiveData<List<String>>
        get() = _photosUrl

    fun uploadPicture(photosUri: ArrayList<Uri>) {
        viewModelScope.launch {
            val list = useCase.uploadPictureUseCase.invoke(photosUri)
            addDetails(list)
        }
    }

    fun addDetails(photosUrl: List<String>) {
        viewModelScope.launch {
            _photosUrl.value = useCase.addDetailsIntoFirestore.invoke(photosUrl)
        }
    }
}
