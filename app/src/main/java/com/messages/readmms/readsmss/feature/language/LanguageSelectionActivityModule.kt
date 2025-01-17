package com.messages.readmms.readsmss.feature.language

import dagger.Module
import dagger.Provides
import com.messages.readmms.readsmss.injection.scope.ActivityScope
import io.reactivex.disposables.CompositeDisposable

@Module
class LanguageSelectionActivityModule {

    @Provides
    @ActivityScope
    fun provideCompositeDiposableLifecycle(): CompositeDisposable = CompositeDisposable()

}