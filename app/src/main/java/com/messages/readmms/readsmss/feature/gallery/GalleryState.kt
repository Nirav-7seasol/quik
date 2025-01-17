
package com.messages.readmms.readsmss.feature.gallery

import com.messages.readmms.readsmss.model.MmsPart
import io.realm.RealmResults

data class GalleryState(
    val navigationVisible: Boolean = true,
    val title: String? = "",
    val parts: RealmResults<MmsPart>? = null
)
