
package com.messages.readmms.readsmss.feature.gallery

import com.messages.readmms.readsmss.common.base.QkView
import com.messages.readmms.readsmss.model.MmsPart
import io.reactivex.Observable

interface GalleryView : QkView<GalleryState> {

    fun optionsItemSelected(): Observable<Int>
    fun screenTouched(): Observable<*>
    fun pageChanged(): Observable<MmsPart>

    fun requestStoragePermission()

}