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

package com.manuelvicnt.coroutinesflow.fibonacci.impl

import com.manuelvicnt.coroutinesflow.MainCoroutineRule
import com.manuelvicnt.coroutinesflow.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NeverEndingFibonacciProducerTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Never ending Fibonacci doesn't stop when called multiple times`() = mainCoroutineRule.runBlocking {
        val subject = NeverEndingFibonacciProducer()
        subject.startNeverEndingFibonacci(mainCoroutineRule.testDispatcher)
        val elements = subject.fibonacci().take(2).toList()

        assertEquals(2, elements[0])
        assertEquals(3, elements[1])

        subject.stopNeverEndingFibonacci()
    }
}
