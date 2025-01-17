
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
