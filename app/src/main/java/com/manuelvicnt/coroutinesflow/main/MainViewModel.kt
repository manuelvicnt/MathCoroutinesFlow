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

package com.manuelvicnt.coroutinesflow.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.manuelvicnt.coroutinesflow.fibonacci.ColdFibonacci
import com.manuelvicnt.coroutinesflow.fibonacci.NeverEndingFibonacci
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class MainViewModel(
    private val coldFibonacciProducer: ColdFibonacci,
    private val neverEndingFibonacciParam: NeverEndingFibonacci
) : ViewModel() {

    /**
     * ColdFibonacci is a LiveData created with the Coroutines builder. This LiveData creates a
     * Coroutine (with its own scope) and can call suspend functions. That's why we can call
     * collect on the fibonacci flow. For every item we get, we emit it to the LiveData observer
     * using the `emit` method.
     *
     * LiveData needs to be observed to do its job. If it's not observed, it won't do anything.
     */
    val coldFibonacci = liveData {
        coldFibonacciProducer.fibonacci().collect {
            emit(it)
        }
    }

    /**
     * Every time we call this variable, we want to get a new subscription from the
     * ConflatedBroadChannel. That's why we need a custom getter.
     *
     * Without a custom setter, the first subscription we get when we first call it won't change.
     * Thus, we won't get a new subscription after a configuration change (for instance), and
     * the UI won't get updates.
     */
    val neverEndingFibonacci: ReceiveChannel<Long>
        get() = neverEndingFibonacciParam.fibonacci()
}
