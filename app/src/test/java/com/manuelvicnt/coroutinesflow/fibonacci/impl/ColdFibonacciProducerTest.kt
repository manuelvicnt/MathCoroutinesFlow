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
import com.manuelvicnt.coroutinesflow.fibonacci.impl.ColdFibonacciProducer
import com.manuelvicnt.coroutinesflow.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ColdFibonacciProducerTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Fibonacci Flow starts again when it's called multiple times`() = mainCoroutineRule.runBlocking {
        val subject = ColdFibonacciProducer()

        val firstExecution = subject.fibonacci().first()
        assertEquals(2, firstExecution)

        val secondExecution = subject.fibonacci().first()
        assertEquals(2, secondExecution)
    }
}
