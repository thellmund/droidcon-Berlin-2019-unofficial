package com.hellmund.droidcon2019.ui.speakers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hellmund.droidcon2019.data.model.Speaker
import com.hellmund.droidcon2019.util.plusAssign
import io.reactivex.disposables.CompositeDisposable

class SpeakersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SpeakersRepository(application.applicationContext)

    private val _speakers = MutableLiveData<List<Speaker>>()
    val speakers: LiveData<List<Speaker>> = _speakers

    private val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable += repository
            .getSpeakers()
            .subscribe(_speakers::postValue) // Error handling: ¯\_(ツ)_/¯
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
