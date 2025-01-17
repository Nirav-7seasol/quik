
package com.messages.readmms.readsmss.feature.notificationprefs

import androidx.lifecycle.ViewModel
import com.messages.readmms.readsmss.injection.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named

@Module
class NotificationPrefsActivityModule {

    @Provides
    @Named("threadId")
    fun provideThreadId(activity: NotificationPrefsActivity): Long = activity.intent.extras?.getLong("threadId") ?: 0L

    @Provides
    @IntoMap
    @ViewModelKey(NotificationPrefsViewModel::class)
    fun provideNotificationPrefsViewModel(viewModel: NotificationPrefsViewModel): ViewModel = viewModel

}