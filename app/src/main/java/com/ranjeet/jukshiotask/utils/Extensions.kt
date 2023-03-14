package com.ranjeet.jukshio_task.utils

import android.view.View

fun View.IsVisible(isVisible: Boolean) {
    if (isVisible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}
