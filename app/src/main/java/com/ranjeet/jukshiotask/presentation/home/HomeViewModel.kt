package com.ranjeet.jukshiotask.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjeet.jukshio_task.domain.GetImagesFromNetworkUseCase
import com.ranjeet.jukshio_task.domain.entity.ImageDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getImagesFromNetworkUseCase: GetImagesFromNetworkUseCase,
) : ViewModel() {

    private val _imageList = MutableLiveData<ArrayList<ImageDataModel>>()
    val imageList: LiveData<ArrayList<ImageDataModel>>
        get() = _imageList

    fun getImages() = viewModelScope.launch {
        _imageList.value?.clear()
        _imageList.value = getImagesFromNetworkUseCase.invoke()
    }
}