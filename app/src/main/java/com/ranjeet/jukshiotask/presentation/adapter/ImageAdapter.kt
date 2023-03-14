package com.ranjeet.jukshio_task.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ranjeet.jukshio_task.domain.entity.ImageDataModel
import com.ranjeet.jukshiotask.R
import com.ranjeet.jukshiotask.databinding.ItemviewImageBinding

class ImageAdapter(
    var imageList: List<ImageDataModel>,
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemviewImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = DataBindingUtil.inflate<ItemviewImageBinding>(
            LayoutInflater.from(parent.context),
            R.layout.itemview_image,
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(imageList.get(position)) {
                binding.ivFirstImage.load(this.firstImage)
                binding.ivSecondImage.load(this.secondImage)

                binding.tvFirstUrl.setText("$firstImage")
                binding.tvSecondUrl.setText("$secondImage")
            }
        }
    }
}
