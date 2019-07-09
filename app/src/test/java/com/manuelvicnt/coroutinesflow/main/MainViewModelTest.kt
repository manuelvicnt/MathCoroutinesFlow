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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.manuelvicnt.coroutinesflow.LiveDataTestUtil
import com.manuelvicnt.coroutinesflow.MainCoroutineRule
import com.manuelvicnt.coroutinesflow.fibonacci.FakeColdFibonacciProducer
import com.manuelvicnt.coroutinesflow.fibonacci.impl.NeverEndingFibonacciProducer
import com.manuelvicnt.coroutinesflow.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Tests Cold fibonacci Live Data`() = mainCoroutineRule.runBlocking {
        val subject = MainViewModel(FakeColdFibonacciProducer(), NeverEndingFibonacciProducer())

        assertEquals(2, LiveDataTestUtil.getValue(subject.coldFibonacci))
        assertEquals(3, LiveDataTestUtil.getValue(subject.coldFibonacci))
    }

    @Test
    fun `Tests Never ending fibonacci Live Data`() = mainCoroutineRule.runBlocking {
        val fibonacci = NeverEndingFibonacciProducer()
        val subject = MainViewModel(FakeColdFibonacciProducer(), fibonacci)

        try {
            fibonacci.startNeverEndingFibonacci(mainCoroutineRule.testDispatcher)
            assertEquals(2, subject.neverEndingFibonacci.receive())
            mainCoroutineRule.testDispatcher.advanceTimeBy(3000)
            assertEquals(3, subject.neverEndingFibonacci.receive())
        } finally {
            fibonacci.stopNeverEndingFibonacci()
        }
    }
}
