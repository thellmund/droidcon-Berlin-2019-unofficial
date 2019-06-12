package com.hellmund.droidcon2019.util

import android.app.Activity
import android.content.res.Configuration
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.O
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, callback: (T) -> Unit) {
    observe(owner, Observer<T> { value -> callback(value) })
}

fun Activity.toggleLightDarkSystemWindows() {
    val view = window.decorView
    val mode = view.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    if (mode == Configuration.UI_MODE_NIGHT_YES) {
        if (SDK_INT >= M) {
            var flags = window.decorView.systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (SDK_INT >= O) {
                flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            window.decorView.systemUiVisibility = flags
        }
    }
}
