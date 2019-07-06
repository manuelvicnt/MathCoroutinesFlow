package com.manuelvicnt.coroutinesflow

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
inline fun <reified T : ViewModel> FragmentActivity.obtainViewModel(
        viewModelFactory: ViewModelProvider.Factory = ViewModelFactory
): T {
    return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
}
