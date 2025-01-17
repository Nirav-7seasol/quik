package com.moez.QKSMS.feature.permission

import dagger.Module
import dagger.Provides
import dev.octoshrimpy.quik.injection.scope.ActivityScope
import io.reactivex.disposables.CompositeDisposable

@Module
class PermissionActivityModule {

    @Provides
    @ActivityScope
    fun provideCompositeDisposableLifecycle(): CompositeDisposable = CompositeDisposable()

}