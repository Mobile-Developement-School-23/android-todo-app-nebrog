package com.example.todoapp.data.alarm.di

import com.example.todoapp.data.alarm.DeadlinePostponeService
import com.example.todoapp.di.AlarmServiceScope
import dagger.Subcomponent

@AlarmServiceScope
@Subcomponent
interface AlarmServiceComponent {

    fun inject(service: DeadlinePostponeService)

    @Subcomponent.Factory
    interface Factory {
        fun create(): AlarmServiceComponent
    }
}
