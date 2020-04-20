package com.simplepos.base

import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment:Fragment() {
    lateinit var rootView: View
    abstract fun setAllListeners()
    abstract fun setAllObservers()
    abstract fun setInitialView()
}