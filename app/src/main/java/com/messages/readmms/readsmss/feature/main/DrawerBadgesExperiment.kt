
package com.messages.readmms.readsmss.feature.main

import android.content.Context
import com.messages.readmms.readsmss.experiment.Experiment
import com.messages.readmms.readsmss.experiment.Variant
import javax.inject.Inject

class DrawerBadgesExperiment @Inject constructor(
    context: Context,
) : Experiment<Boolean>(context) {

    override val key: String = "Drawer Badges"

    override val variants: List<Variant<Boolean>> = listOf(
            Variant("variant_a", false),
            Variant("variant_b", true))

    override val default: Boolean = false

    override val qualifies: Boolean = true

}