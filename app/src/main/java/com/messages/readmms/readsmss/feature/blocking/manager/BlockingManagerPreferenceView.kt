

package com.messages.readmms.readsmss.feature.blocking.manager

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.util.extensions.animateLayoutChanges
import com.messages.readmms.readsmss.common.util.extensions.resolveThemeAttribute
import com.messages.readmms.readsmss.common.util.extensions.setVisible
import kotlinx.android.synthetic.main.blocking_manager_preference_view.view.*

class BlockingManagerPreferenceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var icon: Drawable? = null
        set(value) {
            field = value

            if (isInEditMode) {
                findViewById<ImageView>(R.id.iconView).setImageDrawable(value)
            } else {
                iconView.setImageDrawable(value)
            }
        }

    var title: String? = null
        set(value) {
            field = value

            if (isInEditMode) {
                findViewById<TextView>(R.id.titleView).text = value
            } else {
                titleView.text = value
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
                summaryView.text = value
                summaryView.setVisible(value?.isNotEmpty() == true)
            }
        }

    init {
        View.inflate(context, R.layout.blocking_manager_preference_view, this)
        setBackgroundResource(context.resolveThemeAttribute(R.attr.selectableItemBackground))

        context.obtainStyledAttributes(attrs, R.styleable.BlockingManagerPreferenceView).run {
            icon = getDrawable(R.styleable.BlockingManagerPreferenceView_icon)
            title = getString(R.styleable.BlockingManagerPreferenceView_title)
            summary = getString(R.styleable.BlockingManagerPreferenceView_summary)

            // If there's a custom view used for the preference's widget, inflate it
            getResourceId(R.styleable.BlockingManagerPreferenceView_widget, -1).takeIf { it != -1 }?.let { id ->
                View.inflate(context, id, widgetFrame)
            }

            recycle()
        }
    }
}
