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
package com.messages.readmms.readsmss.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.base.QkPresenter
import com.messages.readmms.readsmss.common.base.QkThemedActivity
import com.messages.readmms.readsmss.common.base.QkViewContract
import com.messages.readmms.readsmss.common.widget.QkTextView

abstract class QkControllerWithBinding<ViewContract : QkViewContract<State>, State : Any, Presenter : QkPresenter<ViewContract, State>, Binding : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> Binding
) : LifecycleController() {

    abstract var presenter: Presenter

    private val appCompatActivity: AppCompatActivity?
        get() = activity as? AppCompatActivity

    protected val themedActivity: QkThemedActivity?
        get() = activity as? QkThemedActivity

    private val toolbar by lazy { view?.findViewById<Toolbar>(R.id.toolbar) }
    private val toolbarTitle by lazy { view?.findViewById<QkTextView>(R.id.toolbarTitle) }

    lateinit var binding: Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        binding = bindingInflater(inflater, container, false)
        onViewCreated()
        return binding.root
    }

    open fun onViewCreated() {
    }

    fun setTitle(@StringRes titleId: Int) {
        setTitle(activity?.getString(titleId))
    }

    fun setTitle(title: CharSequence?) {
        activity?.title = title
        toolbarTitle?.text = title
    }

    fun showBackButton(show: Boolean) {
        appCompatActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

}
