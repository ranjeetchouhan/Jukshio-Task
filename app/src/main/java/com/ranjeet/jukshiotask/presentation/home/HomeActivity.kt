package com.ranjeet.jukshiotask.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ranjeet.jukshio_task.domain.entity.ImageDataModel
import com.ranjeet.jukshio_task.presentation.adapter.ImageAdapter
import com.ranjeet.jukshiotask.R
import com.ranjeet.jukshiotask.databinding.ActivityHomeBinding
import com.ranjeet.jukshiotask.presentation.camera.CameraView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), OnClickListener {
    var binding: ActivityHomeBinding? = null
    var imageList = ArrayList<ImageDataModel>()
    lateinit var adapter: ImageAdapter
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        initialize()

        if (imageList.isEmpty()) {
            binding?.tvNoPictures?.isVisible = true
            binding?.rvImagelist?.isVisible = false
        }

        viewModel.imageList.observe(this, {
            if (it.size > 0) {
                imageList.addAll(it)
                adapter.notifyDataSetChanged()
                binding?.tvNoPictures?.isVisible = false
                binding?.rvImagelist?.isVisible = true
            } else {
                binding?.tvNoPictures?.isVisible = true
                binding?.rvImagelist?.isVisible = false
            }
        })
    }
    private fun initialize() {
        binding?.let {
            it.fabIcCamera.setOnClickListener(this@HomeActivity)
            it.rvImagelist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            adapter = ImageAdapter(imageList)
            it.rvImagelist.adapter = adapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_ic_camera -> {
                goToCameraActivity()
            }
        }
    }

    private fun goToCameraActivity() {
        var intent = Intent(this, CameraView::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        imageList.clear()
        viewModel.getImages()
    }
}