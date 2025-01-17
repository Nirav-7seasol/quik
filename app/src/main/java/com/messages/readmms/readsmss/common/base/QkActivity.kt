
package com.messages.readmms.readsmss.common.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.widget.QkTextView
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

abstract class QkActivity : LocalizationActivity() {

    val toolbar: Toolbar? by lazy { findViewById(R.id.toolbar) }

    protected val menu: Subject<Menu> = BehaviorSubject.create()
    protected val toolbarTitle by lazy { findViewById<QkTextView>(R.id.toolbarTitle) }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setSupportActionBar(toolbar)
        title = title // The title may have been set before layout inflation
    }

    fun setToolbar() {
        setSupportActionBar(toolbar)
        title = title // The title may have been set before layout inflation
    }

    override fun setTitle(titleId: Int) {
        title = getString(titleId)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        toolbarTitle?.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        if (menu != null) {
            this.menu.onNext(menu)
        }
        return result
    }

    protected open fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

}