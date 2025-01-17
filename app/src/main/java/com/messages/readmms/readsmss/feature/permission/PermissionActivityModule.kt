package com.messages.readmms.readsmss.feature.permission

import dagger.Module
import dagger.Provides
import com.messages.readmms.readsmss.injection.scope.ActivityScope
import io.reactivex.disposables.CompositeDisposable

@Module
class PermissionActivityModule {

    @Provides
    @ActivityScope
    fun provideCompositeDisposableLifecycle(): CompositeDisposable = CompositeDisposable()

}