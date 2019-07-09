/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
