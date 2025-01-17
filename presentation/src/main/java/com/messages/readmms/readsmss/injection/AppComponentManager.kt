
package com.messages.readmms.readsmss.injection

import com.messages.readmms.readsmss.common.App

internal lateinit var appComponent: AppComponent
    private set

internal object AppComponentManager {

    fun init(application: App) {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .build()
    }

}