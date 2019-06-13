package com.hellmund.droidcon2019.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Session
import com.hellmund.droidcon2019.util.plusAssign
import io.reactivex.disposables.CompositeDisposable

class DayViewModel(
    day: EventDay,
    application: Application
) : AndroidViewModel(application) {

    private val repository = EventsRepository.getInstance(application.applicationContext)

    private val _events = MutableLiveData<List<Session>>()
    val events: LiveData<List<Session>> = _events

    private val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable += repository
            .getEventsByDay(day)
            .subscribe(_events::postValue) // Error handling: ¯\_(ツ)_/¯
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    class Factory(
        private val day: EventDay,
        private val application: Application
    ): ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DayViewModel(day, application) as T
        }

    }

}
