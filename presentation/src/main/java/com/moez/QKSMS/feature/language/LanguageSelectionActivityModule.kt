package com.moez.QKSMS.feature.language

import dagger.Module
import dagger.Provides
import dev.octoshrimpy.quik.injection.scope.ActivityScope
import io.reactivex.disposables.CompositeDisposable

@Module
class LanguageSelectionActivityModule {

    @Provides
    @ActivityScope
    fun provideCompositeDiposableLifecycle(): CompositeDisposable = CompositeDisposable()

}