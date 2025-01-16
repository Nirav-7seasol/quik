/*
 * Copyright (C) 2017 Moez Bhatti <innovate.bhatti@gmail.com>
 *
 * This file is part of replify.
 *
 * replify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * replify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with replify.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.octoshrimpy.quik.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.common.util.extensions.resolveThemeAttribute
import dev.octoshrimpy.quik.common.util.extensions.resolveThemeColorStateList
import dev.octoshrimpy.quik.common.util.extensions.setVisible
import dev.octoshrimpy.quik.common.util.extensions.viewBinding
import dev.octoshrimpy.quik.databinding.PreferenceViewBinding
import dev.octoshrimpy.quik.injection.appComponent

class PreferenceViewWithBinding @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

    var title: String? = null
        set(value) {
            field = value

            if (isInEditMode) {
                findViewById<TextView>(R.id.titleView).text = value
            } else {
                binding.titleView.text = value
            }
        }

    var summary: String? = null
        set(value) {
            field = value


            if (isInEditMode) {
                findViewById<TextView>(R.id.summaryView).run {
                    text = value
                    setVisible(value?.isNotEmpty() == true)
                }
            } else {
                binding.summaryView.text = value
                binding.summaryView.setVisible(value?.isNotEmpty() == true)
            }
        }

    val binding: PreferenceViewBinding = viewBinding(PreferenceViewBinding::inflate)

    init {
        if (!isInEditMode) {
            appComponent.inject(this)
        }

        setBackgroundResource(context.resolveThemeAttribute(android.R.attr.selectableItemBackground))
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        binding.icon.imageTintList = context.resolveThemeColorStateList(android.R.attr.textColorSecondary)

        context.obtainStyledAttributes(attrs, R.styleable.PreferenceView).run {
            title = getString(R.styleable.PreferenceView_title)
            summary = getString(R.styleable.PreferenceView_summary)

            // If there's a custom view used for the preference's widget, inflate it
            getResourceId(R.styleable.PreferenceView_widget, -1).takeIf { it != -1 }?.let { id ->
                View.inflate(context, id, binding.widgetFrame)
            }

            // If an icon is being used, set up the icon view
            getResourceId(R.styleable.PreferenceView_icon, -1).takeIf { it != -1 }?.let { id ->
                binding.icon.setVisible(true)
                binding.icon.setImageResource(id)
            }

            recycle()
        }
    }

    fun <T : View> widget(): T {
        return binding.widgetFrame.getChildAt(0) as T
    }

}
