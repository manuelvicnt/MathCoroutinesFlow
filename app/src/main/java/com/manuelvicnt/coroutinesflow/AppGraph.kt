package com.manuelvicnt.coroutinesflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.manuelvicnt.coroutinesflow.AppDIGraph.coldFibonacciProducer
import com.manuelvicnt.coroutinesflow.AppDIGraph.neverEndingFibonacciProducer
import com.manuelvicnt.coroutinesflow.AppDIGraph.userRepository
import com.manuelvicnt.coroutinesflow.fibonacci.impl.ColdFibonacciProducer
import com.manuelvicnt.coroutinesflow.fibonacci.impl.NeverEndingFibonacciProducer
import com.manuelvicnt.coroutinesflow.main.MainViewModel
import com.manuelvicnt.coroutinesflow.user.impl.UserRepository
import com.manuelvicnt.coroutinesflow.user.UserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.IllegalStateException

@ExperimentalCoroutinesApi
object AppDIGraph {
    val coldFibonacciProducer by lazy { ColdFibonacciProducer() }
    val neverEndingFibonacciProducer by lazy { NeverEndingFibonacciProducer() }
    val userRepository by lazy { UserRepository() }
}

@ExperimentalCoroutinesApi
object ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == MainViewModel::class.java) {
            return MainViewModel(coldFibonacciProducer, neverEndingFibonacciProducer) as T
        } else if (modelClass == UserViewModel::class.java) {
            return UserViewModel(userRepository) as T
        }
        throw IllegalStateException()
    }
}
